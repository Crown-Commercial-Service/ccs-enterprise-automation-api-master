/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.coreactions.scriptexecutor.javaapi;

import org.ccs.apimaster.apifactory.mainmodule.support.ObjectMapperProvider;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/*
@Purpose: This class manages executable classes based on json
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 01/12/2021
*/
public class JavaCustomExecutor {

    public static Boolean executeMethod(String fqMethodNameWithRawParams, Object actualFieldValue) {
        ObjectMapper mapper = new ObjectMapperProvider().get();
        try {
            String[] parts = fqMethodNameWithRawParams.split("#");
            String className = parts[0];

            String methodNameWithVal = parts[1];
            String[] methodParts = methodNameWithVal.split(":", 2);
            String methodName = methodParts[0];

            String expectedRawJsonArgs = methodParts[1];
            HashMap<String, Object> valueMap = mapper.readValue(expectedRawJsonArgs, HashMap.class);

            Class<?> clazz = Class.forName(className);
            Method method = clazz.getMethod(methodName, Map.class, Object.class);

            Object result = method.invoke(clazz.newInstance(), valueMap, actualFieldValue);

            return (Boolean) result;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
