/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.utils;

import org.ccs.apimaster.apifactory.supportcenter.actionee.EnvProperty;
import org.ccs.apimaster.apifactory.supportcenter.actionee.Parameterized;
import org.ccs.apimaster.apifactory.supportcenter.actionee.Step;
import org.ccs.apimaster.apifactory.supportcenter.actionee.TestMapping;
import org.ccs.apimaster.apifactory.propertymanager.MasterReportProperties;
import org.apache.commons.lang.StringUtils;

import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.ccs.apimaster.apifactory.utils.SmartUtils.getEnvPropertyValue;
import static org.ccs.apimaster.apifactory.utils.TokenUtils.getTestCaseTokens;
import static java.lang.System.getProperty;

/*
@Purpose: This class supports unit runner and package runner
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 15/12/2021
*/
public class RunnerUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(RunnerUtils.class);
    public static final int MIN_COUNT = 1;

    public static String getEnvSpecificConfigFile(String serverEnv, Class<?> testClass) {
        LOGGER.info("###[CCS Master] : testClass : " + testClass);

        final EnvProperty envProperty = testClass.getAnnotation(EnvProperty.class);

        if(envProperty == null){
            return serverEnv;
        }

        String envPropNameWithPrefix = envProperty.value();

        List<String> allTokens = getTestCaseTokens(envPropNameWithPrefix);

        if(allTokens.size() >= 1 && null != getEnvPropertyValue(allTokens.get(0))){

            final String propertyKey = allTokens.get(0);
            final String propertyValue = getEnvPropertyValue(propertyKey);

            Map<String, String> paramMap = new HashMap<>();
            paramMap.put(propertyKey, propertyValue);

            final String resolvedEnvPropNameWithPrefix = SmartUtils.resolveToken(envPropNameWithPrefix, paramMap);

            serverEnv = suffixEnvValue(serverEnv, resolvedEnvPropNameWithPrefix);

            LOGGER.info("[CCS Master] : Found env specific property: '{}={}', Hence using: '{}'", propertyKey, propertyValue, serverEnv);

        } else if(allTokens.size() >= 1) {

            final String propertyKey = allTokens.get(0);

            LOGGER.info("[CCS Master] : Could not find env value for env property '{}', So using '{}'", propertyKey, serverEnv);

        } else {

            LOGGER.info("[CCS Master] : Could not find env specific property, So using '{}'", serverEnv);

        }

        return serverEnv;
    }

    public static String suffixEnvValue(String serverEnv, String resolvedEnvPropNameWithPrefix) {
        final String DOT_PROPERTIES = ".properties";
        return serverEnv.replace(DOT_PROPERTIES, resolvedEnvPropNameWithPrefix + DOT_PROPERTIES);
    }

    public static String getFullyQualifiedUrl(String serviceEndPoint,
                                              String host,
                                              String port,
                                              String applicationContext) {
        if (serviceEndPoint.startsWith("http://") || serviceEndPoint.startsWith("https://")) {
            return serviceEndPoint;

        } else if(StringUtils.isEmpty(port)){
            return String.format("%s%s%s", host, applicationContext, serviceEndPoint);

        } else {

            return String.format("%s:%s%s%s", host, port, applicationContext, serviceEndPoint);
        }
    }

    public static void validateTestMethod(Class<?> testClass) {
        String errMessage = " was invalid, please re-check and pick the correct test method to load.";
        try {
            TestMapping methodMapping = testClass.getAnnotation(TestMapping.class);
            errMessage = "Mapped test method `" + methodMapping.testMethod() + "`" + errMessage;
            methodMapping.testClass().getMethod(methodMapping.testMethod());
        } catch (NoSuchMethodException e) {
            LOGGER.error(errMessage);
            throw new RuntimeException(errMessage + e);
        }
    }

    public static int loopCount(Step thisStep) {
        int stepLoopTimes = 0;

        if(thisStep.getLoop() != null){
            stepLoopTimes = thisStep.getLoop();
        } else if(thisStep.getParameterized() != null){
            stepLoopTimes = thisStep.getParameterized().size();
        } else if(thisStep.getParameterizedCsv() != null){
            stepLoopTimes = thisStep.getParameterizedCsv().size();
        }

        return stepLoopTimes > 0 ? stepLoopTimes: MIN_COUNT;
    }

    public static int getParameterSize(Parameterized parameterized) {
        if (parameterized == null) {
            return 0;
        }

        List<Object> valueSource = parameterized.getValueSource();
        List<String> csvSource = parameterized.getCsvSource();

        return valueSource != null ? valueSource.size() :
                (csvSource != null ? csvSource.size() : 0);
    }

    public static void handleTestCompleted(RunListener reportListener, Logger logger) {
        if (MasterReportProperties.CHARTS_AND_CSV.equals(getProperty(MasterReportProperties.CCS_MASTER_JUNIT))) {
            logger.debug("[CCS Master] : Bypassed JUnit RunListener [as configured by the build tool] to generate useful reports...");
            try {
                reportListener.testRunFinished(new Result());
            } catch (Exception e) {
                logger.error("###[CCS Master] : Exception occurred while handling non-maven(e.g. Gradle) report generation => " + e);
                throw new RuntimeException(e);
            }
        }
    }
}
