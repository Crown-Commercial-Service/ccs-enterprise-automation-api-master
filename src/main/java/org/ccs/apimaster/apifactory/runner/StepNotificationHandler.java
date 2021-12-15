/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.runner;

import org.ccs.apimaster.apifactory.coreactions.asserter.FieldAssertionMatcher;
import org.apache.commons.lang.StringUtils;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.util.Optional.ofNullable;

/*
@Purpose: This class manages notifications at steps
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 15/12/2021
*/
public class StepNotificationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(StepNotificationHandler.class);
    private final int MAX_LINE_LENGTH = 130;

    /*
      @Method: handleAssertionFailed
      @Purpose: To handle failed assertions
      @Author: Mibin Boban, CCS Senior QAT Analyst
      @Creation: 15/12/2021
    */
    Boolean handleAssertionFailed(RunNotifier notifier,
                    Description description,
                    String scenarioName,
                    String stepName,
                    List<FieldAssertionMatcher> failureReportList) {

        LOGGER.error(String.format("[CCS Master] : Failed assertion during Scenario:%s, --> Step:%s, Details: %s\n",
                        scenarioName, stepName, StringUtils.join(failureReportList, "\n")));
        String prettyFailureMessage =
                String.format("[CCS Master] : Assertion failed for :- \n\n[%s] \n\t|\n\t|\n\t+---Step --> [%s] \n\nFailures:\n--------- %n%s%n",
                scenarioName,
                stepName,
                StringUtils.join(failureReportList, "\n" + deckedUpLine(maxEntryLengthOf(failureReportList)) + "\n"));
        LOGGER.error(prettyFailureMessage + "(See below 'Actual Vs Expected' to learn why this step failed) \n");
        notifier.fireTestFailure(new Failure(description, new RuntimeException( prettyFailureMessage)));
        
        return false;
    }

    /*
      @Method: handleStepException
      @Purpose: To handle step exceptions
      @Author: Mibin Boban, CCS Senior QAT Analyst
      @Creation: 15/12/2021
    */
    Boolean handleStepException(RunNotifier notifier,
                    Description description,
                    String scenarioName,
                    String stepName,
                    Exception stepException) {
        LOGGER.error(String.format("[CCS Master] : Exception occurred while executing Scenario:[%s], --> Step:[%s], Details: %s",
                        scenarioName, stepName, stepException));
        notifier.fireTestFailure(new Failure(description, stepException));
        
        return false;
    }

    /*
      @Method: handleAssertionPassed
      @Purpose: To handle passed assertions
      @Author: Mibin Boban, CCS Senior QAT Analyst
      @Creation: 15/12/2021
    */
    Boolean handleAssertionPassed(RunNotifier notifier,
                    Description description,
                    String scenarioName,
                    String stepName,
                    List<FieldAssertionMatcher> failureReportList) {
        LOGGER.info("\n***[CCS Master] : Step PASSED - Scenario:{} -> {}", scenarioName, stepName);
        
        return true;
    }
    
    public <A, B, C, D, E, R> R handleAssertion(A var1,
                    B var2,
                    C var3,
                    D var4,
                    E var5,
                    Notifier<A, B, C, D, E, R> notifyFunc) {
        
        R result = notifyFunc.apply(var1, var2, var3, var4, var5);
        
        return result;
    }

    /*
      @Method: maxEntryLengthOf
      @Purpose: To handle max entry
      @Author: Mibin Boban, CCS Senior QAT Analyst
      @Creation: 15/12/2021
    */
    private int maxEntryLengthOf(List<FieldAssertionMatcher> failureReportList) {
        final Integer maxLength = ofNullable(failureReportList).orElse(Collections.emptyList()).stream()
                        .map(report -> report.toString().length())
                        .max(Comparator.naturalOrder())
                        //.min(Comparator.naturalOrder())
                        .get();
        return maxLength > MAX_LINE_LENGTH ? MAX_LINE_LENGTH : maxLength;
    }
    
    private String deckedUpLine(int stringLength) {
        final String DECKED_CHAR = "-";
        String dottedLine = "";
        for (int i = 0; i < stringLength; i++) {
            dottedLine = dottedLine + DECKED_CHAR;
        }
        return dottedLine;
    }
}
