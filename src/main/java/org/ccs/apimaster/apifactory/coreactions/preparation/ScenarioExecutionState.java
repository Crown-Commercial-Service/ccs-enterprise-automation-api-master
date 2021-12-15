/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.coreactions.preparation;

import org.apache.commons.lang.text.StrSubstitutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*
@Purpose: This class acts as master class for managing the scenario execution states
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 30/11/2021
*/
public class ScenarioExecutionState {

    private String scenarioStateTemplate = "{\n" +
            "  ${STEP_REQUEST_RESPONSE_SECTION}\n" +
            "}";

    List<StepExecutionState> allSteps = new ArrayList<>();
    List<String> allStepsInStringList = new ArrayList<>();

    Map<String, String> paramMap = new HashMap<>();

    public ScenarioExecutionState() {
        //SmartUtils.readJsonAsString("engine/request_respone_template_step.json");
    }

    public String getScenarioStateTemplate() {
        return scenarioStateTemplate;
    }

    public void setScenarioStateTemplate(String scenarioStateTemplate) {
        this.scenarioStateTemplate = scenarioStateTemplate;
    }

    public List<StepExecutionState> getAllSteps() {
        return allSteps;
    }

    public void setAllSteps(List<StepExecutionState> allSteps) {
        this.allSteps = allSteps;
    }

    public List<String> getAllStepsInStringList() {
        return allStepsInStringList;
    }

    public void setAllStepsInStringList(List<String> allStepsInStringList) {
        this.allStepsInStringList = allStepsInStringList;
    }

    public void addStepState(String stepState){
        allStepsInStringList.add(stepState);
    }

    public String getResolvedScenarioState() {
        final String commaSeparatedStepResults = getAllStepsInStringList().stream()
                .map(i -> i)
                .collect(Collectors.joining(", "));
        paramMap.put("STEP_REQUEST_RESPONSE_SECTION", commaSeparatedStepResults);

        return (new StrSubstitutor(paramMap)).replace(scenarioStateTemplate);
    }
}
