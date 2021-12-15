/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */

package org.ccs.apimaster.apifactory.coreactions.asserter.tests;

import org.ccs.apimaster.apifactory.coreactions.asserter.FieldAssertionMatcher;
import org.ccs.apimaster.apifactory.coreactions.asserter.JsonAsserter;
import net.minidev.json.JSONArray;

import static org.ccs.apimaster.apifactory.coreactions.tokens.MasterAssertionTokens.*;

/*
@Purpose: This class manage assertions to verify size of test scenario
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 29/11/2021
*/
public class SizeAsserter implements JsonAsserter {
    private final String path;
    private final int expectedSize;
    private final String expectedSizeExpression;

    public SizeAsserter(String path, int size) {
        this.path = path;
        expectedSize = size;
        expectedSizeExpression = null;
    }

    public SizeAsserter(String path, String expression) {
        this.path = path;
        expectedSizeExpression = expression;
        expectedSize = -1;
    }

    @Override
    public String getJsonPath() {
        return path;
    }

    @Override
    public Object getExpectedValue() {
        return expectedSize;
    }

    @Override
    public FieldAssertionMatcher assertActualToExpected(Object result) {
        if (result instanceof JSONArray) {

            final JSONArray actualArrayValue = (JSONArray) result;

            if (this.expectedSize == -1 && this.expectedSizeExpression != null) {

                return processRelationalExpression(actualArrayValue);

            }

            if (actualArrayValue.size() == this.expectedSize) {

                return FieldAssertionMatcher.reportMatchingMessage();
            }

            return FieldAssertionMatcher.reportNotMatchingMessage(
                    path,
                    String.format("Array of size %d", expectedSize),
                    actualArrayValue.size());

        } else {

            return FieldAssertionMatcher.reportNotMatchingMessage(path, "[]", result);

        }
    }

    public FieldAssertionMatcher processRelationalExpression(JSONArray actualArrayValue) {
        if (expectedSizeExpression.startsWith(CCS_ASSERT_GREATER_THAN)) {
            String greaterThan = this.expectedSizeExpression.substring(CCS_ASSERT_GREATER_THAN.length());
            if (actualArrayValue.size() > Integer.parseInt(greaterThan)) {
                return FieldAssertionMatcher.reportMatchingMessage();
            }
        } else if (expectedSizeExpression.startsWith(CCS_ASSERT_LESSER_THAN)) {
            String lesserThan = this.expectedSizeExpression.substring(CCS_ASSERT_LESSER_THAN.length());
            if (actualArrayValue.size() < Integer.parseInt(lesserThan)) {
                return FieldAssertionMatcher.reportMatchingMessage();
            }
        } else if (expectedSizeExpression.startsWith(CCS_ASSERT_EQUAL_TO_NUMBER)) {
            String equalTo = this.expectedSizeExpression.substring(CCS_ASSERT_EQUAL_TO_NUMBER.length());
            if (actualArrayValue.size() == Integer.parseInt(equalTo)) {
                return FieldAssertionMatcher.reportMatchingMessage();
            }
        } else if (expectedSizeExpression.startsWith(CCS_ASSERT_NOT_EQUAL_TO_NUMBER)) {
            String notEqualTo = this.expectedSizeExpression.substring(CCS_ASSERT_NOT_EQUAL_TO_NUMBER.length());
            if (actualArrayValue.size() != Integer.parseInt(notEqualTo)) {
                return FieldAssertionMatcher.reportMatchingMessage();
            }
        }

        return FieldAssertionMatcher.reportNotMatchingMessage(
                path,
                String.format("Array of size %s", expectedSizeExpression),
                actualArrayValue.size());
    }


}
