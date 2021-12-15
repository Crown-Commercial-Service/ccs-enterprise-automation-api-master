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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
@Purpose: This class manages master execution report
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 15/12/2021
*/
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MasterReport {

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime timeStamp;
    private List<MasterExecResult> results = Collections.synchronizedList(new ArrayList());

    @JsonCreator
    public MasterReport(
            @JsonProperty("timeStamp")LocalDateTime timeStamp,
            @JsonProperty("results")List<MasterExecResult> results) {
        this.timeStamp = timeStamp;
        this.results = results;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public List<MasterExecResult> getResults() {
        return results;
    }

    @Override
    public String toString() {
        return "CCSMasterReport{" +
                "timeStamp=" + timeStamp +
                ", results=" + results +
                '}';
    }
}
