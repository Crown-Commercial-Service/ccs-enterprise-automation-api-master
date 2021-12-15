/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.supportcenter.enhancers;

import org.ccs.apimaster.apifactory.supportcenter.reportutils.chart.HighChartColumnHtml;

/*
@Purpose: This class manages high chart column html builder
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 15/12/2021
*/
public class HighChartColumnHtmlBuilder {
    String pageTitle;
    String testResult;
    String chartTitleTop;
    String textYaxis;
    String chartSeriesName;
    String chartTitleTopInABox;

    public static HighChartColumnHtmlBuilder newInstance() {
        return new HighChartColumnHtmlBuilder();
    }

    public HighChartColumnHtml build() {
        HighChartColumnHtml built = new HighChartColumnHtml(pageTitle, testResult, chartTitleTop,
                textYaxis, chartSeriesName, chartTitleTopInABox);

        return built;
    }

    public HighChartColumnHtmlBuilder pageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
        return this;
    }

    public HighChartColumnHtmlBuilder testResult(String testResult) {
        this.testResult = testResult;
        return this;
    }

    public HighChartColumnHtmlBuilder chartTitleTop(String chartTitleTop) {
        this.chartTitleTop = chartTitleTop;
        return this;
    }

    public HighChartColumnHtmlBuilder textYaxis(String textYaxis) {
        this.textYaxis = textYaxis;
        return this;
    }

    public HighChartColumnHtmlBuilder chartSeriesName(String chartSeriesName) {
        this.chartSeriesName = chartSeriesName;
        return this;
    }

    public HighChartColumnHtmlBuilder chartTitleTopInABox(String chartTitleTopInABox) {
        this.chartTitleTopInABox = chartTitleTopInABox;
        return this;
    }
}
