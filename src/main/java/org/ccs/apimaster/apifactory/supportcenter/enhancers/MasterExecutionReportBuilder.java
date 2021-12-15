/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.supportcenter.enhancers;

import org.ccs.apimaster.apifactory.supportcenter.reportutils.MasterExecResult;
import org.ccs.apimaster.apifactory.supportcenter.reportutils.MasterReportStep;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
@Purpose: This class manages master execution report building
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 15/12/2021
*/
public class MasterExecutionReportBuilder {
    private String scenarioName;
    private Integer loop;
    private List<MasterReportStep> steps = Collections.synchronizedList(new ArrayList());

    public static MasterExecutionReportBuilder newInstance() {
        return new MasterExecutionReportBuilder();
    }

    public MasterExecResult build() {
        MasterExecResult built = new MasterExecResult(scenarioName, loop, steps);
        return built;
    }

    public MasterExecutionReportBuilder scenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
        return this;
    }

    public MasterExecutionReportBuilder loop(Integer loop) {
        this.loop = loop;
        return this;
    }

    public MasterExecutionReportBuilder steps(List<MasterReportStep> steps) {
        this.steps = steps;
        return this;
    }

    public MasterExecutionReportBuilder step(MasterReportStep step) {
        this.steps.add(step);
        return this;
    }
}
