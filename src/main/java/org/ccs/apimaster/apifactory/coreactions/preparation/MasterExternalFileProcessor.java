/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.coreactions.preparation;

import org.ccs.apimaster.apifactory.supportcenter.actionee.Step;

import java.util.List;

/*
@Purpose: This class acts as master file processor
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 30/11/2021
*/
public interface MasterExternalFileProcessor {

    Step manageExtJsonFile(Step thisStep);

    List<Step> buildFromStepFile(Step thisStep, String stepId);
}
