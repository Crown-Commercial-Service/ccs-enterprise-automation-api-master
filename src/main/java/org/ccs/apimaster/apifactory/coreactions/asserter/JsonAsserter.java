/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.coreactions.asserter;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
@Purpose: This class manage assertions and verifications to match the fields in Json
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 29/11/2021
*/
public interface JsonAsserter {
    Logger logger = LoggerFactory.getLogger(JsonAsserter.class);

    /*
    @Method: assertWithJson
    @Purpose: To read and handle project level test properties
    @Author: Mibin Boban, CCS Senior QAT Analyst
    @Creation: 29/10/2021
    */
    default FieldAssertionMatcher assertJson(String jsonSource) {

        Object expResult = null;
        try{
            expResult = JsonPath.read(jsonSource, getJsonPath());

        } catch(PathNotFoundException pEx){
            logger.warn("Failed! Path: {} was not found in the response. Hence this value was treated as null.", getJsonPath());
        }

        return assertActualToExpected(expResult);
    }

    default FieldAssertionMatcher reportDefaultAssertion(Object actualResult, boolean areEqual) {
        return areEqual ? FieldAssertionMatcher.reportMatchingMessage() : FieldAssertionMatcher.reportNotMatchingMessage(getJsonPath(), getExpectedValue(), actualResult);
    }

    String getJsonPath();

    Object getExpectedValue();

    FieldAssertionMatcher assertActualToExpected(Object result);

}
