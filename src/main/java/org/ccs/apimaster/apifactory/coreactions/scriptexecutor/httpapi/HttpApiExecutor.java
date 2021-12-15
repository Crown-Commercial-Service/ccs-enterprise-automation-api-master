/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.coreactions.scriptexecutor.httpapi;

/*
@Purpose: This class acts as master class for http-api executor
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 01/12/2021
*/
public interface HttpApiExecutor {
    String executeApi(String urlName, String methodName, String requestJson) throws Exception;
}
