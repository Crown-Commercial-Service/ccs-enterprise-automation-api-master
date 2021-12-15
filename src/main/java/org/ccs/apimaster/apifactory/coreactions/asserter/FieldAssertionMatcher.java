/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */

package org.ccs.apimaster.apifactory.coreactions.asserter;

/*
@Purpose: This class manage assertions and verifications to match the fields in Json
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 29/11/2021
*/
public class FieldAssertionMatcher {

    private final Object expectedField;
    private final Object actualField;
    private final String jsonPath;

    FieldAssertionMatcher(String path, Object expectedField, Object actualField) {
        this.jsonPath = path;
        this.expectedField = expectedField;
        this.actualField = actualField;
    }

    public Object getExpectedField() {
        return expectedField;
    }

    public Object getActualField() {
        return actualField;
    }

    public String getJsonPath() {
        return jsonPath;
    }

    FieldAssertionMatcher(String path) {
        this(path, null, null);
    }

    public boolean matches() {
        return null == getJsonPath();
    }

    /*
    @Method: reportMatchingMessage
    @Purpose: To return success if fields are matching
    @Author: Mibin Boban, CCS Senior QAT Analyst
    @Creation: 29/10/2021
    */
    public static FieldAssertionMatcher reportMatchingMessage() {
        return new FieldAssertionMatcher(null);
    }

    /*
    @Method: reportNotMatchingMessage
    @Purpose: To return failure if fields are not matching
    @Author: Mibin Boban, CCS Senior QAT Analyst
    @Creation: 29/10/2021
    */
    public static FieldAssertionMatcher reportNotMatchingMessage(String path, Object expected, Object actual) {
        return new FieldAssertionMatcher(path, expected, actual);
    }

    @Override
    public String toString() {
        return matches() ?
                "Success! Actual field value matched the expected field value" :
                String.format("Failed! Assertion jsonPath '%s' with actual value '%s' did not match the expected value '%s'",
                        jsonPath, actualField, expectedField);
    }
}
