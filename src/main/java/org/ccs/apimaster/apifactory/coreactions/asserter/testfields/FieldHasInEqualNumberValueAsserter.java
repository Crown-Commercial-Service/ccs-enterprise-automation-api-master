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
import org.ccs.apimaster.apifactory.coreactions.asserter.NumberComparator;
import static org.ccs.apimaster.apifactory.coreactions.asserter.FieldAssertionMatcher.reportMatchingMessage;
import static org.ccs.apimaster.apifactory.coreactions.asserter.FieldAssertionMatcher.reportNotMatchingMessage;

/*
@Purpose: This class manage assertions to verify if field doesn't contain expected number
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 29/11/2021
*/
public class FieldHasInEqualNumberValueAsserter implements JsonAsserter {
    private final String path;
    private final Number expected;

    public FieldHasInEqualNumberValueAsserter(String path, Number expected) {
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
        boolean areNotEqual;

        if (result instanceof Number && expected instanceof Number) {
            NumberComparator comparator = new NumberComparator();
            areNotEqual = comparator.compare((Number) result, (Number) expected) != 0;

        } else if (result == null && expected == null) {
            areNotEqual = false;

        } else if (result == null) {
            areNotEqual = true;

        } else {
            areNotEqual = true;

        }

        return areNotEqual ?
                reportMatchingMessage() :
                reportNotMatchingMessage(path, "not equals to " + expected, result);
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

