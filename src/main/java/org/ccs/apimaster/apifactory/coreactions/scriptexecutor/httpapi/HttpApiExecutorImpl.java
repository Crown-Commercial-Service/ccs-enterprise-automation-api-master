/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.coreactions.scriptexecutor.httpapi;

import org.ccs.apimaster.apifactory.supportcenter.actionee.PackageMapper;
import org.ccs.apimaster.apifactory.supportcenter.actionee.Response;
import org.ccs.apimaster.apifactory.httpclient.BasicHttpClient;
import org.ccs.apimaster.apifactory.utils.SmartUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.util.HashMap;

import static org.ccs.apimaster.apifactory.coreactions.datamocker.RestEndPointMocker.*;
import static org.ccs.apimaster.apifactory.utils.SmartUtils.prettyPrintJson;
import static org.apache.commons.lang.StringUtils.isEmpty;

/*
@Purpose: This class manages functions for http-api executor
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 01/12/2021
*/
public class HttpApiExecutorImpl implements HttpApiExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpApiExecutorImpl.class);

    private final BasicHttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Inject
    public HttpApiExecutorImpl(BasicHttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    @Inject
    private SmartUtils smartUtils;

    @Inject(optional = true)
    @Named("mock.api.port")
    private int mockPort;

    /*
    @Method: executeApi
    @Purpose: To execute API and read response
    @Author: Mibin Boban, CCS Senior QAT Analyst
    @Creation: 01/12/2021
    */
    @Override
    public String executeApi(String httpUrl, String methodName, String requestJson) throws Exception {

        HashMap queryParams = (HashMap) readJsonPathOrElseNull(requestJson, "$.queryParams");
        HashMap headers = (HashMap) readJsonPathOrElseNull(requestJson, "$.headers");
        Object bodyContent = readJsonPathOrElseNull(requestJson, "$.body");

        if (completedMockingEndPoints(httpUrl, requestJson, methodName, bodyContent)) {
            return "{\"status\": 200}";
        }

        final javax.ws.rs.core.Response serverResponse = httpClient.execute(httpUrl, methodName, headers, queryParams, bodyContent);

        final int responseStatus = serverResponse.getStatus();

        final MultivaluedMap responseHeaders = serverResponse.getMetadata();

        final String responseBodyAsString = (String) serverResponse.getEntity();

        Response apiResponse = deriveApiResponse(responseStatus, responseHeaders, responseBodyAsString);

        final String apiResponseString = objectMapper.writeValueAsString(apiResponse);

        return prettyPrintJson(apiResponseString);
    }

    /*
    @Method: deriveApiResponse
    @Purpose: To extract the API response body
    @Author: Mibin Boban, CCS Senior QAT Analyst
    @Creation: 01/12/2021
    */
    private Response deriveApiResponse(int responseStatus,
                                       MultivaluedMap responseHeaders,
                                       String responseBodyAsString)
            throws IOException {

        final JsonNode jsonBody;
        final String rawBody;

        if (isEmpty(responseBodyAsString)) {
            jsonBody = null;
            rawBody = null;

        } else if (isParsableJson(responseBodyAsString)) {
            jsonBody = objectMapper.readValue(responseBodyAsString, JsonNode.class);
            rawBody = null;

        } else {
            jsonBody = null;
            rawBody = responseBodyAsString;

        }

        return new Response(responseStatus, responseHeaders, jsonBody, rawBody, null);
    }

    /*
    @Method: completedMockingEndPoints
    @Purpose: To manage mocking endpoints on finish
    @Author: Mibin Boban, CCS Senior QAT Analyst
    @Creation: 01/12/2021
    */
    private boolean completedMockingEndPoints(String httpUrl, String requestJson, String methodName, Object bodyContent) throws IOException {
        if (httpUrl.contains("/$MOCK") && methodName.equals("$USE.WIREMOCK")) {

            PackageMapper packageMapper = smartUtils.getMapper().readValue(requestJson, PackageMapper.class);

            if (mockPort > 0) {
                createWithWireMock(packageMapper, mockPort);

                LOGGER.info("#SUCCESS: End points simulated via wiremock.");

                return true;
            }

            LOGGER.error("\n\n#DISABLED: Mocking was not activated as there was no port configured in the properties file. \n\n " +
                    "Usage: e.g. in your <env host config .properties> file provide- \n " +
                    "mock.api.port=8888\n\n");
            return false;
        } else if (httpUrl.contains("/$MOCK") && methodName.equals("$USE.VIRTUOSO")) {
            LOGGER.info("\n#body:\n" + bodyContent);
            createWithVirtuosoMock(bodyContent != null ? bodyContent.toString() : null);
            LOGGER.info("#SUCCESS: End point simulated via virtuoso.");
            return true;
        } else if (httpUrl.contains("/$MOCK") && methodName.equals("$USE.SIMULATOR")) {
            LOGGER.info("\n#body:\n" + bodyContent);
            createWithLocalMock(bodyContent != null ? bodyContent.toString() : null);
            LOGGER.info("#SUCCESS: End point simulated via local simulator.");
            return true;
        }
        return false;
    }

    /*
    @Method: readJsonPathOrElseNull
    @Purpose: To read json path
    @Author: Mibin Boban, CCS Senior QAT Analyst
    @Creation: 01/12/2021
    */
    private Object readJsonPathOrElseNull(String requestJson, String jsonPath) {
        try {
            return JsonPath.read(requestJson, jsonPath);
        } catch (PathNotFoundException pEx) {
            LOGGER.debug("No " + jsonPath + " was present in the request. returned null.");
            return null;
        }
    }

    /*
    @Method: isParsableJson
    @Purpose: To parse the json file
    @Author: Mibin Boban, CCS Senior QAT Analyst
    @Creation: 01/12/2021
    */
    private boolean isParsableJson(String potentialJsonString) {
        try {
            objectMapper.readTree(potentialJsonString);
            return true;
        } catch (IOException e) {
            LOGGER.warn("\n---------------------------------------------\n\n"
                    + "\t\t\t\t\t\t * Warning *  \n\nOutput was not a valid JSON body. It was treated as a simple rawBody."
                    + " If it was intentional, you can ignore this warning. "
                    + "\n -OR- Update your assertions block with \"rawBody\" instead of \"body\" "
                    + "\n e.g. \"rawBody\" : \"an expected string \""
                    + "\n\n---------------------------------------------");
            return false;
        }
    }
}
