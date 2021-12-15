/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.logger;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.time.LocalDateTime;

/*
@Purpose: This class manages logging for scenarios
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 09/12/2021
*/
public class ScenarioLogBuilder {

    String name;
    LocalDateTime requestTimeStamp;

    @JsonCreator
    public ScenarioLogBuilder() {
    }


    public LocalDateTime getRequestTimeStamp() {
        return requestTimeStamp;
    }

    @Override
    public String toString() {
        return "\n[CCS Master]-------------------------- Scenario: ----------------------------\n" +
                "\nname:" + name +
                "\nrequestTimeStamp:" + requestTimeStamp;
    }


    public ScenarioLogBuilder scenarioName(String name) {
        this.name = name;
        return this;
    }

    public ScenarioLogBuilder requestTimeStamp(LocalDateTime now) {
        this.requestTimeStamp = now;
        return this;
    }
}
