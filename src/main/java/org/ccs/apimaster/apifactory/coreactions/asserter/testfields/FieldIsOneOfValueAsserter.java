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
import java.util.Arrays;
import static org.apache.commons.lang.StringUtils.substringBetween;

/*
@Purpose: This class manage assertions to verify if field contains a value from list
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 29/11/2021
*/
public class FieldIsOneOfValueAsserter implements JsonAsserter {
	private final String path;
	final Object expected;

	/*
   @Method: assertActualToExpected
   @Purpose: To assert the expected and actual values from json field
   @Author: Mibin Boban, CCS Senior QAT Analyst
   @Creation: 29/10/2021
   */
	@Override
	public FieldAssertionMatcher assertActualToExpected(Object actualResult) {
		boolean areEqual;

		if (expected != null) {
            String expectedString = substringBetween((String) expected, "[", "]");

			String[] expectedArray = null;

			if (!expectedString.isEmpty()) {
				expectedArray = expectedString.split(",");
			} else {
				expectedArray = new String[] {};
			}

			for (int i = 0; i < expectedArray.length; i++) {
				if (!expectedArray[i].trim().isEmpty())
					expectedArray[i] = expectedArray[i].trim();
			}

			if (actualResult != null) {
				areEqual = Arrays.asList(expectedArray).contains(actualResult);
			} else {
				areEqual = false;
			}
		} else {
			if (actualResult == null) {
				areEqual = true;
			} else {
				areEqual = false;
			}
		}

		return areEqual ? FieldAssertionMatcher.reportMatchingMessage()
				: FieldAssertionMatcher.reportNotMatchingMessage(path, "One Of:" + expected, actualResult);
	}

	public FieldIsOneOfValueAsserter(String path, Object expected) {
		this.path = path;
		this.expected = expected;
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