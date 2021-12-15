/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.supportcenter.reportutils.csv;

/*
@Purpose: This class manages master CSV file report
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 15/12/2021
*/
public class MasterCsvReport {
    private String scenarioName;
    private Integer scenarioLoop;
    private String stepName;
    private Integer stepLoop;
    private String correlationId;
    private String result;
    private String method;
    String requestTimeStamp;
    String responseTimeStamp;
    private Double responseDelayMilliSec;

    public MasterCsvReport(String scenarioName, Integer scenarioLoop, String stepName, Integer stepLoop,
                           String correlationId, String result, String method, String requestTimeStamp,
                           String responseTimeStamp, Double responseDelayMilliSec) {
        this.scenarioName = scenarioName;
        this.scenarioLoop = scenarioLoop;
        this.stepName = stepName;
        this.stepLoop = stepLoop;
        this.correlationId = correlationId;
        this.result = result;
        this.method=method;
        this.requestTimeStamp = requestTimeStamp;
        this.responseTimeStamp = responseTimeStamp;
        this.responseDelayMilliSec = responseDelayMilliSec;
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public Integer getScenarioLoop() {
        return scenarioLoop;
    }

    public String getStepName() {
        return stepName;
    }

    public Integer getStepLoop() {
        return stepLoop;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public String getResult() {
        return result;
    }

    public String getMethod() {
        return method;
    }

    public Double getResponseDelayMilliSec() {
        return responseDelayMilliSec;
    }

    public String getRequestTimeStamp() {
        return requestTimeStamp;
    }

    public String getResponseTimeStamp() {
        return responseTimeStamp;
    }

    @Override
    public String toString() {
        return "CCSMasterCsvReport{" +
                "scenarioName='" + scenarioName + '\'' +
                ", scenarioLoop=" + scenarioLoop +
                ", stepName='" + stepName + '\'' +
                ", stepLoop=" + stepLoop +
                ", correlationId='" + correlationId + '\'' +
                ", result='" + result + '\'' +
                ", method='" + method + '\'' +
                ", requestTimeStamp=" + requestTimeStamp +
                ", responseTimeStamp=" + responseTimeStamp +
                ", responseDelayMilliSec=" + responseDelayMilliSec +
                '}';
    }
}
