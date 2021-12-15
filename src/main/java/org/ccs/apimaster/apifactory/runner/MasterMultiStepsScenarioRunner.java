/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.runner;

import org.ccs.apimaster.apifactory.supportcenter.actionee.ScenarioSpec;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

import java.util.function.BiConsumer;

/*
@Purpose: This class manages multiple steps in a scenario
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 15/12/2021
*/
public interface MasterMultiStepsScenarioRunner {

    boolean runScenario(ScenarioSpec scenarioSpec, RunNotifier notifier, Description description);

    boolean runChildStep(ScenarioSpec scenarioSpec, BiConsumer<String, String> testPassHandler);

}
