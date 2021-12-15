/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.utils;

import org.ccs.apimaster.apifactory.mainmodule.support.ObjectMapperProvider;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;

/*
@Purpose: This class manages filed type conversions
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 15/12/2021
*/
public class FieldTypeConversionUtils {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(FieldTypeConversionUtils.class);
    private static ObjectMapper mapper = new ObjectMapperProvider().get();

    public static final String INT = "(int)";
    public static final String LONG = "(long)";
    public static final String FLOAT = "(float)";
    public static final String BOOLEAN = "(boolean)";
    public static final String DECIMAL = "(decimal)";

    public static List<String> fieldTypes = Arrays.asList(INT, FLOAT, BOOLEAN, DECIMAL, LONG);

    static Function<String, Integer> integerFunction = (input) ->
            Integer.valueOf(input.substring(INT.length()));

    static Function<String, Long> longFunction = input ->
            Long.valueOf(input.substring(LONG.length()));

    static Function<String, Float> floatFunction = (input) ->
            Float.valueOf(input.substring(FLOAT.length()));

    static Function<String, Float> decimalFunction = (input) ->
            Float.valueOf(input.substring(FLOAT.length()));

    static Function<String, Boolean> booleanFUnction = (input) ->
            Boolean.valueOf(input.substring(BOOLEAN.length()));

    static Map<String, Function> typeMap = new HashMap<String, Function>() {
        {
            put(INT, integerFunction);
            put(LONG, longFunction);
            put(FLOAT, floatFunction);
            put(DECIMAL, decimalFunction);
            put(BOOLEAN, booleanFUnction);
        }
    };

    public static void digTypeCast(Map<String, Object> map) {

        map.entrySet().stream().forEach(entry -> {

            Object value = entry.getValue();

            if (value instanceof Map) {
                digTypeCast((Map<String, Object>) value);

            } else if (value instanceof ArrayList) {
                ((ArrayList) value).forEach(thisItem -> {
                    if (thisItem instanceof Map) {
                        digTypeCast((Map<String, Object>) thisItem);
                    }
                    LOGGER.debug("ARRAY - Leaf node found = {}, checking for type value...", thisItem);
                    replaceNodeValue(entry, thisItem);
                });
            } else {
                LOGGER.debug("Leaf node found = {}, checking for type value...", value);
                replaceNodeValue(entry, value);
            }
        });
    }


    private static void replaceNodeValue(Map.Entry<String, Object> entry, Object thisItem) {
        try {
            if (thisItem != null) {
                fieldTypes.stream().forEach(currentType -> {
                    if (thisItem.toString().startsWith(currentType)) {
                        entry.setValue((typeMap.get(currentType)).apply(thisItem.toString()));
                    }
                });
            }
        } catch (Exception exx) {
            String errorMsg = "Can not convert '" + entry.getValue() + "'.";
            LOGGER.error(errorMsg + "\nException Details:" + exx);
            throw new RuntimeException(errorMsg + exx);
        }

    }
}
