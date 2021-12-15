/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.runner;

import org.ccs.apimaster.apifactory.mainmodule.configuration.RunConfiguration;
import org.ccs.apimaster.apifactory.mainmodule.module.HttpClientRuntimeHandler;
import org.ccs.apimaster.apifactory.supportcenter.actionee.*;
import org.ccs.apimaster.apifactory.supportcenter.enhancers.MasterExecutionReportBuilder;
import org.ccs.apimaster.apifactory.supportcenter.enhancers.MasterIoWriteBuilder;
import org.ccs.apimaster.apifactory.coreactions.executionlistener.MasterTestReportListener;
import org.ccs.apimaster.apifactory.httpclient.BasicHttpClient;
import org.ccs.apimaster.apifactory.httpclient.ssl.MasterSslTrustHttpClient;
import org.ccs.apimaster.apifactory.logger.MasterCorrelationshipLogger;
import org.ccs.apimaster.apifactory.reporter.MasterReportGenerator;
import org.ccs.apimaster.apifactory.utils.SmartUtils;
import org.ccs.apimaster.apifactory.propertymanager.MasterReportProperties;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;

import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.annotation.Annotation;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.ccs.apimaster.apifactory.supportcenter.enhancers.MasterExecutionReportBuilder.newInstance;
import static org.ccs.apimaster.apifactory.utils.RunnerUtils.getEnvSpecificConfigFile;
import static org.ccs.apimaster.apifactory.utils.RunnerUtils.handleTestCompleted;
import static java.lang.System.getProperty;

/*
@Purpose: This class manages functions for running tests in isolation
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 15/12/2021
*/
public class CCSUnitRunner extends BlockJUnit4ClassRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(CCSUnitRunner.class);

    private MasterMultiStepsScenarioRunner masterMultiStepsScenarioRunner;
    private final Class<?> testClass;
    private Injector injector;
    private SmartUtils smartUtils;
    private HostProperties hostProperties;
    private String host;
    private String context;
    private int port;
    private List<String> smartTestCaseNames = new ArrayList<>();
    private String currentTestCase;
    private MasterCorrelationshipLogger corrLogger;
    protected boolean testRunCompleted;
    protected boolean passed;

    private MasterMultiStepsScenarioRunner multiStepsRunner;

    public CCSUnitRunner(Class<?> klass) throws InitializationError {
        super(klass);

        if (Files.exists(Paths.get(MasterReportProperties.TARGET_REPORT_DIR))){
            Arrays.stream(new File(MasterReportProperties.TARGET_REPORT_DIR).listFiles()).forEach(File::delete);
        }

        this.testClass = klass;
        this.smartUtils = getInjectedSmartUtilsClass();
        this.smartTestCaseNames = getSmartChildrenList();

        this.hostProperties = testClass.getAnnotation(HostProperties.class);

        if (this.hostProperties != null) {
            this.host = hostProperties.host();
            this.port = hostProperties.port();
            this.context = hostProperties.context();
        }

        this.multiStepsRunner = createCcsMasterMultiStepRunner();
    }

    /*
      @Method: run
      @Purpose: To trigger the execution of test
      @Author: Mibin Boban, CCS Senior QAT Analyst
      @Creation: 15/12/2021
    */
    @Override
    public void run(RunNotifier notifier) {
        RunListener reportListener = createReportListener();

        LOGGER.info("[CCS Master] : System property " + MasterReportProperties.CCS_MASTER_JUNIT + "=" + getProperty(MasterReportProperties.CCS_MASTER_JUNIT));
        if (!MasterReportProperties.CHARTS_AND_CSV.equals(getProperty(MasterReportProperties.CCS_MASTER_JUNIT))) {
            notifier.addListener(reportListener);
        }

        super.run(notifier);

        handleNoRunListenerReport(reportListener);
    }

    /*
     @Method: runChild
     @Purpose: To run tests one by one
     @Author: Mibin Boban, CCS Senior QAT Analyst
     @Creation: 15/12/2021
   */
    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {

        final Description description = describeChild(method);
        JsonTestCase jsonTestCaseAnno = method.getMethod().getAnnotation(JsonTestCase.class);
        if(jsonTestCaseAnno == null){
            jsonTestCaseAnno = evalScenarioToJsonTestCase(method.getMethod().getAnnotation(CCS_Master_Scenario.class));
        }

        if (isIgnored(method)) {

            notifier.fireTestIgnored(description);

        } else if (jsonTestCaseAnno != null) {

            runUnitJsonTest(notifier, description, jsonTestCaseAnno);

        } else {
            runLeafJUnitTest(methodBlock(method), description, notifier);
        }

    }

    public List<String> getSmartTestCaseNames() {
        return smartTestCaseNames;
    }

    public String getCurrentTestCase() {
        return currentTestCase;
    }

    private MasterMultiStepsScenarioRunner getInjectedMultiStepsRunner() {
        masterMultiStepsScenarioRunner = getMainModuleInjector().getInstance(MasterMultiStepsScenarioRunner.class);
        return masterMultiStepsScenarioRunner;
    }

    /*
     @Method: getMainModuleInjector
     @Purpose: To get main module injector
     @Author: Mibin Boban, CCS Senior QAT Analyst
     @Creation: 15/12/2021
   */
    public Injector getMainModuleInjector() {

        synchronized (this) {
            final CCS_Master_Properties envAnnotation = testClass.getAnnotation(CCS_Master_Properties.class);
            String serverEnv = envAnnotation != null ? envAnnotation.value() : "config_hosts.properties";

            serverEnv = getEnvSpecificConfigFile(serverEnv, testClass);

            Class<? extends BasicHttpClient> runtimeHttpClient = createCustomHttpClientOrDefault();

            injector = Guice.createInjector(Modules.override(new RunConfiguration(serverEnv))
                    .with(
                            new HttpClientRuntimeHandler(runtimeHttpClient)
                    )
            );

            return injector;
        }
    }

    public Class<? extends BasicHttpClient> createCustomHttpClientOrDefault() {
        final UseHttpClient httpClientAnnotated = getUseHttpClient();
        return httpClientAnnotated != null ? httpClientAnnotated.value() : MasterSslTrustHttpClient.class;
    }

    public UseHttpClient getUseHttpClient() {
        return testClass.getAnnotation(UseHttpClient.class);
    }

    protected RunListener createReportListener() {
        return getMainModuleInjector().getInstance(MasterTestReportListener.class);
    }

    protected SmartUtils getInjectedSmartUtilsClass() {
        return getMainModuleInjector().getInstance(SmartUtils.class);
    }

    protected MasterReportGenerator getInjectedReportGenerator() {
        return getMainModuleInjector().getInstance(MasterReportGenerator.class);
    }

    /*
     @Method: runUnitJsonTest
     @Purpose: To run identified test
     @Author: Mibin Boban, CCS Senior QAT Analyst
     @Creation: 15/12/2021
   */
    private void runUnitJsonTest(RunNotifier notifier, Description description, JsonTestCase jsonTestCaseAnno) {
        if (jsonTestCaseAnno != null) {
            currentTestCase = jsonTestCaseAnno.value();
        }

        notifier.fireTestStarted(description);

        LOGGER.debug("### [CCS Master] : Running currentTestCase : " + currentTestCase);

        ScenarioSpec child = null;
        try {
            child = smartUtils.scenarioFileToJava(currentTestCase, ScenarioSpec.class);

            LOGGER.debug("### [CCS Master] : Found currentTestCase : -" + child);

            passed = multiStepsRunner.runScenario(child, notifier, description);

        } catch (Exception ioEx) {
            ioEx.printStackTrace();
            notifier.fireTestFailure(new Failure(description, ioEx));
        }

        testRunCompleted = true;

        if (passed) {
            LOGGER.info(String.format("\n**[CCS Master] : FINISHED executing all Steps for [%s] **.\nSteps were:%s",
                    child.getScenarioName(),
                    child.getSteps().stream()
                            .map(step -> step.getName() == null ? step.getId() : step.getName())
                            .collect(Collectors.toList())));
        }

        notifier.fireTestFinished(description);
    }

    /*
     @Method: getSmartChildrenList
     @Purpose: To get json childs
     @Author: Mibin Boban, CCS Senior QAT Analyst
     @Creation: 15/12/2021
   */
    private List<String> getSmartChildrenList() {
        List<FrameworkMethod> children = getChildren();
        children.forEach(
                frameworkMethod -> {
                    JsonTestCase jsonTestCaseAnno = frameworkMethod.getAnnotation(JsonTestCase.class);

                    if(jsonTestCaseAnno == null){
                        jsonTestCaseAnno = evalScenarioToJsonTestCase(frameworkMethod.getAnnotation(CCS_Master_Scenario.class));
                    }

                    if (jsonTestCaseAnno != null) {
                        smartTestCaseNames.add(jsonTestCaseAnno.value());
                    } else {
                        smartTestCaseNames.add(frameworkMethod.getName());
                    }
                }
        );

        return smartTestCaseNames;
    }

    /*
     @Method: createCcsMasterMultiStepRunner
     @Purpose: To handle multiple steps in a test scenario
     @Author: Mibin Boban, CCS Senior QAT Analyst
     @Creation: 15/12/2021
   */
    private MasterMultiStepsScenarioRunner createCcsMasterMultiStepRunner() {
        final MasterMultiStepsScenarioRunner multiStepsRunner = getInjectedMultiStepsRunner();

        if (hostProperties != null) {
            ((MasterMultiStepsScenarioRunnerImpl) multiStepsRunner).overrideHost(host);
            ((MasterMultiStepsScenarioRunnerImpl) multiStepsRunner).overridePort(port);
            ((MasterMultiStepsScenarioRunnerImpl) multiStepsRunner).overrideApplicationContext(context);
        }
        return multiStepsRunner;
    }

    /*
     @Method: runLeafJUnitTest
     @Purpose: To run junit tests
     @Author: Mibin Boban, CCS Senior QAT Analyst
     @Creation: 15/12/2021
   */
    private final void runLeafJUnitTest(Statement statement, Description description,
                                        RunNotifier notifier) {
        LOGGER.info("[CCS Master] : Running a pure JUnit test...");

        EachTestNotifier eachNotifier = new EachTestNotifier(notifier, description);
        eachNotifier.fireTestStarted();

        final String logPrefixRelationshipId = prepareRequestReport(description);

        try {
            statement.evaluate();
            passed = true;
            LOGGER.info("[CCS Master] : JUnit test passed = {} ", passed);

        } catch (AssumptionViolatedException e) {
            passed = false;
            LOGGER.warn("[CCS Master] : JUnit test failed due to : {},  passed = {}", e, passed);

            eachNotifier.addFailedAssumption(e);

        } catch (Throwable e) {
            passed = false;
            LOGGER.warn("[CCS Master] : JUnit test failed due to : {},  passed = {}", e, passed);

            eachNotifier.addFailure(e);

        } finally {
            LOGGER.info("JUnit test run completed. See the results in the console or log.  passed = {}", passed);
            prepareResponseReport(logPrefixRelationshipId);
            buildReportAndPrintToFile(description);

            eachNotifier.fireTestFinished();
        }
    }

    /*
     @Method: buildReportAndPrintToFile
     @Purpose: To build reports
     @Author: Mibin Boban, CCS Senior QAT Analyst
     @Creation: 15/12/2021
   */
    private void buildReportAndPrintToFile(Description description) {
        MasterExecutionReportBuilder reportResultBuilder = newInstance().loop(0).scenarioName(description.getClassName());
        reportResultBuilder.step(corrLogger.buildReportSingleStep());

        MasterIoWriteBuilder reportBuilder = MasterIoWriteBuilder.newInstance().timeStamp(LocalDateTime.now());
        reportBuilder.result(reportResultBuilder.build());
        reportBuilder.printToFile(description.getClassName() + corrLogger.getCorrelationId() + ".json");
    }

    /*
     @Method: prepareResponseReport
     @Purpose: To capture response
     @Author: Mibin Boban, CCS Senior QAT Analyst
     @Creation: 15/12/2021
   */
    private void prepareResponseReport(String logPrefixRelationshipId) {
        LocalDateTime timeNow = LocalDateTime.now();
        LOGGER.info("[CCS Master] : JUnit *responseTimeStamp:{}, \nJUnit Response:{}", timeNow, logPrefixRelationshipId);
        corrLogger.responseBuilder()
                .relationshipId(logPrefixRelationshipId)
                .responseTimeStamp(timeNow);

        corrLogger.stepOutcome(passed);
        corrLogger.buildResponseDelay();
    }

    /*
     @Method: prepareRequestReport
     @Purpose: To capture request
     @Author: Mibin Boban, CCS Senior QAT Analyst
     @Creation: 15/12/2021
   */
    private String prepareRequestReport(Description description) {
        corrLogger = MasterCorrelationshipLogger.newInstance(LOGGER);
        corrLogger.stepLoop(0);
        final String logPrefixRelationshipId = corrLogger.createRelationshipId();
        LocalDateTime timeNow = LocalDateTime.now();
        corrLogger.requestBuilder()
                .stepLoop(0)
                .relationshipId(logPrefixRelationshipId)
                .requestTimeStamp(timeNow)
                .step(description.getMethodName());
        LOGGER.info("[CCS Master] : JUnit *requestTimeStamp:{}, \nJUnit Request:{}", timeNow, logPrefixRelationshipId);
        return logPrefixRelationshipId;
    }

    protected void handleNoRunListenerReport(RunListener reportListener) {
        handleTestCompleted(reportListener, LOGGER);
    }

    private JsonTestCase evalScenarioToJsonTestCase(CCS_Master_Scenario CCSMasterScenario) {

        JsonTestCase jsonTestCase = new JsonTestCase() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return JsonTestCase.class;
            }

            @Override
            public String value() {
                return CCSMasterScenario != null? CCSMasterScenario.value(): null;
            }
        };

        return jsonTestCase.value() == null ? null : jsonTestCase;
    }


}
