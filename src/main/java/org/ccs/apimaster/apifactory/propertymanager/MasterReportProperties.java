/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.propertymanager;

/*
@Purpose: This class manages properties for reporting
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 09/12/2021
*/
public interface MasterReportProperties {
    String RESULT_PASS = "PASSED";
    String RESULT_FAIL = "FAILED";
    String TEST_STEP_CORRELATION_ID = "TEST-STEP-CORRELATION-ID:";
    String TARGET_FULL_REPORT_DIR = "target/";
    String TARGET_REPORT_DIR = "target/api-test-reports/";
    String TARGET_FULL_REPORT_CSV_FILE_NAME = "ccs-junit-granular-report.csv";
    String TARGET_FILE_NAME = "target/ccs-junit-interactive-report.html";
    String HIGH_CHART_HTML_FILE_NAME = "ccs_results_chart";
    String AUTHOR_MARKER_OLD = "@@"; //Deprecated
    String AUTHOR_MARKER_NEW = "@";
    String CATEGORY_MARKER = "#";
    String ANONYMOUS_CAT = "Anonymous";
    String REPORT_TITLE_DEFAULT = "CCS API Test Report";
    String REPORT_DISPLAY_NAME_DEFAULT = "CCS API Automation Test Report";
    String DEFAULT_REGRESSION_CATEGORY = "Regression";
    String LINK_LABEL_NAME = "Spike Chart(Click here)";
    String CCS_MASTER_JUNIT = "master.junit";
    String CHARTS_AND_CSV = "gen-smart-charts-csv-reports";

}
