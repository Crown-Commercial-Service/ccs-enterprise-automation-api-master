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
@Purpose: This class acts as actionee for package mapping
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 15/12/2021
*/
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PackageMapper {

    private final List<TestMapper> mocks;

    @JsonCreator
    public PackageMapper(@JsonProperty("mocks") List<TestMapper> mocks) {
        this.mocks = mocks;
    }

    public List<TestMapper> getMocks() {
        return mocks == null ? (new ArrayList<>()) : mocks;
    }

    @Override
    public String toString() {
        return "MockSteps{" +
                "mocks=" + mocks +
                '}';
    }
}
