/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.supportcenter.actionee;

import org.ccs.apimaster.apifactory.mainmodule.support.ObjectMapperProvider;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.util.HashMap;
import java.util.Map;

/*
@Purpose: This class acts as actionee for test mapping
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 15/12/2021
*/
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestMapper {
    private final String name;
    private final String operation;
    private final String url;
    private final JsonNode request;
    private final JsonNode response;
    private final JsonNode assertions;

    private String body;

    private String headers;

    private Map<String, Object> headersMap;

    public String getName() {
        return name;
    }

    public String getOperation() {
        return operation;
    }

    public String getUrl() {
        return url;
    }

    public JsonNode getRequest() {
        return request;
    }

    public JsonNode getResponse() {
        return response;
    }

    public JsonNode getAssertions() {
        return assertions;
    }

    public String getBody() {
        final JsonNode bodyNode = request.get("body");
        return bodyNode != null ? request.get("body").toString() : null;
    }

    public String getHeaders() {
        return request.get("headers").toString();
    }

    public Map<String, Object> getHeadersMap() {
        ObjectMapper objectMapper = new ObjectMapperProvider().get();
        HashMap<String, Object> headersMap = new HashMap<>();
        try {
            final JsonNode headersNode = request.get("headers");
            if (null != headersNode) {
                headersMap = (HashMap<String, Object>) objectMapper.readValue(headersNode.toString(), HashMap.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return headersMap;
    }

    @JsonCreator
    public TestMapper(
            @JsonProperty("name") String name,
            @JsonProperty("operation") String operation,
            @JsonProperty("url") String url,
            @JsonProperty("request") JsonNode request,
            @JsonProperty("response") JsonNode response,
            @JsonProperty("assertions") JsonNode assertions) {
        this.name = name;
        this.operation = operation;
        this.request = request;
        this.url = url;
        this.response = response;
        this.assertions = assertions;
    }

    @Override
    public String toString() {
        return "Step{" +
                ", name='" + name + '\'' +
                ", operation='" + operation + '\'' +
                ", url='" + url + '\'' +
                ", request=" + request +
                ", response=" + response +
                ", assertions=" + assertions +
                '}';
    }
}
