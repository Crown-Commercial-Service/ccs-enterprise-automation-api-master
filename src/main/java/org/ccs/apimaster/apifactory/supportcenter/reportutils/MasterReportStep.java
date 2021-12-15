/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.supportcenter.reportutils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDateTime;

/*
@Purpose: This class manages master step level report
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 15/12/2021
*/
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MasterReportStep {
    private final Integer loop;
    private final String name;
    private final String url;
    private final String method;
    private final String correlationId;
    private final String operation;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private final LocalDateTime requestTimeStamp;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private final LocalDateTime responseTimeStamp;
    private final Double responseDelay;
    private final String result;
    private final String request;
    private final String response;
    private final String assertions;
    private final String customLog;

    @JsonCreator
    public MasterReportStep(
            @JsonProperty("stepLoop") Integer loop,
            @JsonProperty("name") String name,
            @JsonProperty("url") String url,
            @JsonProperty("method") String method,
            @JsonProperty("correlationId") String correlationId,
            @JsonProperty("operation") String operation,
            @JsonProperty("requestTimeStamp") LocalDateTime requestTimeStamp,
            @JsonProperty("responseTimeStamp") LocalDateTime responseTimeStamp,
            @JsonProperty("responseDelay") Double responseDelay,
            @JsonProperty("result") String result,
            @JsonProperty("request") String request,
            @JsonProperty("response") String response,
            @JsonProperty("assertions") String assertions,
            @JsonProperty("customLog") String customLog) {
        this.loop = loop;
        this.name = name;
        this.url = url;
        this.method=method;
        this.correlationId = correlationId;
        this.operation = operation;
        this.requestTimeStamp = requestTimeStamp;
        this.responseTimeStamp = responseTimeStamp;
        this.responseDelay = responseDelay;
        this.result = result;
        this.request = request;
        this.response = response;
        this.assertions = assertions;
        this.customLog = customLog;
    }


    public MasterReportStep(String correlationId, String result){
        this(null,null,null,null,correlationId,null,null,null,null,result,null,null,null,null);
    }


    public Integer getLoop() {
        return loop;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public String getOperation() {
        return operation;
    }

    public LocalDateTime getRequestTimeStamp() {
        return requestTimeStamp;
    }

    public LocalDateTime getResponseTimeStamp() {
        return responseTimeStamp;
    }

    public Double getResponseDelay() {
        return responseDelay;
    }

    public String getResult() {
        return result;
    }

    public String getRequest() {
        return request;
    }

    public String getResponse() {
        return response;
    }

    public String getAssertions() {
        return assertions;
    }

    public String getCustomLog(){ return customLog;}

    @Override
    public String toString() {
        return "CCSMasterReportStep{" +
                "loop=" + loop +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", method='" + method + '\'' +
                ", correlationId='" + correlationId + '\'' +
                ", operation='" + operation + '\'' +
                ", requestTimeStamp=" + requestTimeStamp +
                ", responseTimeStamp=" + responseTimeStamp +
                ", responseDelay='" + responseDelay + '\'' +
                ", result='" + result + '\'' +
                ", request='" + request + '\'' +
                ", response='" + response + '\'' +
                ", assertions='" + assertions + '\'' +
                ", customLog='" + customLog + '\'' +
                '}';
    }
}
