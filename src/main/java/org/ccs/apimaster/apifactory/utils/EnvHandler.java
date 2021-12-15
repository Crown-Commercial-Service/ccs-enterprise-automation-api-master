/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.utils;

import static java.lang.Integer.parseInt;
import static java.lang.System.getProperty;
import static java.lang.System.getenv;

/*
@Purpose: This class manages environment properties
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 15/12/2021
*/
public class EnvHandler {

    public static Integer getEnvValueInt(String envKey) {
        String envValue = getProperty(envKey) == null ? getenv(envKey) : getProperty(envKey);
        return envValue == null ? null : parseInt(envValue);
    }

    public static String getEnvValueString(String envKey) {
        return getProperty(envKey) == null ? getenv(envKey) : getProperty(envKey);
    }

}
