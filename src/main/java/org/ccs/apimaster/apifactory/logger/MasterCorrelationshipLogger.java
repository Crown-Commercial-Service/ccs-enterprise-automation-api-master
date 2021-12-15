/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.logger;

import org.ccs.apimaster.apifactory.supportcenter.enhancers.MasterReportStepBuilder;
import org.ccs.apimaster.apifactory.supportcenter.reportutils.MasterReportStep;
import org.slf4j.Logger;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.ccs.apimaster.apifactory.propertymanager.MasterReportProperties.RESULT_FAIL;
import static org.ccs.apimaster.apifactory.propertymanager.MasterReportProperties.RESULT_PASS;
import static java.lang.String.format;
import static java.time.LocalDateTime.now;
import static org.ccs.apimaster.apifactory.propertymanager.MasterReportProperties.*;

/*
@Purpose: This class manages logging at test case level
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 09/12/2021
*/
public class MasterCorrelationshipLogger {
    private static final String VIEW_DEMARCATION_ = "\n--------- " + TEST_STEP_CORRELATION_ID + " %s ---------";

    private Logger logger;
    private String correlationId;
    private RequestLogBuilder requestLogBuilder = new RequestLogBuilder();
    private ResponseLogBuilder responseLogBuilder = new ResponseLogBuilder();
    private ScenarioLogBuilder scenarioLogBuilder = new ScenarioLogBuilder();
    private Integer stepLoop;
    private Boolean result;
    private Double responseDelay;

    private List<MasterReportStep> steps = Collections.synchronizedList(new ArrayList());

    public MasterCorrelationshipLogger step(MasterReportStep step) {
        this.steps.add(step);
        return this;
    }

    public MasterCorrelationshipLogger(Logger logger) {
        this.logger = logger;
    }

    public static MasterCorrelationshipLogger newInstance(Logger logger) {
        return new MasterCorrelationshipLogger(logger);
    }

    public RequestLogBuilder requestBuilder() {
        return requestLogBuilder;
    }

    public MasterCorrelationshipLogger assertion(String assertionJson){
        responseLogBuilder.assertionSection(assertionJson);
        return this;
    }

    public MasterCorrelationshipLogger customLog(String customLog){
        responseLogBuilder.customLog(customLog);
        return this;
    }

    public MasterCorrelationshipLogger stepLoop(Integer stepLoop) {
        this.stepLoop = stepLoop;
        return this;
    }

    public MasterCorrelationshipLogger stepOutcome(Boolean result) {
        this.result = result;
        return this;
    }

    /*
   @Method: buildReportSingleStep
   @Purpose: To log the report at step level
   @Author: Mibin Boban, CCS Senior QAT Analyst
   @Creation: 09/12/2021
   */
    public MasterReportStep buildReportSingleStep() {

        result = result != null ? result : false;

        MasterReportStepBuilder masterReportStep = MasterReportStepBuilder.newInstance()
                .loop(stepLoop)
                .name(requestLogBuilder.getStepName())
                .correlationId(getCorrelationId())
                .result(result == true? RESULT_PASS : RESULT_FAIL)
                .url(requestLogBuilder.getUrl())
                .operation(requestLogBuilder.getMethod())
                .assertions(responseLogBuilder.getAssertion())
                .requestTimeStamp(requestLogBuilder.getRequestTimeStamp())
                .responseTimeStamp(responseLogBuilder.responseTimeStamp)
                .responseDelay(responseDelay)
                .id(requestLogBuilder.getId());
        if (this.result) {
        	masterReportStep.result(RESULT_PASS);
		}else{
			masterReportStep.response(responseLogBuilder.getResponse());
			masterReportStep.request(requestLogBuilder.getRequest());
		}
        if(null != responseLogBuilder.customLog){
            masterReportStep.customLog(responseLogBuilder.customLog);
        }

        return masterReportStep.build();
    }

    public ResponseLogBuilder responseBuilder() {
        return responseLogBuilder;
    }

    public ScenarioLogBuilder scenarioBuilder() {
        return scenarioLogBuilder;
    }

    /*
   @Method: buildResponseDelay
   @Purpose: To delay the response
   @Author: Mibin Boban, CCS Senior QAT Analyst
   @Creation: 09/12/2021
   */
    public void buildResponseDelay() {
        responseDelay = durationMilliSecBetween(
                requestLogBuilder.getRequestTimeStamp(),
                responseLogBuilder.getResponseTimeStamp()
        );
    }

    public static double durationMilliSecBetween(LocalDateTime requestTimeStamp, LocalDateTime responseTimeStamp) {

        Duration dur = Duration.between(requestTimeStamp, responseTimeStamp != null ? responseTimeStamp : now());
        return dur.toMillis();
    }

    public String createRelationshipId() {
        correlationId = getRelationshipUniqueId();
        return format(VIEW_DEMARCATION_, correlationId);
    }

    public static String getRelationshipUniqueId() {
        return UUID.randomUUID().toString();
    }

    public String getCorrelationId() {
        return correlationId;
    }

    /*
   @Method: printLog
   @Purpose: To print the log message
   @Author: Mibin Boban, CCS Senior QAT Analyst
   @Creation: 09/12/2021
   */
    public void printLog() {

        buildResponseDelay();

        String customLog = responseLogBuilder.getCustomLog();
        logger.warn(format("%s %s \n*CCS Master: Response delay:%s milli-secs \n%s \n%s \n-done-\n",
                requestLogBuilder.toString(),
                responseLogBuilder.toString(),
                responseDelay,
                "---------> Expected Response: <----------\n" + responseLogBuilder.getAssertion(),
                customLog == null ? "" : "---------> Custom Log: <----------\n" +customLog
                )
        );
    }

}
