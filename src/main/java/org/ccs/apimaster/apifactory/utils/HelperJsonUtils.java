/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.utils;

import org.ccs.apimaster.apifactory.mainmodule.support.ObjectMapperProvider;
import org.ccs.apimaster.apifactory.coreactions.asserter.FieldAssertionMatcher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import org.apache.commons.lang.StringUtils;
import org.jboss.resteasy.client.ClientRequest;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.skyscreamer.jsonassert.JSONCompareMode.STRICT;

/*
@Purpose: This class manages helper functions for json
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 15/12/2021
*/
public class HelperJsonUtils {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(HelperJsonUtils.class);
    private static ObjectMapper mapper = new ObjectMapperProvider().get();


    public static String getContentAsItIsJson(Object bodyContent) {

        if (null == bodyContent) {
            return null;
        }

        final JsonNode bodyJsonNode;
        try {

            final String bodyContentAsString = mapper.writeValueAsString(bodyContent);
            bodyJsonNode = mapper.readValue(bodyContentAsString, JsonNode.class);

            if (bodyJsonNode.isValueNode()) {
                return bodyJsonNode.asText();
            }

            if (bodyJsonNode.size() == 0) {
                return null;
            }

            return bodyJsonNode.toString();

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    public static Map<String, Object> readObjectAsMap(Object jsonContent) {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            map = mapper.readValue(jsonContent.toString(), HashMap.class);
        } catch (IOException exx) {
            LOGGER.error("[CCS Master] : Exception occurred during parse to HashMap - " + exx);
            throw new RuntimeException(exx);
        }

        return map;
    }

    public static String createAndReturnAssertionResultJson(int httpResponseCode,
                                                            String resultBodyContent, String locationHref) {
        LOGGER.debug("\n#locationHref: " + locationHref);

        if (StringUtils.isEmpty(resultBodyContent)) {
            resultBodyContent = "{}";
        }
        String locationField = locationHref != null ? "	\"Location\" : \"" + locationHref + "\",\n" : "";
        String assertJson = "{\n" +
                "	\"status\" : " + httpResponseCode + ",\n" +
                locationField +
                "	\"body\" : " + resultBodyContent + "\n" +
                " }";

        String formattedStr = SmartUtils.prettyPrintJson(assertJson);

        return formattedStr;
    }

    private void setRequestHeaders(Object headers, ClientRequest clientExecutor) {
        Map<String, Object> headersMap = HelperJsonUtils.readObjectAsMap(headers);
        for (Object key : headersMap.keySet()) {
            clientExecutor.header((String) key, headersMap.get(key));
        }
    }

    public static String javaObjectAsString(Object value) {

        try {
            ObjectMapper ow = new ObjectMapperProvider().get();
            return ow.writeValueAsString(value);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Exception while converting IPT Java Object to JsonString" + e);
        }
    }


    public static List<FieldAssertionMatcher> strictComparePayload(String expectedResult, String actualResult) {
        List<FieldAssertionMatcher> matchers = new ArrayList<>();

        String actualResultJson = readPayload(actualResult);
        String expectedResultJson = readPayload(expectedResult);
        try {
            assertEquals(expectedResultJson, actualResultJson, STRICT);
        } catch (AssertionError scream) {
            String rawMsg = scream.getMessage();
            List<String> messageList = Arrays.asList(rawMsg.split(";"));
            matchers = messageList.stream()
                    .map(msg -> {
                        List<String> strings = Arrays.asList(msg.trim().split("\n"));
                        String fieldJsonPath = "";
                        if(strings != null && strings.size() > 0){
                            fieldJsonPath = strings.get(0).substring(strings.get(0).indexOf(": ") + 1).trim();
                        }
                        if (strings.size() == 1) {
                            return FieldAssertionMatcher.reportNotMatchingMessage(fieldJsonPath, "", strings.get(0).trim());
                        } else if (strings.size() == 2) {
                            return FieldAssertionMatcher.reportNotMatchingMessage(fieldJsonPath, "", strings.get(1).trim());
                        } else if (strings.size() > 2) {
                            return FieldAssertionMatcher.reportNotMatchingMessage(fieldJsonPath, strings.get(1).trim(), strings.get(2).trim());
                        } else {
                            return FieldAssertionMatcher.reportMatchingMessage();
                        }
                    })
                    .collect(Collectors.toList());
        }

        return matchers;
    }

    private static String readPayload(String json) {
        String bodyPath = "$.body";
        String rawBodyPath = "$.rawBody";

        Map payload = (Map) readJsonPathOrElseNull(json, bodyPath);
        payload = payload == null ? (Map) readJsonPathOrElseNull(json, rawBodyPath) : payload;

        try {
            return mapper.writeValueAsString(payload);
        } catch (JsonProcessingException ex) {
            LOGGER.debug("Exception while reading payload - " + ex);
            throw new RuntimeException(ex);
        }
    }

    public static Object readJsonPathOrElseNull(String requestJson, String jsonPath) {
        try {
            return JsonPath.read(requestJson, jsonPath);
        } catch (PathNotFoundException pEx) {
            LOGGER.debug("No " + jsonPath + " was present in the request. returned null.");
            return null;
        }
    }
}
