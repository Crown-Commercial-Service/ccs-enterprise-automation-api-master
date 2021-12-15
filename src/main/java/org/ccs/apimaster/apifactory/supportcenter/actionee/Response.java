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
public class Response {
    private final int status;
    private final Map headers;
    private final JsonNode body;
    private final String rawBody;
    private final String location;

    @JsonCreator
    public Response(
                    @JsonProperty("status") int status,
                    @JsonProperty("headers") Map headers,
                    @JsonProperty("body") JsonNode body,
                    @JsonProperty("rawBody") String rawBody,
                    @JsonProperty("location") String location) {
        this.headers = headers;
        this.body = body;
        this.status = status;
        this.rawBody = rawBody;
        this.location = location;
    }

    public Map getHeaders() {
        return headers;
    }

    public JsonNode getBody() {
        return body;
    }

    public int getStatus() {
        return status;
    }
    
    public String getRawBody() {
        return rawBody;
    }
    
    public String getLocation() {
        return location;
    }
    
    @Override
    public String toString() {
        return "Response{" +
               "status=" + status +
               ", headers=" + headers +
               ", body=" + body +
               ", rawBody='" + rawBody + '\'' +
               ", location='" + location + '\'' +
               '}';
    }
}
