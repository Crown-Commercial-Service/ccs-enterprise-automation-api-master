/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.templateformatter;

import org.ccs.apimaster.apifactory.utils.SmartUtils;
import java.util.HashMap;
import java.util.Map;

/*
@Purpose: This class manages formatting of SOAP tests
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 15/12/2021
*/
public class SoapFormatter {

    public Object soapResponseXml(String nothing){

        try {
            final String rawBody = SmartUtils.readJsonAsString("soap_response/mock_soap_response.xml");
            Map<String, String> singleKeyValueMap = new HashMap<>();
            singleKeyValueMap.put("rawBody", rawBody);

            return singleKeyValueMap;

        } catch (RuntimeException  e) {
            throw new RuntimeException("something wrong happened here" + e);
        }
    }
}
