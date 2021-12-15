/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.coreactions.preparation;

import org.ccs.apimaster.apifactory.coreactions.asserter.FieldAssertionMatcher;
import org.ccs.apimaster.apifactory.coreactions.asserter.JsonAsserter;
import org.ccs.apimaster.apifactory.coreactions.asserter.tests.ArrayIsEmptyAsserter;
import org.ccs.apimaster.apifactory.coreactions.asserter.tests.SizeAsserter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.apache.commons.lang.text.StrSubstitutor;
import org.ccs.apimaster.apifactory.coreactions.asserter.testfields.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.ccs.apimaster.apifactory.coreactions.tokens.MasterAssertionTokens.*;
import static org.ccs.apimaster.apifactory.coreactions.tokens.MasterValueTokens.$VALUE;
import static org.ccs.apimaster.apifactory.utils.SmartUtils.isValidAbsolutePath;
import static org.ccs.apimaster.apifactory.utils.TokenUtils.getTestCaseTokens;
import static org.ccs.apimaster.apifactory.utils.TokenUtils.populateParamMap;
import static org.ccs.apimaster.apifactory.utils.FieldTypeConversionUtils.digTypeCast;
import static org.ccs.apimaster.apifactory.utils.FieldTypeConversionUtils.fieldTypes;
import static org.ccs.apimaster.apifactory.utils.PropertiesProviderUtils.loadAbsoluteProperties;
import static java.lang.String.format;
import static org.apache.commons.lang.StringEscapeUtils.escapeJava;
import static org.apache.commons.lang.StringUtils.substringBetween;

import static org.slf4j.LoggerFactory.getLogger;

/*
@Purpose: This class implements assertion operations logic
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 29/11/2021
*/
public class MasterAssertionsProcessorImpl implements MasterAssertionsProcessor {

    private static final org.slf4j.Logger LOGGER = getLogger(MasterAssertionsProcessorImpl.class);

    final List<String> frmPropertyKeys = new ArrayList<>();
    final Properties frmPropertyValues = new Properties();

    private final ObjectMapper objMapper;
    private final String hostFileName;

    @Inject
    public MasterAssertionsProcessorImpl(ObjectMapper objMapper, @Named("HostFileName") String hostFileName) {
        this.objMapper = objMapper;
        this.hostFileName = hostFileName;
        loadAnnotatedHostProperties();
    }

    /*
    @Method: manageStringJson
    @Purpose: To handle json test repo in string format
    @Author: Mibin Boban, CCS Senior QAT Analyst
    @Creation: 29/11/2021
    */
    @Override
    public String manageStringJson(String requestJsonOrAnyString, String scenarioStateJson) {
        String resolvedFromTemplate = manageKnownTokensAndProperties(requestJsonOrAnyString);
        String resolvedJson = manageJsonPaths(resolvedFromTemplate, scenarioStateJson);
        return resolveFieldTypes(resolvedJson);
    }

    /*
    @Method: manageKnownTokensAndProperties
    @Purpose: To handle token and properties for a json test
    @Author: Mibin Boban, CCS Senior QAT Analyst
    @Creation: 29/11/2021
    */
    @Override
    public String manageKnownTokensAndProperties(String requestJsonOrAnyString) {
        Map<String, Object> paramMap = new HashMap<>();

        final List<String> testCaseTokens = getTestCaseTokens(requestJsonOrAnyString);

        testCaseTokens.forEach(runTimeToken -> {
            populateParamMap(paramMap, runTimeToken);

            if (isPropertyKey(runTimeToken)) {
                paramMap.put(runTimeToken, frmPropertyValues.get(runTimeToken));
            }

        });

        StrSubstitutor sub = new StrSubstitutor(paramMap);

        return sub.replace(requestJsonOrAnyString);
    }

    /*
    @Method: manageJsonPaths
    @Purpose: To handle path reference for jsons
    @Author: Mibin Boban, CCS Senior QAT Analyst
    @Creation: 29/11/2021
    */
    @Override
    public String manageJsonPaths(String jsonString, String scenarioState) {
        List<String> jsonPaths = getAllJsonPathTokens(jsonString);
        Map<String, String> paramMap = new HashMap<>();
        final String LEAF_VAL_REGEX = "\\$[.](.*)\\$VALUE\\[\\d\\]";

        jsonPaths.forEach(thisPath -> {
            try {

                if (thisPath.endsWith(RAW_BODY)) {
                    String escapedString = escapeJava(JsonPath.read(scenarioState, thisPath));
                    paramMap.put(thisPath, escapedString);

                } else if (thisPath.matches(LEAF_VAL_REGEX) || thisPath.endsWith($VALUE)) {
                    resolveLeafOnlyNodeValue(scenarioState, paramMap, thisPath);

                } else {
                    Object jsonPathValue = JsonPath.read(scenarioState, thisPath);
                    if (isPathValueJson(jsonPathValue)) {
                        final String jsonAsString = objMapper.writeValueAsString(jsonPathValue);
                        String escapedJsonString = escapeJava(jsonAsString);
                        paramMap.put(thisPath, escapedJsonString);
                    } else {
                        paramMap.put(thisPath, JsonPath.read(scenarioState, thisPath));
                    }
                }

            } catch (Exception e) {
                throw new RuntimeException("\nJSON:" + jsonString + "\nPossibly comments in the JSON found or bad JSON path found: " + thisPath + ",\nDetails: " + e);
            }
        });

        StrSubstitutor sub = new StrSubstitutor(paramMap);

        return sub.replace(jsonString);
    }

    /*
    @Method: getAllJsonPathTokens
    @Purpose: To handle all tokens in test case
    @Author: Mibin Boban, CCS Senior QAT Analyst
    @Creation: 29/11/2021
    */
    @Override
    public List<String> getAllJsonPathTokens(String requestJsonAsString) {
        List<String> jsonPaths = new ArrayList<>();

        final List<String> allTokens = getTestCaseTokens(requestJsonAsString);
        allTokens.forEach(thisToken -> {
            if (thisToken.startsWith("$.")) {
                jsonPaths.add(thisToken);
            }
        });

        return jsonPaths;
    }

    /*
    @Method: createJsonAsserters
    @Purpose: To handle assrertions in json fields
    @Author: Mibin Boban, CCS Senior QAT Analyst
    @Creation: 29/11/2021
    */
    @Override
    public List<JsonAsserter> createJsonAsserters(String resolvedAssertionJson) {
        List<JsonAsserter> asserters = new ArrayList<>();
        try {
            JsonNode jsonNode = objMapper.readTree(resolvedAssertionJson);

            Map<String, Object> createFieldsKeyValuesMap = createAssertionKV(jsonNode, "$.");

            int i = 1;
            for (Map.Entry<String, Object> entry : createFieldsKeyValuesMap.entrySet()) {
                String path = entry.getKey();
                Object value = entry.getValue();

                JsonAsserter asserter;
                if (CCS_ASSERT_NOT_NULL.equals(value) || CCS_ASSERT_IS_NOT_NULL.equals(value)) {
                    asserter = new FieldIsNotNullAsserter(path);

                } else if (value instanceof String && ((String) value).startsWith(CCS_ASSERT_CUSTOM_ASSERT)) {
                    String expected = ((String) value).substring(CCS_ASSERT_CUSTOM_ASSERT.length());
                    asserter = new FieldMatchesCustomAsserter(path, expected);

                } else if (CCS_ASSERT_NULL.equals(value) || CCS_ASSERT_IS_NULL.equals(value)) {
                    asserter = new FieldIsNullAsserter(path);

                } else if (CCS_ASSERT_EMPTY_ARRAY.equals(value)) {
                    asserter = new ArrayIsEmptyAsserter(path);

                } else if (path.endsWith(CCS_ASSERT_PATH_SIZE)) {
                    path = path.substring(0, path.length() - CCS_ASSERT_PATH_SIZE.length());
                    if (value instanceof Number) {
                        asserter = new SizeAsserter(path, ((Integer) value).intValue());
                    } else if (value instanceof String) {
                        asserter = new SizeAsserter(path, (String) value);
                    } else {
                        throw new RuntimeException(format("Oops! Unsupported value for .SIZE: %s", value));
                    }
                } else if (value instanceof String && ((String) value).startsWith(CCS_ASSERT_CONTAINS_STRING)) {
                    String expected = ((String) value).substring(CCS_ASSERT_CONTAINS_STRING.length());
                    asserter = new FieldContainsStringAsserter(path, expected);
                } else if (value instanceof String && ((String) value).startsWith(CCS_ASSERT_MATCHES_STRING)) {
                    String expected = ((String) value).substring(CCS_ASSERT_MATCHES_STRING.length());
                    asserter = new FieldMatchesRegexPatternAsserter(path, expected);
                } else if (value instanceof String && ((String) value).startsWith(CCS_ASSERT_CONTAINS_STRING_IGNORE_CASE)) {
                    String expected = ((String) value).substring(CCS_ASSERT_CONTAINS_STRING_IGNORE_CASE.length());
                    asserter = new FieldContainsStringIgnoreCaseAsserter(path, expected);
                } else if (value instanceof String && (value.toString()).startsWith(CCS_ASSERT_EQUAL_TO_NUMBER)) {
                    String expected = ((String) value).substring(CCS_ASSERT_EQUAL_TO_NUMBER.length());
                    asserter = new FieldHasEqualNumberValueAsserter(path, numberValueOf(expected));
                } else if (value instanceof String && (value.toString()).startsWith(CCS_ASSERT_NOT_EQUAL_TO_NUMBER)) {
                    String expected = ((String) value).substring(CCS_ASSERT_NOT_EQUAL_TO_NUMBER.length());
                    asserter = new FieldHasInEqualNumberValueAsserter(path, numberValueOf(expected));
                } else if (value instanceof String && (value.toString()).startsWith(CCS_ASSERT_GREATER_THAN)) {
                    String expected = ((String) value).substring(CCS_ASSERT_GREATER_THAN.length());
                    asserter = new FieldHasGreaterThanValueAsserter(path, numberValueOf(expected));
                } else if (value instanceof String && (value.toString()).startsWith(CCS_ASSERT_LESSER_THAN)) {
                    String expected = ((String) value).substring(CCS_ASSERT_LESSER_THAN.length());
                    asserter = new FieldHasLesserThanValueAsserter(path, numberValueOf(expected));
                } else if (value instanceof String && (value.toString()).startsWith(CCS_ASSERT_LOCAL_DATETIME_AFTER)) {
                    String expected = ((String) value).substring(CCS_ASSERT_LOCAL_DATETIME_AFTER.length());
                    asserter = new FieldHasDateAfterValueAsserter(path, parseLocalDateTime(expected));
                } else if (value instanceof String && (value.toString()).startsWith(CCS_ASSERT_LOCAL_DATETIME_BEFORE)) {
                    String expected = ((String) value).substring(CCS_ASSERT_LOCAL_DATETIME_BEFORE.length());
                    asserter = new FieldHasDateBeforeValueAsserter(path, parseLocalDateTime(expected));
                } else if (value instanceof String && (value.toString()).startsWith(CCS_ASSERT_VALUE_ONE_OF) ||
                        value instanceof String && (value.toString()).startsWith(CCS_ASSERT_VALUE_IS_ONE_OF)) {
                    String expected = ((String) value).substring(CCS_ASSERT_VALUE_ONE_OF.length());
                    asserter = new FieldIsOneOfValueAsserter(path, expected);
                } else {
                    asserter = new FieldHasExactValueAsserter(path, value);
                }

                asserters.add(asserter);
            }
        } catch (IOException parEx) {
            throw new RuntimeException(parEx);
        }

        return asserters;
    }

    /*
    @Method: numberValueOf
    @Purpose: To format string values to numeric
    @Author: Mibin Boban, CCS Senior QAT Analyst
    @Creation: 30/11/2021
    */
    private BigDecimal numberValueOf(String expected) {
        try {
            return new BigDecimal(expected);
        } catch (Exception e) {
            String msg = "\nValue '" + expected + "' can not be converted to number:" + e;
            LOGGER.error(msg);
            throw new RuntimeException(msg);
        }
    }

    /*
    @Method: createAssertionKV
    @Purpose: To create key-value pair of fields values
    @Author: Mibin Boban, CCS Senior QAT Analyst
    @Creation: 30/11/2021
    */
    private Map<String, Object> createAssertionKV(JsonNode jsonNode, String pathDslPrefix) {
        HashMap<String, Object> resultMap = new HashMap<>();

        if (jsonNode.getNodeType().equals(JsonNodeType.OBJECT)) {
            jsonNode.fieldNames().forEachRemaining(fieldName -> {
                String qualifiedName = pathDslPrefix + fieldName;
                JsonNode thisNode = jsonNode.get(fieldName);

                if (thisNode.isValueNode()) {
                    Object value = convertJsonTypeToJavaType(jsonNode.get(fieldName));
                    resultMap.put(qualifiedName, value);

                } else {
                    String newPrefix = qualifiedName + ".";
                    resultMap.putAll(createAssertionKV(thisNode, newPrefix));

                }
            });

        } else if (jsonNode.getNodeType().equals(JsonNodeType.ARRAY)) {
            int i = 0;
            final Iterator<JsonNode> arrayIterator = jsonNode.elements();
            while (arrayIterator.hasNext()) {
                final JsonNode thisElementValue = arrayIterator.next();
                String elementName = String.format("%s[%d]", pathDslPrefix.substring(0, pathDslPrefix.lastIndexOf('.')), i++);

                if (thisElementValue.isValueNode()) {
                    Object value = convertJsonTypeToJavaType(thisElementValue);
                    resultMap.put(elementName, value);

                } else {
                    String newPrefix = elementName + ".";
                    resultMap.putAll(createAssertionKV(thisElementValue, newPrefix));

                }
            }
        }

        else if (jsonNode.isValueNode()) {
            Object value = convertJsonTypeToJavaType(jsonNode);
            resultMap.put("$", value);

        } else {
            throw new RuntimeException(format("Oops! Unsupported JSON Type: %s", jsonNode.getClass().getName()));

        }

        return resultMap;
    }

    /*
    @Method: convertJsonTypeToJavaType
    @Purpose: To convert type from Json to Java supported
    @Author: Mibin Boban, CCS Senior QAT Analyst
    @Creation: 30/11/2021
    */
    private Object convertJsonTypeToJavaType(JsonNode jsonNode) {
        if (jsonNode.isValueNode()) {
            if (jsonNode.isInt()) {
                return jsonNode.asInt();

            } else if (jsonNode.isTextual()) {
                return jsonNode.asText();

            } else if (jsonNode.isBoolean()) {
                return jsonNode.asBoolean();

            } else if (jsonNode.isLong()) {
                return jsonNode.asLong();

            } else if (jsonNode.isDouble()) {
                return jsonNode.asDouble();

            } else if (jsonNode.isNull()) {
                return null;

            } else {
                throw new RuntimeException(format("Oops! Unsupported JSON primitive to Java : %s by the framework", jsonNode.getClass().getName()));
            }
        } else {
            throw new RuntimeException(format("Unsupported JSON Type: %s", jsonNode.getClass().getName()));
        }
    }

    @Override
    public List<FieldAssertionMatcher> assertAllAndReturnFailed(List<JsonAsserter> asserters, String executionResult) {

        List<FieldAssertionMatcher> failedReports = new ArrayList<>();

        asserters.forEach(asserter -> {

            final FieldAssertionMatcher fieldMatcher = asserter.assertJson(executionResult);

            if (!fieldMatcher.matches()) {

                failedReports.add(fieldMatcher);

            }
        });

        return failedReports;
    }

    /*
    @Method: loadAnnotatedHostProperties
    @Purpose: To manage annotated host properties
    @Author: Mibin Boban, CCS Senior QAT Analyst
    @Creation: 30/11/2021
    */
    private void loadAnnotatedHostProperties() {
        try {
            if(isValidAbsolutePath(hostFileName)){
               loadAbsoluteProperties(hostFileName, frmPropertyValues);
            } else {
                frmPropertyValues.load(getClass().getClassLoader().getResourceAsStream(hostFileName));
            }

        } catch (Exception e) {
            String msg = "Problem encountered while accessing annotated host properties file '";
            LOGGER.error(msg + hostFileName + "'");
            System.err.println(msg + hostFileName + "'");
            throw new RuntimeException(msg + e);
        }

        frmPropertyValues.keySet().stream().forEach(thisKey -> {
            frmPropertyKeys.add(thisKey.toString());
        });
    }

    private boolean isPropertyKey(String runTimeToken) {
        return frmPropertyKeys.contains(runTimeToken);
    }

    private LocalDateTime parseLocalDateTime(String value) {
        return LocalDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME);
    }

    boolean isPathValueJson(Object jsonPathValue) {
        return jsonPathValue instanceof LinkedHashMap || jsonPathValue instanceof JSONArray;
    }

    void resolveLeafOnlyNodeValue(String scenarioState, Map<String, String> paramMap, String thisPath) {
        String actualPath = thisPath.substring(0, thisPath.indexOf($VALUE));
        int index = findArrayIndex(thisPath, actualPath);

        List<String> leafValuesAsArray = JsonPath.read(scenarioState, actualPath);
        paramMap.put(thisPath, leafValuesAsArray.get(index));
    }

    private int findArrayIndex(String thisPath, String actualPath) {
        String valueExpr = thisPath.substring(actualPath.length());
        if ($VALUE.equals(valueExpr)) {
            return 0;
        }
        return Integer.parseInt(substringBetween(valueExpr, "[", "]"));
    }

    /*
    @Method: resolveFieldTypes
    @Purpose: To manage different field types
    @Author: Mibin Boban, CCS Senior QAT Analyst
    @Creation: 30/11/2021
    */
    private String resolveFieldTypes(String resolvedJson) {
        try {
            if (hasNoTypeCast(resolvedJson)) {
                return resolvedJson;
            }

            Map<String, Object> fieldMap = objMapper.readValue(resolvedJson, new TypeReference<Map<String, Object>>() { });
            digTypeCast(fieldMap);

            return objMapper.writeValueAsString(fieldMap);

        } catch (Exception ex) {
            LOGGER.error("Field Type conversion exception. \nDetails:" + ex);
            throw new RuntimeException(ex);
        }
    }

    private boolean hasNoTypeCast(String resolvedJson) {
        long foundCount = fieldTypes.stream().filter(thisType -> resolvedJson.contains(thisType)).count();
        return foundCount <= 0 ? true : false;
    }


}
