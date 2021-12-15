/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.supportcenter.reportutils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;

/*
@Purpose: This class manages local date and time de-serialization
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 15/12/2021
*/
public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
    @Override
    public LocalDateTime deserialize(JsonParser arg0, DeserializationContext arg1) throws IOException, JsonProcessingException {
        return LocalDateTime.parse(arg0.getText());
    }
}