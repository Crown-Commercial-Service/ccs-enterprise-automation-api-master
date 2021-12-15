/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.templateformatter;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

/*
@Purpose: This class manages formatting of test case
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 15/12/2021
*/
public interface Formatter {
    Object xmlToJson(String xmlObject);

    Object stringToJson(String jsonString) throws IOException;

    Object jsonToJson(String jsonString) throws IOException;

    Object jsonBlockToJson(JsonNode jsonNode) throws IOException;

    default Object jsonNodeToJson(JsonNode jsonNode) throws IOException {
        return jsonBlockToJson(jsonNode);
    }
}
