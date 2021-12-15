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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/*
@Purpose: This class manages chart key value array builder
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 15/12/2021
*/
public class MasterChartKeyValueArrayBuilder {

    public static final String TEST_FAILED = "FAILED";

    List<MasterChartKeyValue> kvs = new ArrayList<>();

    public static MasterChartKeyValueArrayBuilder newInstance() {
        return new MasterChartKeyValueArrayBuilder();
    }

    public String build() {
        final String dataRowsCommaSeparated = kvs.stream()
                .map(thisRow -> {
                            if (TEST_FAILED.equals(thisRow.getResult())) {
                                return String.format("{name: '%s', y: %s, color: '#FF0000'}", thisRow.getKey(), thisRow.getValue());
                            } else {
                                return String.format("{name: '%s', y: %s}", thisRow.getKey(), thisRow.getValue());
                            }

                        }

                )
                .collect(Collectors.joining(", "));

        String dataArray = "[" + dataRowsCommaSeparated + "]";

        return dataArray;
    }

    public MasterChartKeyValueArrayBuilder kvs(List<MasterChartKeyValue> kvs) {
        this.kvs = kvs;
        return this;
    }

    public MasterChartKeyValueArrayBuilder kv(MasterChartKeyValue kv) {
        this.kvs.add(kv);
        return this;
    }

}
