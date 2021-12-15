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

import java.util.HashMap;
import java.util.Map;

/*
@Purpose: This class acts as master class for managing the step execution states
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 30/11/2021
*/
public class StepExecutionState {
    Map<String, String> paramMap = new HashMap<>();

    private static String requestResponseState = "\"${STEP.NAME}\": {\n" +
            "    \"request\":${STEP.REQUEST},\n" +
            "    \"response\": ${STEP.RESPONSE}\n" +
            "  }";

    public StepExecutionState() {
        //SmartUtils.readJsonAsString("engine/request_respone_template_scene.json");
    }

    public static String getRequestResponseState() {
        return requestResponseState;
    }

    public void setRequestResponseState(String requestResponseState) {
        this.requestResponseState = requestResponseState;
    }

    public void addStep(String stepName) {
        paramMap.put("STEP.NAME", stepName);
    }

    public void addRequest(String requestJson) {
        paramMap.put("STEP.REQUEST", requestJson);

    }

    public void addResponse(String responseJson) {
        paramMap.put("STEP.RESPONSE", responseJson);
    }

    public String getResolvedStep() {
        StrSubstitutor sub = new StrSubstitutor(paramMap);
        return sub.replace(requestResponseState);
    }
}
