/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.coreactions.scriptexecutor;

import org.ccs.apimaster.apifactory.coreactions.scriptexecutor.httpapi.HttpApiExecutor;
import org.ccs.apimaster.apifactory.coreactions.scriptexecutor.javaapi.JavaMethodExecutor;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
@Purpose: This class manages functions for API service executor
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 01/12/2021
*/
public class ApiServiceExecutorImpl implements ApiServiceExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiServiceExecutorImpl.class);

    @Inject
    private JavaMethodExecutor javaMethodExecutor;

    @Inject
    private HttpApiExecutor httpApiExecutor;

    @Inject(optional = true)
    @Named("mock.api.port")
    private int mockPort;

    public ApiServiceExecutorImpl() {
    }

    /*
    @Method: executeHttpApi
    @Purpose: To execute API service for Http API
    @Author: Mibin Boban, CCS Senior QAT Analyst
    @Creation: 01/12/2021
    */
    @Override
    public String executeHttpApi(String url, String methodName, String requestJson) {
        try {
            return httpApiExecutor.executeApi(url, methodName, requestJson);
        } catch (Throwable severError) {
            LOGGER.error("Failure! Something unexpected happened while connecting to the url:{} " +
                    "\n1) Check if the service is running at the host -or-" +
                    "\n2) Check the corporate proxy has been configured correctly -or" +
                    "\n3) Choose another mocking(if in use) port not to conflict with the port:{} -or-" +
                    "\n4) Restart the service. -or- " +
                    "See the full error details below-\n{}", url, mockPort, severError);

            throw new RuntimeException(severError);
        }
    }

    /*
    @Method: executeJavaOperation
    @Purpose: To execute API service for Java API
    @Author: Mibin Boban, CCS Senior QAT Analyst
    @Creation: 01/12/2021
    */
    @Override
    public String executeJavaOperation(String className, String methodName, String requestJson) {
        try{
            return javaMethodExecutor.executeMethod(className, methodName, requestJson);
        } catch (Exception e) {
            LOGGER.error("Java method execution exception - " + e);
            throw new RuntimeException(e);
        }
    }
}
