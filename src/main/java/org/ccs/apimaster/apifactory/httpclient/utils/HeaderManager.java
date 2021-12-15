/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.httpclient.utils;

import org.ccs.apimaster.apifactory.httpclient.BasicHttpClient;
import org.apache.http.client.methods.RequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/*
@Purpose: This class manages headers
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 02/12/2021
*/
public class HeaderManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(HeaderManager.class);

    /*
    @Method: processFrameworkDefault
    @Purpose: To create file upload request
    @Author: Mibin Boban, CCS Senior QAT Analyst
    @Creation: 02/12/2021
    */
    public static void processFrameworkDefault(Map<String, Object> headers, RequestBuilder requestBuilder) {
        if (headers != null) {
            Map headersMap = headers;
            for (Object key : headersMap.keySet()) {
                if(BasicHttpClient.CONTENT_TYPE.equalsIgnoreCase((String)key) && BasicHttpClient.MULTIPART_FORM_DATA.equals(headersMap.get(key))){
                    continue;
                }
                removeDuplicateHeaders(requestBuilder, (String) key);
                requestBuilder.addHeader((String) key, (String) headersMap.get(key));
                LOGGER.info("Overridden the header key:{}, with value:{}", key, headersMap.get(key));
            }
        }
    }

    public static void removeDuplicateHeaders(RequestBuilder requestBuilder, String key) {
        if (requestBuilder.getFirstHeader(key) != null) {
            requestBuilder.removeHeaders(key);
        }
    }
}
