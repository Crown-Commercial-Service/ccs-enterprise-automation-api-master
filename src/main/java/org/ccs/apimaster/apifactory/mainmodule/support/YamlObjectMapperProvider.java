/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.mainmodule.support;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import javax.inject.Provider;

/*
@Purpose: This class supports Yaml Object mapper
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 09/12/2021
*/
public class YamlObjectMapperProvider implements Provider<ObjectMapper> {

    @Override
    public ObjectMapper get() {

        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);

        return objectMapper;
    }

}
