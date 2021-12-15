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

/*
@Purpose: This class manage assertions to verify if field contains a string by ignoring case
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 29/11/2021
*/
public class FieldContainsStringIgnoreCaseAsserter implements JsonAsserter {
    private final String path;
    private final String expected;

    public FieldContainsStringIgnoreCaseAsserter(String path, String expected) {
        this.path = path;
        this.expected = expected;
    }

    /*
    @Method: assertActualToExpected
    @Purpose: To assert the expected and actual values from json field
    @Author: Mibin Boban, CCS Senior QAT Analyst
    @Creation: 29/10/2021
    */
    @Override
    public FieldAssertionMatcher assertActualToExpected(Object result) {
        boolean areEqual;
        if (result instanceof String && expected instanceof String) {
            String s1 = (String) result;
            String s2 = expected;
            areEqual = s1.toUpperCase().contains(s2.toUpperCase());
        } else {
            areEqual = false;
        }

        return areEqual ?
                FieldAssertionMatcher.reportMatchingMessage() :
                FieldAssertionMatcher.reportNotMatchingMessage(path, "containing sub-string with ignoring case:" + expected, result);
    }

    @Override
    public String getJsonPath() {
        return path;
    }

    @Override
    public Object getExpectedValue() {
        return expected;
    }

}
