/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.supportcenter.reportutils.chart;

import com.fasterxml.jackson.annotation.JsonProperty;

/*
@Purpose: This class manages master chart with key-value
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 15/12/2021
*/
public class MasterChartKeyValue {

    String key;
    Double value;
    private String result;

    public MasterChartKeyValue(
            @JsonProperty("key")String key,
            @JsonProperty("value")Double value,
            @JsonProperty("result")String result) {
        this.key = key;
        this.value = value;
        this.result = result;
    }


    public String getKey() {
        return key;
    }

    public Double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "CCSMasterKeyValue{" +
                "key='" + key + '\'' +
                ", value=" + value +
                '}';
    }

    public String getResult() {
        return result;
    }
}
