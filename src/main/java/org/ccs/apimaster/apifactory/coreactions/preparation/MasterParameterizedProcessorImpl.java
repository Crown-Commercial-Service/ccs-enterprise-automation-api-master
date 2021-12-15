/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.coreactions.preparation;

import org.ccs.apimaster.apifactory.supportcenter.actionee.ScenarioSpec;
import org.ccs.apimaster.apifactory.propertymanager.MasterProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.univocity.parsers.csv.CsvParser;
import org.apache.commons.lang.text.StrSubstitutor;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static org.ccs.apimaster.apifactory.mainmodule.support.CsvParserProvider.LINE_SEPARATOR;
import static org.slf4j.LoggerFactory.getLogger;

/*
@Purpose: This class manages functions for parameterised execution
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 30/11/2021
*/
@Singleton
public class MasterParameterizedProcessorImpl implements MasterParameterizedProcessor {
    private static final Logger LOGGER = getLogger(MasterParameterizedProcessorImpl.class);

    public static final String VALUE_SOURCE_KEY = "0";

    private final ObjectMapper objectMapper;

    private final CsvParser csvParser;

    @Inject
    public MasterParameterizedProcessorImpl(ObjectMapper objectMapper, CsvParser csvParser) {
        this.objectMapper = objectMapper;
        this.csvParser = csvParser;
    }

    /*
    @Method: manageParameterisedTests
    @Purpose: To manage parameters for testcase
    @Author: Mibin Boban, CCS Senior QAT Analyst
    @Creation: 30/11/2021
    */
    @Override
    public ScenarioSpec manageParameterisedTests(ScenarioSpec scenario, int iteration) {

        if(scenario.getParameterized() == null){

            return scenario;

        } else if (scenario.getParameterized().getValueSource() != null) {

            return manageParamValues(scenario, iteration);

        } else if (scenario.getParameterized().getCsvSource() != null) {

            return manageParamsCsv(scenario, iteration);

        }

        throw new RuntimeException("Scenario spec was invalid. Please check the DSL format \ne.g. \n" + MasterProperties.DSL_FORMAT);
    }

    /*
   @Method: manageParamValues
   @Purpose: To manage parameter values for test execution
   @Author: Mibin Boban, CCS Senior QAT Analyst
   @Creation: 30/11/2021
   */
    private ScenarioSpec manageParamValues(ScenarioSpec scenario, int paramIndex) {
        LOGGER.info("Resolving parameter value-source for index - {}", paramIndex);

        try {
            String stepJson = objectMapper.writeValueAsString(scenario);
            List<Object> parameterized = scenario.getParameterized().getValueSource();

            if (parameterized == null || parameterized.isEmpty()) {
                return scenario;
            }

            Map<String, Object> valuesMap = new HashMap<>();
            valuesMap.put(VALUE_SOURCE_KEY, parameterized.get(paramIndex));

            String resultantStepJson = replaceWithValues(stepJson, valuesMap);

            return objectMapper.readValue(resultantStepJson, ScenarioSpec.class);

        } catch (Exception exx) {
            throw new RuntimeException("Error while resolving parameterized values for a scenario - " + exx);
        }
    }

    /*
   @Method: manageParamsCsv
   @Purpose: To manage parameters in CSV block
   @Author: Mibin Boban, CCS Senior QAT Analyst
   @Creation: 30/11/2021
   */
    private ScenarioSpec manageParamsCsv(ScenarioSpec scenario, int paramIndex) {
        LOGGER.info("Resolving parameter CSV-source for row number - {}", paramIndex);
        try {
            String stepJson = objectMapper.writeValueAsString(scenario);
            List<String> parameterizedCsvList = scenario.getParameterized().getCsvSource();

            if (parameterizedCsvList == null || parameterizedCsvList.isEmpty()) {
                return scenario;
            }

            Map<String, Object> valuesMap = new HashMap<>();
            String csvLine = parameterizedCsvList.get(paramIndex);

            manageCsvLine(valuesMap, csvLine);

            String resultantStepJson = replaceWithValues(stepJson, valuesMap);

            return objectMapper.readValue(resultantStepJson, ScenarioSpec.class);

        } catch (Exception exx) {
            throw new RuntimeException("Error while resolving parameterizedCsv values - " + exx);
        }
    }

    /*
   @Method: manageCsvLine
   @Purpose: To manage CSV block for parameterisation
   @Author: Mibin Boban, CCS Senior QAT Analyst
   @Creation: 30/11/2021
   */
    private void manageCsvLine(Map<String, Object> valuesMap, String csvLine) {
        String[] parsedLine = csvParser.parseLine(csvLine + LINE_SEPARATOR);
        AtomicLong index = new AtomicLong(0);
        Arrays.stream(parsedLine)
                .forEach(thisValue -> valuesMap.put(index.getAndIncrement() + "", thisValue));
    }

    private String replaceWithValues(String stepJson, Map<String, Object> valuesMap) {
        StrSubstitutor sub = new StrSubstitutor(valuesMap);
        return sub.replace(stepJson);
    }

}
