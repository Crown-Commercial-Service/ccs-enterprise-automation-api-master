/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.coreactions.preparation;

import org.ccs.apimaster.apifactory.supportcenter.actionee.Step;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.ccs.apimaster.apifactory.utils.TokenUtils.getTestCaseTokens;
import static org.ccs.apimaster.apifactory.coreactions.tokens.MasterValueTokens.JSON_PAYLOAD_FILE;
import static org.ccs.apimaster.apifactory.utils.SmartUtils.readJsonAsString;
import static org.slf4j.LoggerFactory.getLogger;

/*
@Purpose: This class manages functions for file processing
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 30/11/2021
*/
@Singleton
public class MasterExternalFileProcessorImpl implements MasterExternalFileProcessor {
    private static final Logger LOGGER = getLogger(MasterExternalFileProcessorImpl.class);

    private final ObjectMapper objectMapper;

    @Inject
    public MasterExternalFileProcessorImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /*
    @Method: manageExtJsonFile
    @Purpose: To manage json files which are testcases
    @Author: Mibin Boban, CCS Senior QAT Analyst
    @Creation: 30/11/2021
    */
    @Override
    public Step manageExtJsonFile(Step thisStep) {

        try {

            if (!checkRefNeeded(thisStep)) {
                return thisStep;
            }

            JsonNode stepNode = objectMapper.convertValue(thisStep, JsonNode.class);

            Map<String, Object> stepMap = objectMapper.readValue(stepNode.toString(), new TypeReference<Map<String, Object>>() {
            });

            manageExtFileReferences(stepMap);

            JsonNode jsonStepNode = objectMapper.valueToTree(stepMap);

            return objectMapper.treeToValue(jsonStepNode, Step.class);

        } catch (Exception exx) {

            LOGGER.error("External file reading exception - {}", exx.getMessage());

            throw new RuntimeException("External file reading exception. Details - " + exx);

        }

    }

    /*
    @Method: buildFromStepFile
    @Purpose: To build steps in scenarios
    @Author: Mibin Boban, CCS Senior QAT Analyst
    @Creation: 30/11/2021
    */
    @Override
    public List<Step> buildFromStepFile(Step thisStep, String stepId) {
        List<Step> thisSteps = new ArrayList<>();
        if (thisStep.getStepFile() != null) {
            try {
                thisSteps.add(objectMapper.treeToValue(thisStep.getStepFile(), Step.class));
            } catch (JsonProcessingException e) {
                LOGGER.error("\n### Error while parsing for stepId - {}, stepFile - {}",
                        stepId, thisStep.getStepFile());
                throw new RuntimeException(e);
            }
        } else if(null != thisStep.getStepFiles() && !thisStep.getStepFiles().isEmpty()) {
            try {
                for(int i = 0; i < thisStep.getStepFiles().size(); i++)
                    thisSteps.add(objectMapper.treeToValue(thisStep.getStepFiles().get(i), Step.class));
            } catch (JsonProcessingException e) {
                LOGGER.error("\n### Error while parsing for stepId - {}, stepFile - {}",
                        stepId, thisStep.getStepFiles());
                throw new RuntimeException(e);
            }
        }
        return thisSteps;
    }

    /*
    @Method: manageExtFileReferences
    @Purpose: To manage external json file references inside a test case
    @Author: Mibin Boban, CCS Senior QAT Analyst
    @Creation: 30/11/2021
    */
    void manageExtFileReferences(Map<String, Object> map) {

        map.entrySet().stream().forEach(entry -> {

            Object value = entry.getValue();

            if (value instanceof Map) {
                manageExtFileReferences((Map<String, Object>) value);

            } else {
                LOGGER.debug("Leaf node found = {}, checking for any external json file...", value);
                if (value != null && value.toString().contains(JSON_PAYLOAD_FILE)) {
                    LOGGER.info("Found external JSON file place holder = {}. Replacing with content", value);
                    String valueString = value.toString();
                    String token = getJsonFileToken(valueString);
                    if (token != null && token.startsWith(JSON_PAYLOAD_FILE)) {
                        String resourceJsonFile = token.substring(JSON_PAYLOAD_FILE.length());
                        try {
                            JsonNode jsonNode = objectMapper.readTree(readJsonAsString(resourceJsonFile));
                            if (jsonNode.isObject()) {
                                final Map<String, Object> jsonFileContent = objectMapper.convertValue(jsonNode, Map.class);
                                manageExtFileReferences(jsonFileContent);
                                jsonNode = objectMapper.convertValue(jsonFileContent, JsonNode.class);
                            }
                            entry.setValue(jsonNode);
                        } catch (Exception exx) {
                            LOGGER.error("External file reference exception - {}", exx.getMessage());
                            throw new RuntimeException(exx);
                        }
                    }

                }
            }
        });
    }

    private String getJsonFileToken(String valueString) {
        if (valueString != null) {
            List<String> allTokens = getTestCaseTokens(valueString);
            if (allTokens != null && !allTokens.isEmpty()) {
                return allTokens.get(0);
            }
        }
        return null;
    }

    boolean checkRefNeeded(Step thisStep) throws JsonProcessingException {
        String stepJson = objectMapper.writeValueAsString(thisStep);
        List<String> allTokens = getTestCaseTokens(stepJson);
        return allTokens.toString().contains(JSON_PAYLOAD_FILE);
    }

}
