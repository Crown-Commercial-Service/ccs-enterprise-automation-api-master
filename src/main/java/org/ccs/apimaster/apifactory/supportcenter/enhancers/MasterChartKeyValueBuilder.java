/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.supportcenter.enhancers;

import org.ccs.apimaster.apifactory.supportcenter.reportutils.chart.MasterChartKeyValue;

/*
@Purpose: This class manages chart key value builder
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 15/12/2021
*/
public class MasterChartKeyValueBuilder {

    String key;
    Double value;
    String result;

    public static MasterChartKeyValueBuilder newInstance() {
        return new MasterChartKeyValueBuilder();
    }

    public MasterChartKeyValue build() {
        MasterChartKeyValue built = new MasterChartKeyValue(key, value, result);
        return built;
    }


    public MasterChartKeyValueBuilder key(String key) {
        this.key = key;
        return  this;
    }

    public MasterChartKeyValueBuilder value(Double value) {
        this.value = value;
        return  this;
    }

    public MasterChartKeyValueBuilder result(String result) {
        this.result = result;
        return  this;
    }
}
