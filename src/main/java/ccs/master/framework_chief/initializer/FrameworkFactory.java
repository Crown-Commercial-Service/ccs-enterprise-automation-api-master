/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor:
 * Authors:
 * Contributors:
 */
package ccs.master.framework_chief.initializer;

import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import ccs.master.framework_chief.actionee.MasterActions;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.TestNGException;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.proxy.CaptureType;

import ccs.master.support_centre.propertymanager.CcsMasterPropertyHandler;

/*
@Class: FrameworkFactory
@Purpose: This class is the initialization point and handles initial set up for test execution
@Author: Mibin Boban, CCS Test Analyst
@Creation: 29/10/2021
*/
public class FrameworkFactory {
    public static String testName;
    private static ThreadLocal<WebDriver> driver=new ThreadLocal<WebDriver>();
    private static ThreadLocal<BrowserMobProxy> proxy=new ThreadLocal<BrowserMobProxy>();
    public static WebDriver getDriver() {
        return driver.get();
    }
    public static BrowserMobProxy getProxy() {
        return proxy.get();
    }
    public static SoftAssert getSoftAssert() {
        return sa.get();
    }
    public static HashMap<String, Object> getHashMap() {
        return hashMap.get();
    }

    private static ThreadLocal<HashMap<String, Object>> hashMap = new ThreadLocal<HashMap<String, Object>>() {
        @Override
        protected HashMap<String, Object> initialValue() {
            return new HashMap<>();
        }
    };

    private static ThreadLocal<SoftAssert> sa = new ThreadLocal<SoftAssert>()
    {
        @Override protected SoftAssert initialValue() {
            return new SoftAssert();
        }
    };

    /*
    @Method: getActioniee
    @Purpose: To link with actionee based on test type
    @Author: Mibin Boban, CCS Test Analyst
    @Creation: 29/10/2021
    */
    public static MasterActions getActionee() {
        String testType = CcsMasterPropertyHandler.TEST_TYPE.toString().toLowerCase();
        MasterActions action = null;
        switch (testType){
            case "web":
                action= new MasterActions(driver.get(),hashMap.get(),sa.get());
                break;
            case "api":
                break;
            case "mobile":
                action= new MasterActions(driver.get(),hashMap.get(),sa.get());
                break;
            case "database":
                break;
        }
        return action;
    }

    public  FrameworkFactory() {
        // TODO Auto-generated constructor stub
    }

    /*
    @Method: setProxySetting
    @Purpose: To handle proxy settings
    @Author: Mibin Boban, CCS Test Analyst
    @Creation: 29/10/2021
    */
    public Proxy setProxySetting() {
        proxy.set(new BrowserMobProxyServer());
        proxy.get().setTrustAllServers(true);
        proxy.get().start();

        Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy.get());
        try {
            String hostIp = Inet4Address.getLocalHost().getHostAddress();
            seleniumProxy.setHttpProxy(hostIp+ ":" + proxy.get().getPort());
            seleniumProxy.setSslProxy(hostIp+":" + proxy.get().getPort());
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return seleniumProxy;
    }
    /*
    @Method: setProxySetting
    @Purpose: To configure proxy request filters
    @Author: Mibin Boban, CCS Test Analyst
    @Creation: 01/11/2021
    */
    public void addProxyRequestFilter() {
        proxy.get().enableHarCaptureTypes(CaptureType.REQUEST_BINARY_CONTENT,CaptureType.REQUEST_CONTENT,CaptureType.REQUEST_COOKIES,CaptureType.REQUEST_HEADERS,
                CaptureType.RESPONSE_BINARY_CONTENT,CaptureType.RESPONSE_CONTENT,CaptureType.RESPONSE_COOKIES,CaptureType.RESPONSE_HEADERS);
    }

    /*
    @Method: initialize
    @Purpose: To initialize the execution flow depending on test type
    @Author: Mibin Boban, CCS Test Analyst
    @Creation: 01/11/2021
    */
    @BeforeMethod(alwaysRun=true)
    public void initialize(Method method) throws Exception {
        String testType = CcsMasterPropertyHandler.TEST_TYPE.toString().toLowerCase();
        switch (testType){
            case "web":
                assignDriver(method);
                break;
            case "api":
                break;
            case "mobile":
                enableMobileEmulation(method);
                break;
            case "database":
                break;

        }
    }

    /*
    @Method: closeExecution
    @Purpose: To terminate the test execution flow based on test type
    @Author: Mibin Boban, CCS Test Analyst
    @Creation: 01/11/2021
    */
    @AfterMethod(alwaysRun=true)
    public void closeExecution() throws Exception {
        String testType = CcsMasterPropertyHandler.TEST_TYPE.toString().toLowerCase();
        switch (testType) {
            case "web":
                driver.get().quit();
                FrameworkFactory.sa.remove();
                break;
            case "api":
                break;
            case "mobile":
                driver.get().quit();
                FrameworkFactory.sa.remove();
                break;
            case "database":
                break;
        }
    }

    /*
    @Method: assignDriver
    @Purpose: To enable the cross browser testing based on browser type
    @Author: Mibin Boban, CCS Test Analyst
    @Creation: 01/11/2021
    */
    public void assignDriver(Method method) throws Exception{
        Proxy browserMobProxy=setProxySetting();
        addProxyRequestFilter();
        getHashMap().put("testname", method.getName());
        if(CcsMasterPropertyHandler.TEST_BROWSER.toString().toLowerCase().contains("chrome")) {
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.setAcceptInsecureCerts(true);
            chromeOptions.setExperimentalOption("useAutomationExtension", false);
            chromeOptions.addArguments("--no-sandbox");
            chromeOptions.addArguments("--disable-dev-shm-usage");
            driver.set(new RemoteWebDriver(new URL("http://"+CcsMasterPropertyHandler.SELENIUM_HOST+":"+CcsMasterPropertyHandler.SELENIUM_PORT+"/wd/hub"),chromeOptions));
            driver.get().manage().window().maximize();
        }
        else if(CcsMasterPropertyHandler.TEST_BROWSER.toString().toLowerCase().contains("ff")||
                CcsMasterPropertyHandler.TEST_BROWSER.toString().toLowerCase().contains("firefox")) {
            FirefoxOptions ffOptions = new FirefoxOptions();
            driver.set(new RemoteWebDriver(new URL("http://"+CcsMasterPropertyHandler.SELENIUM_HOST+":"+CcsMasterPropertyHandler.SELENIUM_PORT+"/wd/hub"),ffOptions));
            driver.get().manage().window().maximize();
        }
        else if(CcsMasterPropertyHandler.TEST_BROWSER.toString().toLowerCase().contains("edge")){
            EdgeOptions edgeOptions = new EdgeOptions();
            edgeOptions.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
            driver.set(new RemoteWebDriver(new URL("http://"+CcsMasterPropertyHandler.SELENIUM_HOST+":"+CcsMasterPropertyHandler.SELENIUM_PORT+"/wd/hub"),edgeOptions));
            driver.get().manage().window().maximize();
        }
        else if(CcsMasterPropertyHandler.TEST_BROWSER.toString().toLowerCase().contains("ie")|| CcsMasterPropertyHandler.TEST_BROWSER.toString().toLowerCase().contains("explor")
                || CcsMasterPropertyHandler.TEST_BROWSER.toString().toLowerCase().contains("internet")) {
            InternetExplorerOptions ieOptions = new InternetExplorerOptions();
            ieOptions.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
            driver.set(new RemoteWebDriver(new URL("http://"+CcsMasterPropertyHandler.SELENIUM_HOST+":"+CcsMasterPropertyHandler.SELENIUM_PORT+"/wd/hub"),ieOptions));
            driver.get().manage().window().maximize();
        }
        else if(CcsMasterPropertyHandler.TEST_BROWSER.toString().toLowerCase().contains("headless-chrome")) {
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.setHeadless(true);
            chromeOptions.setAcceptInsecureCerts(true);
            chromeOptions.setExperimentalOption("useAutomationExtension", false);
            chromeOptions.addArguments("--no-sandbox");
            chromeOptions.addArguments("--disable-dev-shm-usage");
            chromeOptions.addArguments("--window-size=1920,1080");
            chromeOptions.addArguments("--disable-gpu");
            chromeOptions.addArguments("--disable-extensions");
            chromeOptions.addArguments("--start-maximized");
            driver.set(new RemoteWebDriver(new URL("http://"+CcsMasterPropertyHandler.SELENIUM_HOST+":"+CcsMasterPropertyHandler.SELENIUM_PORT+"/wd/hub"),chromeOptions));

        }
        else {
            throw new TestNGException("The browser specified in the properties file is not valid.");
        }
    }

    /*
    @Method: enableMobileEmulation
    @Purpose: To enable mobile emulation for mobile view testing
    @Author: Mibin Boban, CCS Test Analyst
    @Creation: 01/11/2021
    */
    public void enableMobileEmulation(Method method) throws Exception{
        Proxy browserMobProxy=setProxySetting();
        addProxyRequestFilter();
        getHashMap().put("testname", method.getName());
        Map<String, String> mobileEmulation = new HashMap<>();
        mobileEmulation.put("deviceName",  CcsMasterPropertyHandler.MOBILE_DEVICE.toString());
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setExperimentalOption("mobileEmulation", mobileEmulation);
        driver.set(new RemoteWebDriver(new URL("http://"+CcsMasterPropertyHandler.SELENIUM_HOST+":"+CcsMasterPropertyHandler.SELENIUM_PORT+"/wd/hub"),chromeOptions));
        CcsMasterPropertyHandler.MOBILE_EXECUTION.setProperty(true);
    }

    /*
    @Method: addHeaderToDriver
    @Purpose: To add headers to driver
    @Author: Mibin Boban, CCS Test Analyst
    @Creation: 01/11/2021
    */
    public String addHeaderToDriver() {
        String proxyOption = "--proxy-server=" ;
        BrowserMobProxy proxy = new BrowserMobProxyServer();
        proxy.start(0);
        Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);
        proxy.addRequestFilter((request, contents, messageInfo)->{
            request.headers().add(CcsMasterPropertyHandler.HEADER_NAME.toString(), CcsMasterPropertyHandler.HEADER_VALUE.toString());
            return null;
        });
        proxyOption = "--proxy-server=" + seleniumProxy.getHttpProxy();
        return proxyOption;
    }
}
