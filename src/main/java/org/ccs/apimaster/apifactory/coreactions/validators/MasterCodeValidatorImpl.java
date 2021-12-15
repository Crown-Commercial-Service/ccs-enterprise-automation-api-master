/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.coreactions.validators;

import org.ccs.apimaster.apifactory.supportcenter.actionee.Step;
import org.ccs.apimaster.apifactory.supportcenter.actionee.Validator;
import org.ccs.apimaster.apifactory.coreactions.asserter.FieldAssertionMatcher;
import org.ccs.apimaster.apifactory.coreactions.asserter.JsonAsserter;
import org.ccs.apimaster.apifactory.coreactions.preparation.MasterAssertionsProcessor;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.jayway.jsonpath.JsonPath;

import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static org.ccs.apimaster.apifactory.utils.HelperJsonUtils.strictComparePayload;
import static org.slf4j.LoggerFactory.getLogger;

/*
@Purpose: This class manages functions for validation rules
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 01/12/2021
*/
public class MasterCodeValidatorImpl implements MasterCodeValidator {
    private static final Logger LOGGER = getLogger(MasterCodeValidatorImpl.class);

    private final MasterAssertionsProcessor masterAssertionsProcessor;

    @Inject
    public MasterCodeValidatorImpl(MasterAssertionsProcessor masterAssertionsProcessor) {
        this.masterAssertionsProcessor = masterAssertionsProcessor;
    }

    /*
    @Method: validateRuleFlat
    @Purpose: To validate fields via flat validators
    @Author: Mibin Boban, CCS Senior QAT Analyst
    @Creation: 01/12/2021
    */
    @Override
    public List<FieldAssertionMatcher> validateRuleFlat(Step thisStep, String actualResult) {
        LOGGER.info("Comparing results via flat validators");
        List<FieldAssertionMatcher> failureResults = new ArrayList<>();
        List<Validator> validators = thisStep.getValidators();

        for (Validator validator : validators) {
            String josnPath = validator.getField();
            JsonNode expectedValue = validator.getValue();
            Object actualValue = JsonPath.read(actualResult, josnPath);

            List<JsonAsserter> asserters = masterAssertionsProcessor.createJsonAsserters(expectedValue.toString());

            failureResults.addAll(masterAssertionsProcessor.assertAllAndReturnFailed(asserters, actualValue.toString()));
        }

        return failureResults;
    }

    /*
   @Method: validateRuleStrict
   @Purpose: To validate fields via strict matchers
   @Author: Mibin Boban, CCS Senior QAT Analyst
   @Creation: 01/12/2021
   */
    @Override
    public List<FieldAssertionMatcher> validateRuleStrict(String expectedResult, String actualResult) {
        LOGGER.info("Comparing results via STRICT matchers");

        return strictComparePayload(expectedResult, actualResult);
    }

    /*
   @Method: validateRuleStrict
   @Purpose: To validate fields via lenient matchers
   @Author: Mibin Boban, CCS Senior QAT Analyst
   @Creation: 01/12/2021
   */
    @Override
    public List<FieldAssertionMatcher> validateRuleLenient(String expectedResult, String actualResult) {
        LOGGER.info("Comparing results via LENIENT matchers");

        List<JsonAsserter> asserters = masterAssertionsProcessor.createJsonAsserters(expectedResult);
        return masterAssertionsProcessor.assertAllAndReturnFailed(asserters, actualResult);
    }
}
