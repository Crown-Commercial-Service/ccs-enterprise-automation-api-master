/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.supportcenter.actionee;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

/*
@Purpose: This class acts as actionee for api requests section in tests
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 15/12/2021
*/
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Request {
    private final Map<String, Object> headers;
    private final Map<String, Object> queryParams;
    private final JsonNode body;

    @JsonCreator
    public Request(
            @JsonProperty("headers")Map<String, Object> headers,
            @JsonProperty("queryParams")Map<String, Object> queryParams,
            @JsonProperty("body")JsonNode body) {
        this.headers = headers;
        this.queryParams = queryParams;
        this.body = body;
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }

    public Map<String, Object> getQueryParams() {
        return queryParams;
    }

    public JsonNode getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Request{" +
                "headers=" + headers +
                ", queryParams=" + queryParams +
                ", body='" + body + '\'' +
                '}';
    }
}
