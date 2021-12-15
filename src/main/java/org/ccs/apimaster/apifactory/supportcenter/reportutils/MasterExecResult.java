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

import java.util.ArrayList;
import java.util.List;

/*
@Purpose: This class manages master execution result
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 15/12/2021
*/
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MasterExecResult {
    private String scenarioName;
    private Integer loop;
    private List<MasterReportStep> steps = new ArrayList<>();

    @JsonCreator
    public MasterExecResult(
            @JsonProperty("scenarioName")String scenarioName,
            @JsonProperty("stepLoop")Integer loop,
            @JsonProperty("steps")List<MasterReportStep> steps) {
        this.scenarioName = scenarioName;
        this.loop = loop;
        this.steps = steps;
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public Integer getLoop() {
        return loop;
    }

    public List<MasterReportStep> getSteps() {
        return steps;
    }

    public void setSteps(List<MasterReportStep> steps) {
        this.steps = steps;
    }

    @Override
    public String toString() {
        return "CCSMasterExecResult{" +
                "scenarioName='" + scenarioName + '\'' +
                ", stepLoop=" + loop +
                ", steps=" + steps +
                '}';
    }
}
