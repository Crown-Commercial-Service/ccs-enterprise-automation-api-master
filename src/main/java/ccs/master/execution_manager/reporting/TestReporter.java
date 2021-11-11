/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor:
 * Authors:
 * Contributors:
 */
package ccs.master.execution_manager.reporting;

import io.qameta.allure.Step;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.TestNGException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import static io.qameta.allure.Allure.step;

/*
@Class: TestReporter
@Purpose: This class handles reporting of test execution statuses
@Author: Mibin Boban, CCS Test Analyst
@Creation: 02/11/2021
*/
public class TestReporter {

    /*
    @Method: reportWarningLog
    @Purpose: To report warnings
    @Author: Mibin Boban, CCS Test Analyst
    @Creation: 02/11/2021
    */
    @Step("{0}")
    public static void reportWarningLog(String message){
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
        Calendar cal = Calendar.getInstance();
        String Timestamp = dateFormat.format(cal.getTime());
        message =Timestamp+ " : <b style='color:red;background-color:yellow'>"+message+"</b>";
        step(message);
        Reporter.log(message);
    }

    /*
    @Method: reportScenarioLog
    @Purpose: To report scenario based messages
    @Author: Mibin Boban, CCS Test Analyst
    @Creation: 02/11/2021
    */
    @Step("{0}")
    public static void reportScenarioLog(String message){
        message ="<b style='color:black;background-color:aqua'>"+message+"</b>";
        step(message);
        Reporter.log(message);
    }

    /*
    @Method: reportLog
    @Purpose: To report step level messages
    @Author: Mibin Boban, CCS Test Analyst
    @Creation: 02/11/2021
    */
    @Step("{0}")
    public static void reportLog(String message){
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
        Calendar cal = Calendar.getInstance();
        String Timestamp = dateFormat.format(cal.getTime());

        if (message.contains("<<") & message.contains(">>")) {
            message = message.replace("<<", "<b style='color:black;background-color:orange'>");
            message = message.replace(">>", "</b>");
        }
        message =Timestamp+" : "+message;
        step(message);
        Reporter.log(message);
    }

    /*
    @Method: reportVerificationLog
    @Purpose: To report verification messages
    @Author: Mibin Boban, CCS Test Analyst
    @Creation: 02/11/2021
    */
    @Step("{0}")
    public static void reportVerificationLog(String message){
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
        Calendar cal = Calendar.getInstance();
        String Timestamp = dateFormat.format(cal.getTime());
        if (message.contains("<<") & message.contains(">>")) {
            message = message.replace("<<", "<b style='color:black;background-color:orange'>");
            message = message.replace(">>", "</b>");
        }
        message =Timestamp+ " : <b style='color:green;background-color:white'>"+message+"</b>";
        step(message);
        Reporter.log(message);
    }

    /*
    @Method: reportSubLog
    @Purpose: To report sub step messages
    @Author: Mibin Boban, CCS Test Analyst
    @Creation: 02/11/2021
    */
    @Step("{0}")
    public static void reportSubLog(String message) {
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
        Calendar cal = Calendar.getInstance();
        String Timestamp = dateFormat.format(cal.getTime());

        if (message.contains("<<") & message.contains(">>")) {
            message = message.replace("<<", "<b style='color:black;background-color:orange'>");
            message = message.replace(">>", "</b>");
        }

        message = Timestamp + " : <label style=font-size:14px; margin-left:20px;>" + message + "</label>";
        step(message);
        Reporter.log(message);
    }

    /*
    @Method: reportVerificationStepLog
    @Purpose: To report verification step messages
    @Author: Mibin Boban, CCS Test Analyst
    @Creation: 02/11/2021
    */
    @Step("{0}")
    public static void reportVerificationStepLog(String message) {
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
        Calendar cal = Calendar.getInstance();
        String Timestamp = dateFormat.format(cal.getTime());

        if (message.contains("<<") & message.contains(">>")) {
            message = message.replace("<<", "<b style='color:black;background-color:orange'>");
            message = message.replace(">>", "</b>");
        }

        message = Timestamp + " : <label style='background-color:green;color:black'>" + message + "</label>";
        step(message);
        Reporter.log(message);
    }

    /*
    @Method: reportSubVerificationLog
    @Purpose: To report sub verification messages
    @Author: Mibin Boban, CCS Test Analyst
    @Creation: 02/11/2021
    */
    @Step("{0}")
    public static void reportSubVerificationLog(String message) {
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
        Calendar cal = Calendar.getInstance();
        String Timestamp = dateFormat.format(cal.getTime());

        if (message.contains("<<") & message.contains(">>")) {
            message = message.replace("<<", "<b style='color:black;background-color:orange'>");
            message = message.replace(">>", "</b>");
        }

        message = Timestamp + " : <label style='color:green;font-size:14px;'>" + message + "</label>";
        step(message);
        Reporter.log(message);
    }

    /*
    @Method: reportError
    @Purpose: To report errors
    @Author: Mibin Boban, CCS Test Analyst
    @Creation: 02/11/2021
    */
    @Step("{0}")
    public static void reportError(String message) {
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
        Calendar cal = Calendar.getInstance();
        String Timestamp = dateFormat.format(cal.getTime());
        message = Timestamp + " : <b style='color:red;background-color:white'>" + message + "</b>";
        Reporter.log(message);
        step(message);
        Assert.fail(message);
        throw new TestNGException(message);
    }
}
