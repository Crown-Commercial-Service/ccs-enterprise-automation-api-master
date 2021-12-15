/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.coreactions.executionlistener;

import org.ccs.apimaster.apifactory.reporter.MasterReportGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;

import static org.slf4j.LoggerFactory.getLogger;

/*
@Purpose: This class acts as master reporting listener
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 29/11/2021
*/
public class MasterTestReportListener extends RunListener {
    private static final org.slf4j.Logger LOGGER = getLogger(MasterTestReportListener.class);

    private final ObjectMapper objMapper;

    private final MasterReportGenerator ccsReportGenerator;

    @Inject
    public MasterTestReportListener(ObjectMapper objMapper, MasterReportGenerator injectedReportGenerator) {
        this.objMapper = objMapper;
        this.ccsReportGenerator = injectedReportGenerator;
    }

    @Override
    public void testRunStarted(Description description) throws Exception {

    }

    /*
    @Method: testRunFinished
    @Purpose: To perform a set of actions when execution is completed
    @Author: Mibin Boban, CCS Senior QAT Analyst
    @Creation: 29/11/2021
    */
    @Override
    public void testRunFinished(Result result) {
        printTestCompleted();
        generateChartsAndReports();
        runPostFinished();
    }

    private void printTestCompleted() {
        LOGGER.info("#CCS API Automation: Test run completed for this runner. Generating test reports and charts.");
    }

    public void runPostFinished() {

    }

    /*
    @Method: generateChartsAndReports
    @Purpose: To genarate reports and charts
    @Author: Mibin Boban, CCS Senior QAT Analyst
    @Creation: 29/11/2021
    */
    private void generateChartsAndReports() {

        ccsReportGenerator.generateCsvReport();

        /**
         * Not compatible with open source license i.e. why not activated. But visit www.highcharts.com for details.
         */

        //reportGenerator.generateHighChartReport();

        ccsReportGenerator.generateExtentReport();
    }
}