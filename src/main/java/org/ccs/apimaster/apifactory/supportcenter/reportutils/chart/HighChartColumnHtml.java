/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.supportcenter.reportutils.chart;

/*
@Purpose: This class manages high chart to column html
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 15/12/2021
*/
public class HighChartColumnHtml {
    String pageTitle;
    String testResult;
    String chartTitleTop;
    String textYaxis;
    String chartSeriesName;
    String chartTitleTopInABox;

    public HighChartColumnHtml(String pageTitle, String testResult, String chartTitleTop, String textYaxis, String chartSeriesName, String chartTitleTopInABox) {
        this.pageTitle = pageTitle;
        this.testResult = testResult;
        this.chartTitleTop = chartTitleTop;
        this.textYaxis = textYaxis;
        this.chartSeriesName = chartSeriesName;
        this.chartTitleTopInABox = chartTitleTopInABox;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public String getTestResult() {
        return testResult;
    }

    public String getChartTitleTop() {
        return chartTitleTop;
    }

    public String getTextYaxis() {
        return textYaxis;
    }

    public String getChartSeriesName() {
        return chartSeriesName;
    }

    public String getChartTitleTopInABox() {
        return chartTitleTopInABox;
    }

}
