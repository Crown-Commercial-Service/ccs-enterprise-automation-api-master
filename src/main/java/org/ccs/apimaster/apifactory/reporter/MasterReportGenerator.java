/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.reporter;

/*
@Purpose: This class manages report generation in csv and extend formats
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 09/12/2021
*/
public interface MasterReportGenerator {
    void generateCsvReport();

    void generateHighChartReport();

    void generateExtentReport();
}
