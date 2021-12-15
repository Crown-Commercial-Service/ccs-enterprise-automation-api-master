/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.utils;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isEmpty;

/*
@Purpose: This class manages support to different types of tests
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 15/12/2021
*/
public class TestTypeSupport {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestTypeSupport.class);

    public static final String JAVA_API_PROTOCOL_MAPPINGS = "java.api.protocol.mappings";

    @Inject(optional = true)
    @Named(JAVA_API_PROTOCOL_MAPPINGS)
    private String javaApiProtoMappings;

    public TestTypeSupport() {
    }

    public TestTypeSupport(String javaApiProtoMappings) {
        this.javaApiProtoMappings = javaApiProtoMappings;
    }

    public static TestType apiType(String serviceName, String methodName) {
        TestType testType;

        if (StringUtils.isEmpty(serviceName) || isEmpty(methodName)) {
            testType = TestType.NONE;

        } else if (serviceName.contains("://") && !serviceName.startsWith("http")) {
            testType = TestType.JAVA_CALL;

        } else if (serviceName != null && serviceName.contains("/")) {
            testType = TestType.REST_CALL;


        } else {
            testType = TestType.JAVA_CALL;

        }

        return testType;
    }

    public String getQualifiedJavaApi(String url) {
        if (!url.contains("://")){
            return url;
        }
        return findMapping(javaApiProtoMappings, url);
    }

    private String findMapping(String javaApiProtoMappings, String url) {
        LOGGER.info("Locating protocol service mapping for - '{}'", url);

        if (isEmpty(javaApiProtoMappings)) {
            LOGGER.error("Protocol mapping was null or empty. Please create the mappings first and then rerun");
            throw new RuntimeException("\nProtocol mapping was null or empty.");
        }
        List<String> mappingList = Arrays.asList(javaApiProtoMappings.split(","));
        String foundMapping = mappingList.stream()
                .filter(thisMapping -> thisMapping.startsWith(url))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("\nurl '" + url + "' Not found"));

        String qualifiedClazz = foundMapping.split("\\|")[1];
        LOGGER.info("Found protocol mapping for - '{} -> {}'", url, qualifiedClazz);

        return qualifiedClazz;
    }
}
