/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.utils;

import org.ccs.apimaster.apifactory.mainmodule.FrameworkProperties;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.ccs.apimaster.apifactory.utils.SmartUtils.replaceHome;

/*
@Purpose: This class acts as util to provide properties
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 15/12/2021
*/
public class PropertiesProviderUtils {


    private static Properties properties = new Properties();

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static Properties getProperties(String propertyResourceFile) {
        InputStream inputStream = PropertiesProviderUtils.class
                .getClassLoader()
                .getResourceAsStream(propertyResourceFile);

        try {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return properties;
    }


    public static Properties loadAbsoluteProperties(String host, Properties properties) {
        try {
            host = replaceHome(host);

            InputStream inputStream = new FileInputStream(host);
            properties.load(inputStream);

            checkAndLoadOldProperties(properties);

            return properties;

        } catch (Exception exx) {
            throw new RuntimeException(exx);
        }
    }

    public static void checkAndLoadOldProperties(Properties properties) {

        if (properties.get(FrameworkProperties.WEB_APPLICATION_ENDPOINT_HOST) == null && properties.get(FrameworkProperties.RESTFUL_APPLICATION_ENDPOINT_HOST) != null) {
            Object oldPropertyValue = properties.get(FrameworkProperties.RESTFUL_APPLICATION_ENDPOINT_HOST);
            properties.setProperty(FrameworkProperties.WEB_APPLICATION_ENDPOINT_HOST, oldPropertyValue != null ? oldPropertyValue.toString() : null);
        }

        if (properties.get(FrameworkProperties.WEB_APPLICATION_ENDPOINT_PORT) == null && properties.get(FrameworkProperties.RESTFUL_APPLICATION_ENDPOINT_PORT) != null) {
            Object oldPropertyValue = properties.get(FrameworkProperties.RESTFUL_APPLICATION_ENDPOINT_PORT);
            properties.setProperty(FrameworkProperties.WEB_APPLICATION_ENDPOINT_PORT, oldPropertyValue != null ? oldPropertyValue.toString() : null);
        }

        if (properties.get(FrameworkProperties.WEB_APPLICATION_ENDPOINT_CONTEXT) == null && properties.get(FrameworkProperties.RESTFUL_APPLICATION_ENDPOINT_CONTEXT) != null) {
            Object oldPropertyValue = properties.get(FrameworkProperties.RESTFUL_APPLICATION_ENDPOINT_CONTEXT);
            properties.setProperty(FrameworkProperties.WEB_APPLICATION_ENDPOINT_CONTEXT, oldPropertyValue != null ? oldPropertyValue.toString() : null);
        }

    }
}
