/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.runner;

import org.ccs.apimaster.apifactory.supportcenter.actionee.ScenarioSpec;
import org.ccs.apimaster.apifactory.supportcenter.actionee.Step;
import org.ccs.apimaster.apifactory.supportcenter.enhancers.MasterExecutionReportBuilder;
import org.ccs.apimaster.apifactory.supportcenter.enhancers.MasterIoWriteBuilder;
import org.ccs.apimaster.apifactory.coreactions.asserter.FieldAssertionMatcher;
import org.ccs.apimaster.apifactory.coreactions.scriptexecutor.ApiServiceExecutor;
import org.ccs.apimaster.apifactory.coreactions.preparation.*;
import org.ccs.apimaster.apifactory.coreactions.validators.MasterCodeValidator;
import org.ccs.apimaster.apifactory.logger.MasterCorrelationshipLogger;
import org.ccs.apimaster.apifactory.utils.TestTypeSupport;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.univocity.parsers.csv.CsvParser;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.slf4j.Logger;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static org.ccs.apimaster.apifactory.coreactions.datamocker.RestEndPointMocker.wireMockServer;
import static org.ccs.apimaster.apifactory.utils.TestTypeSupport.apiType;
import static org.ccs.apimaster.apifactory.utils.RunnerUtils.getFullyQualifiedUrl;
import static org.ccs.apimaster.apifactory.utils.RunnerUtils.getParameterSize;
import static org.ccs.apimaster.apifactory.utils.SmartUtils.*;
import static java.util.Optional.ofNullable;
import static org.ccs.apimaster.apifactory.supportcenter.enhancers.MasterExecutionReportBuilder.newInstance;

import static org.slf4j.LoggerFactory.getLogger;

/*
@Purpose: This class manages functions for running multiple steps in a scenario
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 15/12/2021
*/
@Singleton
public class MasterMultiStepsScenarioRunnerImpl implements MasterMultiStepsScenarioRunner {

    private static final Logger LOGGER = getLogger(MasterMultiStepsScenarioRunnerImpl.class);

    @Inject
    private ObjectMapper objectMapper;

    @Inject
    private MasterAssertionsProcessor masterAssertionsProcessor;

    @Inject
    private MasterExternalFileProcessor extFileProcessor;

    @Inject
    private MasterParameterizedProcessor parameterizedProcessor;

    @Inject
    private ApiServiceExecutor apiExecutor;

    @Inject
    private CsvParser csvParser;

    @Inject
    private TestTypeSupport testTypeSupport;

    @Inject
    MasterCodeValidator validator;

    @Inject(optional = true)
    @Named("web.application.endpoint.host")
    private String host;

    @Inject(optional = true)
    @Named("web.application.endpoint.port")
    private String port;

    @Inject(optional = true)
    @Named("web.application.endpoint.context")
    private String applicationContext;

    private MasterCorrelationshipLogger correlLogger;

    private static StepNotificationHandler notificationHandler = new StepNotificationHandler();

    private MasterIoWriteBuilder ioWriterBuilder;

    private MasterExecutionReportBuilder resultReportBuilder;

    private Boolean stepOutcomeGreen;

    /*
      @Method: runScenario
      @Purpose: To run the scenario
      @Author: Mibin Boban, CCS Senior QAT Analyst
      @Creation: 15/12/2021
    */
    @Override
    public synchronized boolean runScenario(ScenarioSpec scenario, RunNotifier notifier, Description description) {

        LOGGER.info("\n-------------------------- [CCS Master] : BDD- Scenario:{} -------------------------\n", scenario.getScenarioName());

        ioWriterBuilder = MasterIoWriteBuilder.newInstance().timeStamp(LocalDateTime.now());

        ScenarioExecutionState scenarioExecutionState = new ScenarioExecutionState();

        int scenarioLoopTimes = deriveScenarioLoopTimes(scenario);

        boolean wasExecSuccessful = false;

        for (int scnCount = 0; scnCount < scenarioLoopTimes; scnCount++) {

            LOGGER.info("{}\n     [CCS Master] : Executing Scenario Count No. or parameter No. or Row No. | {} | {}",
                    "\n-------------------------------------------------------------------------",
                    scnCount,
                    "\n-------------------------------------------------------------------------");

            ScenarioSpec parameterizedScenario = parameterizedProcessor.manageParameterisedTests(scenario, scnCount);

            resultReportBuilder = newInstance()
                    .loop(scnCount)
                    .scenarioName(parameterizedScenario.getScenarioName());

            wasExecSuccessful = executeSteps(notifier, description, scenarioExecutionState, parameterizedScenario);

            ioWriterBuilder.result(resultReportBuilder.build());
        }

        stopIfWireMockServerRunning();

        ioWriterBuilder.printToFile(scenario.getScenarioName() + correlLogger.getCorrelationId() + ".json");

        if (wasExecSuccessful) {
            return stepOutcomeGreen;
        }

        return true;
    }

    /*
      @Method: executeSteps
      @Purpose: To execute steps in scenario
      @Author: Mibin Boban, CCS Senior QAT Analyst
      @Creation: 15/12/2021
    */
    private boolean executeSteps(RunNotifier notifier,
                                 Description description,
                                 ScenarioExecutionState scenarioExecutionState,
                                 ScenarioSpec parameterizedScenario) {

        ScenarioSpec scenario = parameterizedScenario;

        for (Step thisStep : parameterizedScenario.getSteps()) {
            if (thisStep.getIgnoreStep()) {
                LOGGER.info("Step \"" + thisStep.getName() + "\" is ignored because of ignoreStep property.");
                continue;
            }

            correlLogger = MasterCorrelationshipLogger.newInstance(LOGGER);

            Boolean wasExecSuccess = executeRetryWithSteps(notifier, description, scenarioExecutionState, scenario, thisStep);
            if (wasExecSuccess != null) return wasExecSuccess;
        }

        return false;
    }

    /*
      @Method: executeRetryWithSteps
      @Purpose: To attempt retry for steps
      @Author: Mibin Boban, CCS Senior QAT Analyst
      @Creation: 15/12/2021
    */
    private Boolean executeRetryWithSteps(RunNotifier notifier,
                                          Description description,
                                          ScenarioExecutionState scenarioExecutionState,
                                          ScenarioSpec scenario, Step thisStep) {
        thisStep = extFileProcessor.manageExtJsonFile(thisStep);
        List<Step> thisSteps = extFileProcessor.buildFromStepFile(thisStep, thisStep.getId());
        if(null == thisSteps || thisSteps.isEmpty()) thisSteps.add(thisStep);
        Boolean wasExecSuccess = null;
        for(Step step : thisSteps) {
             wasExecSuccess = executeRetry(notifier,
                    description,
                    scenarioExecutionState,
                    scenario,
                    step);
            if (wasExecSuccess != null) {
                return wasExecSuccess;
            }
        }
        return null;
    }

    /*
      @Method: executeRetry
      @Purpose: To attempt retry execution
      @Author: Mibin Boban, CCS Senior QAT Analyst
      @Creation: 15/12/2021
    */
    private Boolean executeRetry(RunNotifier notifier,
                                 Description description,
                                 ScenarioExecutionState scenarioExecutionState,
                                 ScenarioSpec scenario,
                                 Step thisStep) {

        final String logPrefixRelationshipId = correlLogger.createRelationshipId();
        String executionResult = "-response unavailable-";
        final String requestJsonAsString = thisStep.getRequest().toString();
        StepExecutionState stepExecutionState = new StepExecutionState();
        stepExecutionState.addStep(thisStep.getName());
        String resolvedRequestJson = masterAssertionsProcessor.manageStringJson(
                requestJsonAsString,
                scenarioExecutionState.getResolvedScenarioState());
        stepExecutionState.addRequest(resolvedRequestJson);

        boolean retryTillSuccess = false;
        int retryDelay = 0;
        int retryMaxTimes = 1;
        if (thisStep.getRetry() != null) {
            retryMaxTimes = thisStep.getRetry().getMax();
            retryDelay = thisStep.getRetry().getDelay();
            retryTillSuccess = true;
        }

        String thisStepName = thisStep.getName();

        for (int retryCounter = 0; retryCounter < retryMaxTimes; retryCounter++) {
            try {

                executionResult = executeApi(logPrefixRelationshipId, thisStep, resolvedRequestJson, scenarioExecutionState);

                final LocalDateTime responseTimeStamp = LocalDateTime.now();
                correlLogger.responseBuilder()
                        .relationshipId(logPrefixRelationshipId)
                        .responseTimeStamp(responseTimeStamp)
                        .response(executionResult);
                correlLogger.responseBuilder().customLog(thisStep.getCustomLog());
                stepExecutionState.addResponse(executionResult);
                scenarioExecutionState.addStepState(stepExecutionState.getResolvedStep());

                String resolvedAssertionJson = masterAssertionsProcessor.manageStringJson(
                        thisStep.getAssertions().toString(),
                        scenarioExecutionState.getResolvedScenarioState()
                );

                List<FieldAssertionMatcher> failureResults = compareStepResults(thisStep, executionResult, resolvedAssertionJson);

                if (!failureResults.isEmpty()) {
                    StringBuilder builder = new StringBuilder();

                    builder.append("[CCS Master]: Assumed Payload: \n" + prettyPrintJson(resolvedAssertionJson) + "\n");
                    builder.append("[CCS Master]: Assertion Errors: \n");

                    failureResults.forEach(f -> {
                        builder.append(f.toString() + "\n");
                    });
                    correlLogger.assertion(resolvedAssertionJson != null ? builder.toString() : expectedValidatorsAsJson(thisStep));
                } else {
                    correlLogger.assertion(resolvedAssertionJson != null && !"null".equalsIgnoreCase(resolvedAssertionJson) ?
                            prettyPrintJson(resolvedAssertionJson) : expectedValidatorsAsJson(thisStep));
                }

                if (retryTillSuccess && (retryCounter + 1 < retryMaxTimes) && !failureResults.isEmpty()) {
                    LOGGER.info("\n---------------------------------------\n" +
                            "        Retry: Attempt number: {}", retryCounter + 2 +
                            "\n---------------------------------------\n");
                    waitForDelay(retryDelay);
                    stepOutcomeGreen = true;
                    continue;
                }

                boolean ignoreStepFailures = scenario.getIgnoreStepFailures() == null ? false : scenario.getIgnoreStepFailures();
                if (!failureResults.isEmpty()) {
                    stepOutcomeGreen = notificationHandler.handleAssertion(
                            notifier,
                            description,
                            scenario.getScenarioName(),
                            thisStepName,
                            failureResults,
                            notificationHandler::handleAssertionFailed);

                    correlLogger.stepOutcome(stepOutcomeGreen);

                    if (ignoreStepFailures == true) {
                        stepOutcomeGreen = true;
                        continue;
                    }

                    return true;
                }

                stepOutcomeGreen = notificationHandler.handleAssertion(
                        notifier,
                        description,
                        scenario.getScenarioName(),
                        thisStepName,
                        failureResults,
                        notificationHandler::handleAssertionPassed);
                correlLogger.stepOutcome(stepOutcomeGreen);

                if (retryTillSuccess) {
                    LOGGER.info("Retry: Leaving early with successful assertion");
                    break;
                }

            } catch (Exception ex) {

                ex.printStackTrace();
                LOGGER.error("###Exception while executing a step in the master dsl.");
                final LocalDateTime responseTimeStampEx = LocalDateTime.now();
                correlLogger.responseBuilder()
                        .relationshipId(logPrefixRelationshipId)
                        .responseTimeStamp(responseTimeStampEx)
                        .response(executionResult)
                        .exceptionMessage(ex.getMessage());

                stepOutcomeGreen = notificationHandler.handleAssertion(
                        notifier,
                        description,
                        scenario.getScenarioName(),
                        thisStepName,
                        (new RuntimeException("Master Step execution failed. Details:" + ex)),
                        notificationHandler::handleStepException);

                correlLogger.stepOutcome(stepOutcomeGreen);

                return true;

            } finally {
                correlLogger.printLog();
                resultReportBuilder.step(correlLogger.buildReportSingleStep());

                if (!stepOutcomeGreen) {
                }
            }
        }
        return null;
    }

    /*
      @Method: expectedValidatorsAsJson
      @Purpose: To handle validators
      @Author: Mibin Boban, CCS Senior QAT Analyst
      @Creation: 15/12/2021
    */
    private String expectedValidatorsAsJson(Step thisStep) throws JsonProcessingException {
        if(thisStep.getValidators() == null){
            return "No validators were found for this step";
        }
        return prettyPrintJson(objectMapper.writeValueAsString((thisStep.getValidators())));
    }

    /*
      @Method: executeApi
      @Purpose: To execute the api endpoint
      @Author: Mibin Boban, CCS Senior QAT Analyst
      @Creation: 15/12/2021
    */
    private String executeApi(String logPrefixRelationshipId,
                              Step thisStep,
                              String resolvedRequestJson,
                              ScenarioExecutionState scenarioExecutionState) {

        String url = thisStep.getUrl();
        String operationName = thisStep.getOperation();
        String stepId = thisStep.getId();
        String thisStepName = thisStep.getName();

        url = masterAssertionsProcessor.manageStringJson(url, scenarioExecutionState.getResolvedScenarioState());

        final LocalDateTime requestTimeStamp = LocalDateTime.now();

        String executionResult;

        switch (apiType(url, operationName)) {
            case REST_CALL:
                url = getFullyQualifiedUrl(url, host, port, applicationContext);
                correlLogger.requestBuilder()
                        .relationshipId(logPrefixRelationshipId)
                        .requestTimeStamp(requestTimeStamp)
                        .step(thisStepName)
                        .url(url)
                        .method(operationName)
                        .id(stepId)
                        .request(prettyPrintJson(resolvedRequestJson));

                executionResult = apiExecutor.executeHttpApi(url, operationName, resolvedRequestJson);
                break;

            case JAVA_CALL:
                correlLogger.requestBuilder()
                        .relationshipId(logPrefixRelationshipId)
                        .requestTimeStamp(requestTimeStamp)
                        .step(thisStepName)
                        .id(stepId)
                        .url(url)
                        .method(operationName)
                        .request(prettyPrintJson(resolvedRequestJson));

                url = testTypeSupport.getQualifiedJavaApi(url);
                executionResult = apiExecutor.executeJavaOperation(url, operationName, resolvedRequestJson);
                break;

            case NONE:
                correlLogger.requestBuilder()
                        .relationshipId(logPrefixRelationshipId)
                        .requestTimeStamp(requestTimeStamp)
                        .step(thisStepName)
                        .id(stepId)
                        .url(url)
                        .method(operationName)
                        .request(prettyPrintJson(resolvedRequestJson));

                executionResult = prettyPrintJson(resolvedRequestJson);
                break;

            default:
                throw new RuntimeException("[CCS Master] : ! API Type Undecided. If it is intentional, " +
                        "then keep the value as empty to receive the request in the response");
        }

        return executionResult;
    }

    private void waitForDelay(int delay) {
        if (delay > 0) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ignored) {
            }
        }
    }

    @Override
    public boolean runChildStep(ScenarioSpec scenarioSpec, BiConsumer testPassHandler) {

        scenarioSpec.getSteps()
                .forEach(step -> testPassHandler.accept(scenarioSpec.getScenarioName(), step.getName()));

        return true;
    }

    public void overridePort(int port) {
        this.port = port + "";
    }

    public void overrideHost(String host) {
        this.host = host;
    }

    public void overrideApplicationContext(String applicationContext) {
        this.applicationContext = applicationContext;
    }

    private void stopIfWireMockServerRunning() {
        if (null != wireMockServer) {
            wireMockServer.stop();
            wireMockServer = null;
            LOGGER.info("Scenario: All mockings done via WireMock server. Dependant end points executed. Stopped WireMock.");
        }
    }

    private int deriveScenarioLoopTimes(ScenarioSpec scenario) {
        int scenarioLoopTimes = scenario.getLoop() == null ? 1 : scenario.getLoop();
        int parameterSize = getParameterSize(scenario.getParameterized());
        scenarioLoopTimes = parameterSize != 0 ? parameterSize : scenarioLoopTimes;
        return scenarioLoopTimes;
    }

    /*
      @Method: compareStepResults
      @Purpose: To compare the step results
      @Author: Mibin Boban, CCS Senior QAT Analyst
      @Creation: 15/12/2021
    */
    private List<FieldAssertionMatcher> compareStepResults(Step thisStep, String actualResult, String expectedResult) {
        List<FieldAssertionMatcher> failureResults = new ArrayList<>();

        if (ofNullable(thisStep.getValidators()).orElse(null) != null) {
            failureResults = validator.validateRuleFlat(thisStep, actualResult);
        }

        else if (ofNullable(thisStep.getVerifyMode()).orElse("LENIENT").equals("STRICT")) {
            failureResults = validator.validateRuleStrict(expectedResult, actualResult);
        }

        else {
            failureResults = validator.validateRuleLenient(expectedResult, actualResult);
        }

        return failureResults;
    }

}
