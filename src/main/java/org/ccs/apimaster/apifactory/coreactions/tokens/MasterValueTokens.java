/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.coreactions.tokens;

import java.util.List;

import static java.util.Arrays.asList;

/*
@Purpose: This class manages dynamic placeholders
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 01/12/2021
*/
public class MasterValueTokens {
    public static final String JSON_PAYLOAD_FILE = "JSON.FILE:";
    public static final String PREFIX_ASU = "ASU";
    public static final String XML_FILE = "XML.FILE:";
    public static final String GQL_FILE = "GQL.FILE:";
    public static final String RANDOM_UU_ID = "RANDOM.UUID";
    public static final String RANDOM_UU_ID_FIXED = "RANDOM.UUID.FIXED";
    public static final String RECORD_DUMP = "RECORD.DUMP:";
    public static final String RANDOM_NUMBER = "RANDOM.NUMBER";
    public static final String RANDOM_NUMBER_FIXED = "CCS.MASTER.RANDOM.NUMBER.FIXED";
    public static final String RANDOM_STRING_ALPHA = "CCS.MASTER.RANDOM.STRING:";
    public static final String RANDOM_STRING_ALPHA_NUMERIC = "CCS.MASTER.RANDOM.ALPHANUMERIC:";
    public static final String RANDOM_STRING_EMAIL = "CCS.MASTER.RANDOM.EMAIL:";
    public static final String STATIC_ALPHABET = "STATIC.ALPHABET:";
    public static final String LOCALDATE_TODAY = "LOCAL.DATE.TODAY:";
    public static final String LOCALDATETIME_NOW = "LOCAL.DATETIME.NOW:";
    public static final String SYSTEM_PROPERTY = "SYSTEM.PROPERTY:";
    public static final String SYSTEM_ENV = "SYSTEM.ENV:";
    public static final String $VALUE = ".$VALUE";
    public static final String $VALINT = ".$VALINT";
    public static final String ABS_PATH = "ABS.PATH:";

    public static List<String> getKnownTokens() {
        return asList(
                PREFIX_ASU,
                RANDOM_NUMBER,
                RANDOM_STRING_ALPHA,
                RANDOM_STRING_ALPHA_NUMERIC,
                RANDOM_STRING_EMAIL,
                STATIC_ALPHABET,
                LOCALDATE_TODAY,
                LOCALDATETIME_NOW,
                SYSTEM_PROPERTY,
                XML_FILE,
                GQL_FILE,
                RANDOM_UU_ID,
                RECORD_DUMP,
                ABS_PATH,
                SYSTEM_ENV
        );
    }
}
