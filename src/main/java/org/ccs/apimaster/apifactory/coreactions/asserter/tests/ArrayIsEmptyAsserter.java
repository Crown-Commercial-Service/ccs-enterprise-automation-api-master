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

/*
@Purpose: This class manage assertions to verify if a test is empty
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 29/11/2021
*/
public class ArrayIsEmptyAsserter implements JsonAsserter {
    private final String path;

    public ArrayIsEmptyAsserter(String path) {
        this.path = path;
    }

    /*
    @Method: assertActualToExpected
    @Purpose: To assert the expected and actual values from json field
    @Author: Mibin Boban, CCS Senior QAT Analyst
    @Creation: 29/10/2021
    */
    @Override
    public FieldAssertionMatcher assertActualToExpected(Object result) {
        if(result instanceof JSONArray){

            final JSONArray actualArrayValue = (JSONArray) result;

            if(actualArrayValue.isEmpty()){

                return FieldAssertionMatcher.reportMatchingMessage();
            }

            return FieldAssertionMatcher.reportNotMatchingMessage(path, "[]", result);

        } else {

            return FieldAssertionMatcher.reportNotMatchingMessage(path, "[]", result);

        }
    }

    @Override
    public String getJsonPath() {
        return path;
    }

    @Override
    public Object getExpectedValue() {
        return "[]";
    }

}
