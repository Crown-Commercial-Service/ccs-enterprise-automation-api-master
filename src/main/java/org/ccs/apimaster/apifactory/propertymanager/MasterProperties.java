/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.propertymanager;

import static org.ccs.apimaster.apifactory.utils.SmartUtils.readJsonAsString;

/*
@Purpose: This class manages properties
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 09/12/2021
*/
public interface MasterProperties {
    String PROPERTY_KEY_HOST = "restful.application.endpoint.host";
    String PROPERTY_KEY_PORT = "restful.application.endpoint.context";

    String OK = "Ok";
    String FAILED = "Failed";

    String DSL_FORMAT = readJsonAsString("dsl_formats/dsl_parameterized_values.json");
}
