/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor:
 * Authors:
 * Contributors:
 */
package ccs.master.execution_manager.monitoring;

import ccs.master.execution_manager.management.TestCaseRepository;
import ccs.master.execution_manager.reporting.TestReporter;
import ccs.master.framework_chief.initializer.FrameworkFactory;
import ccs.master.support_centre.propertymanager.CcsMasterPropertyHandler;

import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.WebDriver;
import org.testng.*;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;
import javax.imageio.ImageIO;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

/*
@Class: TestMonitor
@Purpose: This class monitors the test execution and takes action based on execution status
@Author: Mibin Boban, CCS Test Analyst
@Creation: 02/11/2021
*/
public class TestMonitor extends TestListenerAdapter implements IExecutionListener, IHookable {
    public TestMonitor(){

    }
    static
    {
        try {
            CcsMasterPropertyHandler.loadProjectLevelProperties();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int failCount=0;
    public static int passCount=0;
    public static int skipCount=0;
    String ExecutionStartTime="";
    String ExecutionEndTime="";
    public static String retry=CcsMasterPropertyHandler.TESTCASE_RETRY.getProperty().toString();
    public static String Exception="";

    /*
    @Method: onFinish
    @Purpose: To action when execution finish
    @Author: Mibin Boban, CCS Test Analyst
    @Creation: 02/11/2021
    */
    @Override
    public void onFinish(ITestContext arg0) {
        passCount=arg0.getPassedTests().size();
        failCount=arg0.getFailedTests().size();
        skipCount=arg0.getSkippedTests().size();
    }

    /*
    @Method: onStart
    @Purpose: To action when execution starts
    @Author: Mibin Boban, CCS Test Analyst
    @Creation: 02/11/2021
    */
    @Override
    public void onStart(ITestContext arg0) {

    }

    /*
   @Method: onTestFailedButWithinSuccessPercentage
   @Purpose: To action when execution failed but within success percentatge
   @Author: Mibin Boban, CCS Test Analyst
   @Creation: 02/11/2021
   */
    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult arg0) {
        // TODO Auto-generated method stub

    }

    /*
    @Method: onTestFailure
    @Purpose: To action when execution fails
    @Author: Mibin Boban, CCS Test Analyst
    @Creation: 02/11/2021
    */
    @Override
    public void onTestFailure(ITestResult arg0) {
        List<String> reporterOutput = Reporter.getOutput( arg0 );
        Throwable cause =arg0.getThrowable();
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        if (null != cause) {
            cause.printStackTrace(pw);
            Exception=sw.getBuffer().toString();
        }
        StringBuilder builder =new StringBuilder();
        for(String step : reporterOutput) {
            builder.append(step);
            builder.append("<br>");
        }
    }

    /*
    @Method: onTestSkipped
    @Purpose: To action when test skips execution
    @Author: Mibin Boban, CCS Test Analyst
    @Creation: 02/11/2021
    */
    @Override
    public void onTestSkipped(ITestResult arg0) {
    }

    /*
    @Method: onTestStart
    @Purpose: To action when test starts execution
    @Author: Mibin Boban, CCS Test Analyst
    @Creation: 02/11/2021
    */
    @Override
    public void onTestStart(ITestResult arg0) {
        if(CcsMasterPropertyHandler.TESTMANAGEMENT_RESULT_UPDATE.toBoolean()==true) {
            try{
                String []allTestcaseId = TestCaseRepository.testcaseHashMap.get(arg0.getName()).split(",");
                for (int i = 0; i < allTestcaseId.length; i++) {
                    TestCaseRepository.testcaseFailedHashMap.put(arg0.getName(), allTestcaseId[i]);
                }
            }
            catch (NullPointerException e) {
                e.printStackTrace();
            } catch (java.lang.Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*
    @Method: onTestSuccess
    @Purpose: To action when test passed
    @Author: Mibin Boban, CCS Test Analyst
    @Creation: 02/11/2021
    */
    @Override
    public void onTestSuccess(ITestResult arg0) {
        if(CcsMasterPropertyHandler.TESTMANAGEMENT_RESULT_UPDATE.toBoolean()==true) {
            try{
                MultiValuedMap<String, String> testcaseFailedHashMapTemp=TestCaseRepository.testcaseFailedHashMap;
                for(String testCaseIdMappedToTheScript : testcaseFailedHashMapTemp.get(arg0.getName())) {
                    TestCaseRepository.testcasePassedHashMap.put(arg0.getName(), testCaseIdMappedToTheScript);
                }
                TestCaseRepository.testcaseFailedHashMap.remove(arg0.getName());
            }catch (java.lang.Exception e) {
                e.printStackTrace();
            }
        }
        List<String> reporterOutput = Reporter.getOutput( arg0 );
        StringBuilder builder =new StringBuilder();
        for(String step : reporterOutput) {
            builder.append(step);
            builder.append("<br>");
        }
    }

    /*
    @Method: onExecutionFinish
    @Purpose: To action when execution is completed
    @Author: Mibin Boban, CCS Test Analyst
    @Creation: 02/11/2021
    */
    @Override
    public void onExecutionFinish() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MMM/dd-HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        ExecutionEndTime = dateFormat.format(cal.getTime());
        Iterator<String> it = TestCaseRepository.testcaseFailedHashMap.keySet().iterator();
        it = TestCaseRepository.testcasePassedHashMap.keySet().iterator();
    }

    /*
    @Method: onExecutionStart
    @Purpose: To action when execution is started
    @Author: Mibin Boban, CCS Test Analyst
    @Creation: 02/11/2021
    */
    @Override
    public void onExecutionStart() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MMM/dd-HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        ExecutionStartTime = dateFormat.format(cal.getTime());

    }

    /*
    @Method: screenShotOnFailure
    @Purpose: To take screenshot when test is failed
    @Author: Mibin Boban, CCS Test Analyst
    @Creation: 02/11/2021
    */
    @Attachment(value = "Failure screenshot of {0}", type = "image/png")
    public static byte[] screenShotOnFailure(String testcasename, WebDriver driver) 	{
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd-HH-mm-ss");
        Calendar cal = Calendar.getInstance();
        String testFailureTime = dateFormat.format(cal.getTime());
        File dir = new File("./build/test-output/screenshot/");
        if (!dir.exists()) dir.mkdirs();
        String screenShotLocation="./build/test-output/screenshot/"+testcasename+"_"+testFailureTime+".PNG";
        File test = new File(screenShotLocation);
        File source = null;
        Path path = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
        try {

            if(CcsMasterPropertyHandler.TEST_BROWSER.toString().equalsIgnoreCase("windows")){
                //WebDriverFactory.getWinDriver().takeScreenShot(screenShotLocation);
            } else {
                Screenshot screenshot=new AShot().shootingStrategy(ShootingStrategies.viewportPasting(100)).takeScreenshot(driver);
                ImageIO.write(screenshot.getImage(),"PNG",test);
            }

            System.out.println("currentDir Path "+path);
            if(!path.toString().contains("Jenkins")){
                TestReporter.reportLog("<a href=" + "./screenshot/"+testcasename+"_"+testFailureTime+".PNG" +" target=_blank>SCREENSHOT</a>");
                Allure.attachment(testcasename, FileUtils.openInputStream(test));
            }
            else{
                TestReporter.reportLog("<a href="+"./screenshot/"+testcasename+"_"+testFailureTime+".PNG> "
                        + "<img src=" + "./screenshot/"+testcasename+"_"+testFailureTime+".PNG height=100 width=100> </a>");
                Allure.attachment(testcasename, FileUtils.openInputStream(test));

            }
        }
        catch(Exception e) {e.printStackTrace();}
        try {
            return fileToBytes(screenShotLocation);
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }
        return "Unable to Get Screenshot.".getBytes();

    }

    /*
    @Method: fileToBytes
    @Purpose: To convert file to bytes format
    @Author: Mibin Boban, CCS Test Analyst
    @Creation: 02/11/2021
    */
    private static byte[] fileToBytes(String fileName) throws Exception
    {
        File file = new File(fileName);
        return Files.readAllBytes(Paths.get(file.getAbsolutePath()));
    }

    /*
    @Method: run
    @Purpose: To trigger the execution of test
    @Author: Mibin Boban, CCS Test Analyst
    @Creation: 02/11/2021
    */
    @Override
    public void run(IHookCallBack callBack, ITestResult testResult) {

        callBack.runTestMethod(testResult);
        if (testResult.getThrowable() != null) {
            screenShotOnFailure(testResult.getMethod().getMethodName(), FrameworkFactory.getDriver());
        }
    }

    /*
    @Method: getPageSourceOnFailure
    @Purpose: To get the page source when execution is failed
    @Author: Mibin Boban, CCS Test Analyst
    @Creation: 02/11/2021
    */
    public void getPageSourceOnFailure( ITestResult testResult, WebDriver driver) 	{
        if(testResult.getMethod().getMethodName().toLowerCase().contains("sitemap"))
            return ;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd-HH-mm-ss");
        Calendar cal = Calendar.getInstance();
        String testFailureTime = dateFormat.format(cal.getTime());
        String pageSource =FrameworkFactory.getDriver().getPageSource();
        String fileLocation="./build/test-output/screenshot/"+testResult.getTestName()+"_"+testFailureTime+".txt";
        try {
            File temp = File.createTempFile("temp-file-name", ".tmp");
            FileWriter fw=new FileWriter(temp);
            fw.write(pageSource);
            fw.close();
            File newTextFile = new File(fileLocation);
            FileUtils.copyFile(temp, newTextFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Path path = Paths.get(System.getProperty("user.dir"));
        if(!path.toString().contains("Jenkins")){
            TestReporter.reportLog("<a href=" + "./screenshot/"+testResult.getTestName()+"_"+testFailureTime+".txt" +" target=_blank>PAGE SOURCE</a>");
            Allure.attachment(testResult.getTestName(), pageSource);

        }
        else{
            TestReporter.reportLog("<a href="+"./screenshot/"+testResult.getTestName()+"_"+testFailureTime+".txt> "+" PAGE SOURCE "
                    + "</a>");
            Allure.attachment(testResult.getTestName(), pageSource);
        }
    }
}
