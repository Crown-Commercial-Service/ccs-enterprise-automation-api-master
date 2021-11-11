/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor:
 * Authors:
 * Contributors:
 */
package ccs.master.execution_manager.monitoring;

import ccs.master.execution_manager.reporting.TestReporter;
import ccs.master.support_centre.filehelper.ConfigFileHelper;
import ccs.master.support_centre.propertymanager.CcsMasterPropertyHandler;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import java.util.logging.Logger;

/*
@Class: RetryAnalyzer
@Purpose: This class manages the retry for failed tests
@Author: Mibin Boban, CCS Test Analyst
@Creation: 03/11/2021
*/
public class RetryAnalyzer implements IRetryAnalyzer {
    protected Logger log;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    private int count = 0;
    private int maxCount= CcsMasterPropertyHandler.TESTCASE_RETRY.toInteger();

    /*
    @Method: retry
    @Purpose: To manage the retry when execution of test fails
    @Author: Mibin Boban, CCS Test Analyst
    @Creation: 03/11/2021
    */
    @Override
    public boolean retry(ITestResult result) {
        System.err.println(String.format("[%s]\t\t[FAILED]", result.getMethod().getMethodName()));
        String failCount = null;
        if (count < maxCount) {
            count++;
            failCount = ""+count;
            String TestCaseID = result.getMethod().getMethodName();
            ConfigFileHelper.writeConfigFile("./retry.properties", TestCaseID, failCount);
            TestReporter.reportLog("TestCase ID:"+result.getMethod().getMethodName()+", Count:"+failCount);
            return true;
        }
        return false;
    }
}
