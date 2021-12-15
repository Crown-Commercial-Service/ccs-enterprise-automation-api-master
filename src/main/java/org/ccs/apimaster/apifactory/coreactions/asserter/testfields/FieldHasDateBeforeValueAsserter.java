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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static org.ccs.apimaster.apifactory.coreactions.asserter.FieldAssertionMatcher.reportMatchingMessage;
import static org.ccs.apimaster.apifactory.coreactions.asserter.FieldAssertionMatcher.reportNotMatchingMessage;

/*
@Purpose: This class manage assertions to verify if field contains a date before value
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 29/11/2021
*/
public class FieldHasDateBeforeValueAsserter implements JsonAsserter {
    private final String path;
    private final LocalDateTime expected;

    public FieldHasDateBeforeValueAsserter(String path, LocalDateTime expected) {
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

        if (result == null && expected == null) {
            areEqual = true;
        } else if (result == null) {
            areEqual = false;
        } else {
            LocalDateTime resultDT = null;
            try {
                resultDT = LocalDateTime.parse((String) result,
                        DateTimeFormatter.ISO_DATE_TIME);
                areEqual = resultDT.isBefore(expected);
            } catch (DateTimeParseException ex) {
                areEqual = false;
            }
        }

        return areEqual ? reportMatchingMessage()
                : reportNotMatchingMessage(path, "Date Before:" + expected, result);
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
