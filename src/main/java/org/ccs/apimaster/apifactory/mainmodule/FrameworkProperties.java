/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.mainmodule;

/*
@Purpose: This class manages properties placeholder
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 09/12/2021
*/
public interface FrameworkProperties {

    String WEB_APPLICATION_ENDPOINT_HOST="web.application.endpoint.host";
    String WEB_APPLICATION_ENDPOINT_PORT="web.application.endpoint.port";
    String WEB_APPLICATION_ENDPOINT_CONTEXT="web.application.endpoint.context";

    String RESTFUL_APPLICATION_ENDPOINT_HOST="restful.application.endpoint.host";
    String RESTFUL_APPLICATION_ENDPOINT_PORT="restful.application.endpoint.port";
    String RESTFUL_APPLICATION_ENDPOINT_CONTEXT="restful.application.endpoint.context";
}
