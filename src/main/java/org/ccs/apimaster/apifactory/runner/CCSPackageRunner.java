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
import org.ccs.apimaster.apifactory.coreactions.executionlistener.MasterTestReportListener;
import org.ccs.apimaster.apifactory.httpclient.BasicHttpClient;
import org.ccs.apimaster.apifactory.httpclient.ssl.MasterSslTrustHttpClient;
import org.ccs.apimaster.apifactory.reporter.MasterReportGenerator;
import org.ccs.apimaster.apifactory.supportcenter.actionee.*;
import org.ccs.apimaster.apifactory.utils.SmartUtils;
import org.ccs.apimaster.apifactory.propertymanager.MasterReportProperties;
import org.ccs.apimaster.apifactory.utils.RunnerUtils;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.util.Modules;

import org.junit.runner.Description;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.inject.Guice.createInjector;
import static java.lang.System.getProperty;

/*
@Purpose: This class manages functions for running multiple tests in a batch
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 15/12/2021
*/
public class CCSPackageRunner extends ParentRunner<ScenarioSpec> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CCSPackageRunner.class);

    private final Class<?> testClass;
    private List<ScenarioSpec> scenarioSpecs;
    private Injector injector;
    private SmartUtils smartUtils;
    protected Description scenarioDescription;
    protected boolean isRunSuccess;
    protected boolean passed;
    protected boolean testRunCompleted;
    private MasterMultiStepsScenarioRunner masterMultiStepsScenarioRunner;

    public CCSPackageRunner(Class<?> testClass) throws InitializationError {
        super(testClass);

        if (Files.exists(Paths.get(MasterReportProperties.TARGET_REPORT_DIR))){
            Arrays.stream(new File(MasterReportProperties.TARGET_REPORT_DIR).listFiles()).forEach(File::delete);
        }

        this.testClass = testClass;
        this.masterMultiStepsScenarioRunner = getInjectedMultiStepsRunner();
        this.smartUtils = getInjectedSmartUtilsClass();
    }

    protected SmartUtils getInjectedSmartUtilsClass() {
        return getMainModuleInjector().getInstance(SmartUtils.class);
    }

    @Inject
    public CCSPackageRunner(Class<?> testClass, SmartUtils smartUtils) throws InitializationError {
        super(testClass);
        this.testClass = testClass;
        this.smartUtils = smartUtils;
    }

    /*
      @Method: getChildren
      @Purpose: To get child tests to form batch
      @Author: Mibin Boban, CCS Senior QAT Analyst
      @Creation: 15/12/2021
    */
    @Override
    protected List<ScenarioSpec> getChildren() {
        CCS_Master_Suite rootPackageAnnotation = testClass.getAnnotation(CCS_Master_Suite.class);
        JsonSuite jsonSuiteAnnotation = testClass.getAnnotation(JsonSuite.class);
        CCS_Master_Scenarios CCSMasterScenariosAnnotation = testClass.getAnnotation(CCS_Master_Scenarios.class);
        validateSuiteAnnotationPresent(rootPackageAnnotation, jsonSuiteAnnotation, CCSMasterScenariosAnnotation);

        if (rootPackageAnnotation != null) {
            smartUtils.checkDuplicateScenarios(rootPackageAnnotation.value());
            return smartUtils.getScenarioSpecListByPackage(rootPackageAnnotation.value());

        } else {
            List<String> allEndPointFiles = readTestScenarioFiles();
            return allEndPointFiles.stream()
                    .map(testResource -> {
                        try {
                            return smartUtils.scenarioFileToJava(testResource, ScenarioSpec.class);
                        } catch (IOException e) {
                            throw new RuntimeException("[CCS Master] : Exception while deserializing to Spec. Details: " + e);
                        }
                    })
                    .collect(Collectors.toList());
        }
    }

    /*
      @Method: describeChild
      @Purpose: To get description of tests in batch
      @Author: Mibin Boban, CCS Senior QAT Analyst
      @Creation: 15/12/2021
    */
    @Override
    protected Description describeChild(ScenarioSpec child) {

        this.scenarioDescription = Description.createTestDescription(testClass, child.getScenarioName());
        return scenarioDescription;
    }

    /*
      @Method: run
      @Purpose: To trigger the batch with multiple tests
      @Author: Mibin Boban, CCS Senior QAT Analyst
      @Creation: 15/12/2021
    */
    @Override
    public void run(RunNotifier notifier) {
        RunListener reportListener = createReportListener();
        notifier.addListener(reportListener);

        LOGGER.info("[CCS Master] : System property " + MasterReportProperties.CCS_MASTER_JUNIT + "=" + getProperty(MasterReportProperties.CCS_MASTER_JUNIT));
        if (!MasterReportProperties.CHARTS_AND_CSV.equals(getProperty(MasterReportProperties.CCS_MASTER_JUNIT))) {
            notifier.addListener(reportListener);
        }

        super.run(notifier);
        handleNoRunListenerReport(reportListener);
    }

    protected RunListener createReportListener() {
        return getMainModuleInjector().getInstance(MasterTestReportListener.class);
    }

    /*
     @Method: runChild
     @Purpose: To run tests one by one in the batch with multiple tests
     @Author: Mibin Boban, CCS Senior QAT Analyst
     @Creation: 15/12/2021
   */
    @Override
    protected void runChild(ScenarioSpec child, RunNotifier notifier) {

        final Description description = Description.createTestDescription(testClass, child.getScenarioName());

        notifier.fireTestStarted(description);

        passed = masterMultiStepsScenarioRunner.runScenario(child, notifier, description);

        testRunCompleted = true;

        if (passed) {
            LOGGER.info(String.format("\n[CCS Master] : PackageRunner- **FINISHED executing all Steps for [%s] **.\nSteps were:%s",
                    child.getScenarioName(),
                    child.getSteps().stream().map(step -> step.getName()).collect(Collectors.toList())));
        }
        notifier.fireTestFinished(description);

    }

    /*
     @Method: getMainModuleInjector
     @Purpose: To get main module injector
     @Author: Mibin Boban, CCS Senior QAT Analyst
     @Creation: 15/12/2021
   */
    public Injector getMainModuleInjector() {

        final CCS_Master_Properties envAnnotation = testClass.getAnnotation(CCS_Master_Properties.class);
        String serverEnv = envAnnotation != null ? envAnnotation.value() : "config_hosts.properties";

        serverEnv = RunnerUtils.getEnvSpecificConfigFile(serverEnv, testClass);

        Class<? extends BasicHttpClient> runtimeHttpClient = createCustomHttpClientOrDefault();

        return createInjector(Modules.override(new RunConfiguration(serverEnv))
                .with(
                        new HttpClientRuntimeHandler(runtimeHttpClient)
                ));
    }

    public void setSmartUtils(SmartUtils smartUtils) {
        this.smartUtils = smartUtils;
    }

    public boolean isRunSuccess() {
        return isRunSuccess;
    }

    public boolean isPassed() {
        return passed;
    }

    public boolean isTestRunCompleted() {
        return testRunCompleted;
    }

    public void setMasterMultiStepsScenarioRunner(MasterMultiStepsScenarioRunner masterMultiStepsScenarioRunner) {
        this.masterMultiStepsScenarioRunner = masterMultiStepsScenarioRunner;
    }

    public Class<? extends BasicHttpClient> createCustomHttpClientOrDefault() {
        final UseHttpClient httpClientAnnotated = getUseHttpClient();
        return httpClientAnnotated != null ? httpClientAnnotated.value() : MasterSslTrustHttpClient.class;
    }

    public UseHttpClient getUseHttpClient() {
        return testClass.getAnnotation(UseHttpClient.class);
    }

    private MasterMultiStepsScenarioRunner getInjectedMultiStepsRunner() {
        masterMultiStepsScenarioRunner = getMainModuleInjector().getInstance(MasterMultiStepsScenarioRunner.class);
        return masterMultiStepsScenarioRunner;
    }

    private MasterReportGenerator getInjectedReportGenerator() {
        return getMainModuleInjector().getInstance(MasterReportGenerator.class);
    }

    private void handleNoRunListenerReport(RunListener reportListener) {
        RunnerUtils.handleTestCompleted(reportListener, LOGGER);
    }

    /*
     @Method: readTestScenarioFiles
     @Purpose: To read test scenario files
     @Author: Mibin Boban, CCS Senior QAT Analyst
     @Creation: 15/12/2021
   */
    private List<String> readTestScenarioFiles() {

        List<JsonTestCase> jsonTestCases = Arrays.asList(testClass.getAnnotationsByType(JsonTestCase.class));
        if (jsonTestCases != null && jsonTestCases.size() > 0) {
            return jsonTestCases.stream()
                    .map(thisTestCase -> thisTestCase.value())
                    .collect(Collectors.toList());
        }

        List<CCS_Master_Scenario> CCSMasterScenarios = Arrays.asList(testClass.getAnnotationsByType(CCS_Master_Scenario.class));
        return CCSMasterScenarios.stream()
                .map(thisTestCase -> thisTestCase.value())
                .collect(Collectors.toList());
    }

    /*
     @Method: validateSuiteAnnotationPresent
     @Purpose: To validate suite annotation
     @Author: Mibin Boban, CCS Senior QAT Analyst
     @Creation: 15/12/2021
   */
    private void validateSuiteAnnotationPresent(CCS_Master_Suite rootPackageAnnotation,
                                                JsonSuite jsonSuiteAnnotation,
                                                CCS_Master_Scenarios CCSMasterScenarios) {
        if (rootPackageAnnotation == null && (jsonSuiteAnnotation == null && CCSMasterScenarios == null)) {
            throw new RuntimeException("[CCS Master] : Missing Test Suite details." +
                    "To run as a Test Suite - \n" +
                    "Annotate your Test Suite class with, e.g. \n@TestPackageRoot(\"resource_folder_for_scenario_files\") " +
                    "\n\n-Or- \n" +
                    "Annotate your Test Suite class with, e.g. \n@JsonTestCases({\n" +
                    "        @JsonTestCase(\"path/to/test_case_1.json\"),\n" +
                    "        @JsonTestCase(\"path/to/test_case_2.json\")\n" +
                    "})" +
                    "\n\n-Or- \n" +
                    "Annotate your Test Suite class with, e.g. \n@Scenarios({\n" +
                    "        @Scenario(\"path/to/test_case_1.json\"),\n" +
                    "        @Scenario(\"path/to/test_case_2.json\")\n" +
                    "})" +
                    "\n\n-Or- \n" +
                    "Run as usual 'Junit Suite' pointing to the individual test classes.");
        }
    }
}
