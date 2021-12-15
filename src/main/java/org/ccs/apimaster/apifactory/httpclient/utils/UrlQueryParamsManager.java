/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.httpclient.utils;

import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static java.util.Optional.ofNullable;

/*
@Purpose: This class manages query params in url
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 02/12/2021
*/
public class UrlQueryParamsManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(UrlQueryParamsManager.class);

    public static String setQueryParams(final String httpUrl, final Map<String, Object> queryParams) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(httpUrl);
        Map<String, Object> nullSafeQueryParams = ofNullable(queryParams).orElseGet(HashMap::new);
        nullSafeQueryParams.keySet().forEach(key ->
                uriBuilder.addParameter(key, nullSafeQueryParams.get(key).toString())
        );
        String composedURL = uriBuilder.build().toString();
        LOGGER.info("### Effective url is : {}", composedURL);
        return composedURL;
    }
}
