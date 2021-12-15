/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.supportcenter.actionee;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/*
@Purpose: This class acts as interface for test mapping annotation
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 15/12/2021
*/
@Retention(RetentionPolicy.RUNTIME)
@Repeatable( value = TestMappings.class )
public @interface TestMapping {
    Class<?> testClass();
    String testMethod();
}