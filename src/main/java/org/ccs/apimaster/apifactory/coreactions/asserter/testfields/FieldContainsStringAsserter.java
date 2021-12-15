/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */

package org.ccs.apimaster.apifactory.coreactions.asserter.testfields;

import org.ccs.apimaster.apifactory.coreactions.asserter.FieldAssertionMatcher;
import org.ccs.apimaster.apifactory.coreactions.asserter.JsonAsserter;

import static org.ccs.apimaster.apifactory.coreactions.asserter.FieldAssertionMatcher.reportMatchingMessage;
import static org.ccs.apimaster.apifactory.coreactions.asserter.FieldAssertionMatcher.reportNotMatchingMessage;

/*
@Purpose: This class manage assertions to verify if field contains a string
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 29/11/2021
*/
public class FieldContainsStringAsserter implements JsonAsserter {
    private final String jsonPath;
    private final String expectedValue;

    public FieldContainsStringAsserter(String jsonPath, String expectedValue) {
        this.jsonPath = jsonPath;
        this.expectedValue = expectedValue;
    }

    /*
    @Method: assertActualToExpected
    @Purpose: To assert the expected and actual values from json field
    @Author: Mibin Boban, CCS Senior QAT Analyst
    @Creation: 29/11/2021
    */
    @Override
    public FieldAssertionMatcher assertActualToExpected(Object result) {
        boolean areEqual;
        if (result instanceof String && expectedValue instanceof String) {
            String s1 = (String) result;
            String s2 = expectedValue;
            areEqual = s1.contains(s2);
        } else {
            areEqual = false;
        }

        return areEqual ?
                reportMatchingMessage() :
                reportNotMatchingMessage(jsonPath, "containing sub-string:" + expectedValue, result);
    }

    @Override
    public String getJsonPath() {
        return jsonPath;
    }

    @Override
    public Object getExpectedValue() {
        return expectedValue;
    }

}
