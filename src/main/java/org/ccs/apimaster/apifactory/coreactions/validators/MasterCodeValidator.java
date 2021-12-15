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
import org.ccs.apimaster.apifactory.coreactions.asserter.FieldAssertionMatcher;

import java.util.List;

/*
@Purpose: This class manages level of validation for field values
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 01/12/2021
*/
public interface MasterCodeValidator {

    List<FieldAssertionMatcher> validateRuleFlat(Step thisStep, String actualResult);

    List<FieldAssertionMatcher> validateRuleStrict(String expectedResult, String actualResult);

    List<FieldAssertionMatcher> validateRuleLenient(String expectedResult, String actualResult);

}
