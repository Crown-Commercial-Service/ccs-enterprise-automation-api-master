/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.supportcenter.enhancers;

import org.ccs.apimaster.apifactory.propertymanager.MasterReportProperties;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static org.slf4j.LoggerFactory.getLogger;

/*
@Purpose: This class manages extent report factory
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 15/12/2021
*/
public class ExtentReportsFactory {
    private static final org.slf4j.Logger LOGGER = getLogger(ExtentReportsFactory.class);

    private static ExtentHtmlReporter extentHtmlReporter;

    private static ExtentReports extentReports;

    private static Map<Object, String> systemProperties = new HashMap<>();

    /*
      @Method: createReportTheme
      @Purpose: To create report theme
      @Author: Mibin Boban, CCS Senior QAT Analyst
      @Creation: 15/12/2021
    */
    public static ExtentReports createReportTheme(String reportFileName) {

        ExtentHtmlReporter extentHtmlReporter = createExtentHtmlReporter(reportFileName);

        extentReports = new ExtentReports();

        attachSystemInfo();

        extentReports.attachReporter(extentHtmlReporter);
        extentReports.setReportUsesManualConfiguration(true);

        return extentReports;
    }

    /*
      @Method: attachSystemInfo
      @Purpose: To attach system info
      @Author: Mibin Boban, CCS Senior QAT Analyst
      @Creation: 15/12/2021
    */
    public static void attachSystemInfo() {	
        systemProperties = getSystemProperties();
        final String osName = systemProperties.get("os.name");
        final String osArchitecture = systemProperties.get("os.arch");
        final String javaVersion = systemProperties.get("java.version");
        final String javaVendor = systemProperties.get("java.vendor");
        final String author = "Mibin Boban, Senior QAT Analyst, CCS";

        LOGGER.info("Where were the tests fired? Ans: OS:{}, Architecture:{}, Java:{}, Vendor:{}",
                osName, osArchitecture, javaVersion, javaVendor);

        extentReports.setSystemInfo("OS : ", osName);
        extentReports.setSystemInfo("OS Architecture : ", osArchitecture);
        extentReports.setSystemInfo("Java Version : ", javaVersion);
        extentReports.setSystemInfo("Java Vendor : ", javaVendor);
        extentReports.setSystemInfo("Framework Author : ", author);
    }

    /*
      @Method: createExtentHtmlReporter
      @Purpose: To create extent html reporter
      @Author: Mibin Boban, CCS Senior QAT Analyst
      @Creation: 15/12/2021
    */
    public static ExtentHtmlReporter createExtentHtmlReporter(String reportFileName) {
        extentHtmlReporter = new ExtentHtmlReporter(reportFileName);
        extentHtmlReporter.config().setDocumentTitle(MasterReportProperties.REPORT_TITLE_DEFAULT);
        extentHtmlReporter.config().setReportName(MasterReportProperties.REPORT_DISPLAY_NAME_DEFAULT);

        return extentHtmlReporter;
    }

    /*
      @Method: getSystemProperties
      @Purpose: To get system properties
      @Author: Mibin Boban, CCS Senior QAT Analyst
      @Creation: 15/12/2021
    */
    public static Map<Object, String> getSystemProperties() {
        Map<Object, String> map = new HashMap<>();
        try {
            Properties properties = System.getProperties();

            Set sysPropertiesKeys = properties.keySet();
            for (Object key : sysPropertiesKeys) {
                map.put(key, properties.getProperty((String) key));
            }

        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Extent reporting error. You can safely ignore this. But to fix this see:" + e);
        }

        return map;
    }

    public static void reportName(String reportName) {
        extentHtmlReporter.config().setReportName(reportName);
    }

    public static String getReportName() {
        return extentHtmlReporter.config().getReportName();
    }

}
