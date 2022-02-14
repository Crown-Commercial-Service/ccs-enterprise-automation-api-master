/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.supportcenter.actionee;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/*
@Purpose: This class acts as actionee for test scenario specification
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 15/12/2021
*/
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScenarioSpec {
    private final Integer loop;
    private final Boolean ignoreStepFailures;
    private final String scenarioName;
    private final List<Step> steps;
    private final Parameterized parameterized;
    private final String scenarioDesc;

    @JsonCreator
    public ScenarioSpec(
            @JsonProperty("stepLoop") Integer loop,
            @JsonProperty("ignoreStepFailures") Boolean ignoreStepFailures,
            @JsonProperty("scenarioName") String scenarioName,
            @JsonProperty("scenarioDesc") String scenarioDesc,
            @JsonProperty("steps") List<Step> steps,
            @JsonProperty("parameterized") Parameterized parameterized) {
        this.loop = loop;
        this.ignoreStepFailures = ignoreStepFailures;
        this.scenarioName = scenarioName;
        this.scenarioDesc = scenarioDesc;
        this.steps = steps;
        this.parameterized = parameterized;
    }

    public Integer getLoop() {
        return loop;
    }

    public Boolean getIgnoreStepFailures() {
        return ignoreStepFailures;
    }

    public String getScenarioName() {
        return scenarioName;
    }
    public String getScenarioDesc() {
        return scenarioDesc;
    }

    public List<Step> getSteps() {
        return steps == null? (new ArrayList<>()) : steps;
    }

    public Parameterized getParameterized() {
        return parameterized;
    }

    @Override
    public String toString() {
        return "ScenarioSpec{" +
                "loop=" + loop +
                ", ignoreStepFailures=" + ignoreStepFailures +
                ", scenarioName='" + scenarioName + '\'' +
                ", steps=" + steps +
                ", parameterized=" + parameterized +
                '}';
    }
}
