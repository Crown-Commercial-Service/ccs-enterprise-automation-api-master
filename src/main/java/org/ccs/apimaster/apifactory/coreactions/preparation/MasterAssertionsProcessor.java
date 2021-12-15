/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.coreactions.preparation;

import org.ccs.apimaster.apifactory.coreactions.asserter.FieldAssertionMatcher;
import org.ccs.apimaster.apifactory.coreactions.asserter.JsonAsserter;

import java.util.List;

/*
@Purpose: This interface manages the assetions made at test case level
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 29/11/2021
*/
public interface MasterAssertionsProcessor {

    String manageStringJson(String requestJsonAsString, String resolvedScenarioState);

    String manageKnownTokensAndProperties(String requestJsonOrAnyString);

    String manageJsonPaths(String resolvedFromTemplate, String jsonString);

    List<String> getAllJsonPathTokens(String requestJsonAsString);

    List<JsonAsserter> createJsonAsserters(String resolvedAssertionJson);

    List<FieldAssertionMatcher> assertAllAndReturnFailed(List<JsonAsserter> asserters, String executionResult);
}
