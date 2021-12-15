/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */

package org.ccs.apimaster.apifactory.coreactions.scriptexecutor.javaapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;

import static org.ccs.apimaster.apifactory.utils.SmartUtils.prettyPrintJson;
import static java.lang.Class.forName;
import static java.lang.String.format;
import static java.util.Arrays.asList;

/*
@Purpose: This class manages functions for java method executor
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 01/12/2021
*/
public class JavaMethodExecutorImpl implements JavaMethodExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(JavaMethodExecutorImpl.class);

    private final Injector injector;

    private final ObjectMapper objectMapper;

    @Inject
    public JavaMethodExecutorImpl(Injector injector, ObjectMapper objectMapper) {
        this.injector = injector;
        this.objectMapper = objectMapper;
    }

    /*
    @Method: executeMethod
    @Purpose: To execute method in a executable java class - json based
    @Author: Mibin Boban, CCS Senior QAT Analyst
    @Creation: 01/12/2021
    */
    @Override
    public String executeMethod(String qualifiedClassName, String methodName, String requestJson) {

        try {
            List<Class<?>> parameterTypes = getParameterTypes(qualifiedClassName, methodName);

            Object result;

            if (parameterTypes == null || parameterTypes.size() == 0) {

                result = executeWithParams(qualifiedClassName, methodName);

            } else {

                Object request = objectMapper.readValue(requestJson, parameterTypes.get(0));
                result = executeWithParams(qualifiedClassName, methodName, request);
            }

            final String resultJson = objectMapper.writeValueAsString(result);
            return prettyPrintJson(resultJson);

        } catch (Exception e) {
            LOGGER.error("Exception - " + e);
            throw new RuntimeException(e);

        }
    }

    /*
    @Method: executeWithParams
    @Purpose: To execute method in a executable java class with params
    @Author: Mibin Boban, CCS Senior QAT Analyst
    @Creation: 01/12/2021
    */
    Object executeWithParams(String qualifiedClassName, String methodName, Object... params) {

        try {
            Method method = findMatchingMethod(qualifiedClassName, methodName);
            Object objectToInvokeOn = injector.getInstance(forName(qualifiedClassName));

            return method.invoke(objectToInvokeOn, params);
        } catch (Exception e) {
            String errMsg = format("Java exec(): Invocation failed for method %s in class %s", methodName, qualifiedClassName);
            LOGGER.error(errMsg + ". Exception - " + e);
            throw new RuntimeException(errMsg);
        }
    }

    /*
   @Method: findMatchingMethod
   @Purpose: To find method from executable java classes
   @Author: Mibin Boban, CCS Senior QAT Analyst
   @Creation: 01/12/2021
   */
    Method findMatchingMethod(String className, String methodName) {
        try{

            Class<?> clazz = forName(className);

            Method[] allMethods = clazz.getDeclaredMethods();
            for (Method m : allMethods) {
                if (m.getName().equals(methodName)) {
                    return m;
                }
            }

            throw new RuntimeException(format("Java exec(): No matching method %s found in class %s", methodName, className));

        } catch(Exception e){
            LOGGER.error("Exception occurred while finding the matching method - " + e);
            throw new RuntimeException(e);
        }
    }

    List<Class<?>> getParameterTypes(String className, String methodName) {
        return asList(findMatchingMethod(className, methodName).getParameterTypes());
    }

}
