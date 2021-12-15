/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.coreactions.tokens;

/*
@Purpose: This class manages assertion placeholders
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 01/12/2021
*/
public class MasterAssertionTokens {
    public static final String CCS_ASSERT_CONTAINS_STRING = "$CONTAINS.STRING:";
    public static final String CCS_ASSERT_MATCHES_STRING = "$MATCHES.STRING:";
    public static final String CCS_ASSERT_EQUAL_TO_NUMBER = "$EQ.";
    public static final String CCS_ASSERT_NOT_EQUAL_TO_NUMBER = "$NOT.EQ.";
    public static final String CCS_ASSERT_GREATER_THAN = "$GT.";
    public static final String CCS_ASSERT_LESSER_THAN = "$LT.";
    public static final String CCS_ASSERT_IS_NOT_NULL = "$IS.NOTNULL";
    public static final String CCS_ASSERT_NOT_NULL = "$NOT.NULL";
    public static final String CCS_ASSERT_IS_NULL = "$IS.NULL";
    public static final String CCS_ASSERT_NULL = "$NULL";
    public static final String CCS_ASSERT_CUSTOM_ASSERT = "$CUSTOM.ASSERT:";
    public static final String CCS_ASSERT_EMPTY_ARRAY = "$[]";
    public static final String CCS_ASSERT_PATH_SIZE = ".SIZE";
    public static final String CCS_ASSERT_CONTAINS_STRING_IGNORE_CASE = "$CONTAINS.STRING.IGNORECASE:";
    public static final String CCS_ASSERT_LOCAL_DATETIME_AFTER = "$LOCAL.DATETIME.AFTER:";
    public static final String CCS_ASSERT_LOCAL_DATETIME_BEFORE = "$LOCAL.DATETIME.BEFORE:";
    public static final String CCS_ASSERT_VALUE_ONE_OF = "$ONE.OF:";
    public static final String CCS_ASSERT_VALUE_IS_ONE_OF = "$IS.ONE.OF:";
    public static final String ASSERT_PATH_VALUE_NODE = "$";
    public static final String RAW_BODY = ".rawBody";

}
