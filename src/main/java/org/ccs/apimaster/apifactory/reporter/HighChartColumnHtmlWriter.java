/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.reporter;

import org.ccs.apimaster.apifactory.supportcenter.reportutils.chart.HighChartColumnHtml;
import org.ccs.apimaster.apifactory.propertymanager.MasterReportProperties;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import static org.slf4j.LoggerFactory.getLogger;

/*
@Purpose: This class manages reporting with High Chart Coulmn html
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 09/12/2021
*/
public class HighChartColumnHtmlWriter {
    private static final org.slf4j.Logger LOGGER = getLogger(HighChartColumnHtmlWriter.class);

    public static final String VELOCITY_HIGH_CHART_DEFAULT_FILE = "reports/01_high_chart_column.vm";

    private VelocityEngine vEngine = new VelocityEngine();

    private String templateFile;

    public HighChartColumnHtmlWriter() {
    }

    public HighChartColumnHtmlWriter(String templateFile) {
        this.templateFile = templateFile;
    }

    public String generateHighChart(HighChartColumnHtml highChartColumnHtml, String spikeChartFileName){
        vEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        vEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());

        vEngine.init();

        VelocityContext context = new VelocityContext();

        /*  add the htmlReport Params to a VelocityContext  */
        context.put("highChartColumnHtml", highChartColumnHtml);

        /*  get the Template  */
        Template t = vEngine.getTemplate(getTemplateFileElseDefault());

        /*  now render the template into a Writer  */
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(MasterReportProperties.TARGET_FULL_REPORT_DIR + spikeChartFileName);

            t.merge(context, fileWriter);
            fileWriter.close();
        } catch (IOException chartEx) {
            chartEx.printStackTrace();
            LOGGER.error("Problem occurred during generating test chart. Detail: " + chartEx);

            /*
             * Do not throw exception as this exception is not part of a test execution.
             */
             // throw new RuntimeException(chartEx);
        }

        /* Write to a string - Unit test purpose */
        StringWriter writer = new StringWriter();
        t.merge(context, writer);

        /* use the output */
        final String htmlOut = writer.toString();

        return htmlOut;
    }

    private String getTemplateFileElseDefault() {

        if(templateFile != null){
            return templateFile;
        }

        return VELOCITY_HIGH_CHART_DEFAULT_FILE;
    }

}
