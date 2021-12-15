/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.supportcenter.enhancers;

import org.ccs.apimaster.apifactory.supportcenter.reportutils.MasterReportStep;

import java.time.LocalDateTime;

/*
@Purpose: This class manages step level report builder
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 15/12/2021
*/
public class MasterReportStepBuilder {
    Integer loop;
    String name;
    String url;
    String method;
    String correlationId;
    String operation;
    LocalDateTime requestTimeStamp;
    LocalDateTime responseTimeStamp;
    Double responseDelay;
    String result;
    String request;
    String response;
    String id;
    String assertions;
    String customLog;

    public static MasterReportStepBuilder newInstance() {
        return new MasterReportStepBuilder();
    }

    public MasterReportStep build() {
        MasterReportStep built = new MasterReportStep(
                loop, name, url, method,
                correlationId, operation, requestTimeStamp,
                responseTimeStamp, responseDelay, result,
                request, response, assertions, customLog);
        return built;
    }

    public MasterReportStepBuilder loop(Integer loop) {
        this.loop = loop;
        return this;
    }

    public MasterReportStepBuilder name(String name) {
        this.name = name;
        return this;
    }

    public MasterReportStepBuilder url(String url) {
        this.url = url;
        return this;
    }

    public MasterReportStepBuilder method(String method) {
        this.method = method;
        return this;
    }
    public MasterReportStepBuilder correlationId(String correlationId) {
        this.correlationId = correlationId;
        return this;
    }

    public MasterReportStepBuilder operation(String operation) {
        this.operation = operation;
        return this;
    }

    public MasterReportStepBuilder requestTimeStamp(LocalDateTime requestTimeStamp) {
        this.requestTimeStamp = requestTimeStamp;
        return this;
    }

    public MasterReportStepBuilder responseTimeStamp(LocalDateTime responseTimeStamp) {
        this.responseTimeStamp = responseTimeStamp;
        return this;
    }

    public MasterReportStepBuilder responseDelay(double responseDelay) {
        this.responseDelay = responseDelay;
        return this;
    }

    public MasterReportStepBuilder request(String request) {
        this.request = request;
        return this;
    }

    public MasterReportStepBuilder response(String response) {
        this.response = response;
        return this;
    }

    public MasterReportStepBuilder result(String result) {
        this.result = result;
        return this;
    }

    public MasterReportStepBuilder assertions(String assertions) {
        this.assertions = assertions;
        return this;
    }

    public MasterReportStepBuilder id(String id) {
        this.id = id;
        return this;
    }
    public MasterReportStepBuilder customLog(String customLog) {
        this.customLog = customLog;
        return this;
    }

}
