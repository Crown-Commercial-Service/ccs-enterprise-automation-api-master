/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.supportcenter.enhancers;

import org.ccs.apimaster.apifactory.supportcenter.reportutils.csv.MasterCsvReport;

/*
@Purpose: This class manages CSV report
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 15/12/2021
*/
public class MasterCsvReportBuilder {
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

    public static MasterCsvReportBuilder newInstance() {
        return new MasterCsvReportBuilder();
    }

    public MasterCsvReport build() {
        MasterCsvReport built = new MasterCsvReport(scenarioName,scenarioLoop,stepName, stepLoop,
                correlationId, result, method, requestTimeStamp, responseTimeStamp, responseDelayMilliSec);
        return built;
    }

    public MasterCsvReportBuilder scenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
        return this;
    }

    public MasterCsvReportBuilder scenarioLoop(Integer scenarioLoop) {
        this.scenarioLoop = scenarioLoop;
        return this;
    }

    public MasterCsvReportBuilder stepName(String stepName) {
        this.stepName = stepName;
        return this;
    }

    public MasterCsvReportBuilder stepLoop(Integer stepLoop) {
        this.stepLoop = stepLoop;
        return this;
    }

    public MasterCsvReportBuilder correlationId(String correlationId) {
        this.correlationId = correlationId;
        return this;
    }

    public MasterCsvReportBuilder result(String result) {
        this.result = result;
        return this;
    }

    public MasterCsvReportBuilder method(String method) {
        this.method = method;
        return this;
    }

    public MasterCsvReportBuilder requestTimeStamp(String requestTimeStamp) {
        this.requestTimeStamp = requestTimeStamp;
        return this;
    }

    public MasterCsvReportBuilder responseTimeStamp(String responseTimeStamp) {
        this.responseTimeStamp = responseTimeStamp;
        return this;
    }

    public MasterCsvReportBuilder responseDelayMilliSec(Double responseDelayMilliSec) {
        this.responseDelayMilliSec = responseDelayMilliSec;
        return this;
    }
}
