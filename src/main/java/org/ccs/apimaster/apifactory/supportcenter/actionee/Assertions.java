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

/*
@Purpose: This class acts as actionee for assertions
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 15/12/2021
*/
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Assertions {
    private final Integer status;
    private final JsonNode body;

    @JsonCreator
    public Assertions(
            @JsonProperty("status") Integer status,
            @JsonProperty("body") JsonNode body) {
        this.status = status;
        this.body = body;
    }

    public Integer getStatus() {
        return status;
    }

    public JsonNode getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Assertions{" +
                "status=" + status +
                ", body=" + body +
                '}';
    }
}
