/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */

package org.ccs.apimaster.apifactory.coreactions.asserter.testfields;

import org.ccs.apimaster.apifactory.mainmodule.support.ObjectMapperProvider;
import org.ccs.apimaster.apifactory.coreactions.asserter.FieldAssertionMatcher;
import org.ccs.apimaster.apifactory.coreactions.asserter.JsonAsserter;
import org.ccs.apimaster.apifactory.coreactions.scriptexecutor.javaapi.JavaCustomExecutor;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.ccs.apimaster.apifactory.coreactions.asserter.FieldAssertionMatcher.reportMatchingMessage;
import static org.ccs.apimaster.apifactory.coreactions.asserter.FieldAssertionMatcher.reportNotMatchingMessage;

/*
@Purpose: This class manage assertions to verify if field value matches with custom rule
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 29/11/2021
*/
public class FieldMatchesCustomAsserter implements JsonAsserter {
    private final String path;
    private final String expected;
    ObjectMapper mapper = new ObjectMapperProvider().get();

    public FieldMatchesCustomAsserter(String path, String expected) {
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
    public FieldAssertionMatcher assertActualToExpected(Object actual) {
        boolean areEqual = JavaCustomExecutor.executeMethod(expected, actual);
        return areEqual ?
                reportMatchingMessage() :
                reportNotMatchingMessage(path, " custom assertion:" + expected, actual);
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
