/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.supportcenter.actionee;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/*
@Purpose: This class acts as actionee for parameters
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 15/12/2021
*/
public class Parameterized {
    private final List<Object> valueSource;
    private final List<String> csvSource;

    public Parameterized(
            @JsonProperty("valueSource") List<Object> valueSource,
            @JsonProperty("csvSource") List<String> csvSource) {
        this.valueSource = valueSource;
        this.csvSource = csvSource;
    }

    public List<Object> getValueSource() {
        return valueSource;
    }

    public List<String> getCsvSource() {
        return csvSource;
    }

    @Override
    public String toString() {
        return "Parameterized{" +
                "valueSource=" + valueSource +
                ", csvSource=" + csvSource +
                '}';
    }
}
