/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor:
 * Authors:
 * Contributors:
 */
package ccs.master.framework_chief.actionee;

import ccs.master.execution_manager.reporting.TestReporter;

import ccs.master.support_centre.actionRepository.WebHelper;
import net.lightbody.bmp.BrowserMobProxy;
import org.openqa.selenium.WebDriver;
import org.testng.asserts.SoftAssert;
import java.util.HashMap;

/*
@Class: MasterActions
@Purpose: This class is the repository of framework level actions
@Author: Mibin Boban, CCS Test Analyst
@Creation: 01/11/2021
*/
public class MasterActions {
    public WebDriver driver;
    private BrowserMobProxy proxy;
    private HashMap<String, Object> storageMap;
    private SoftAssert sa;

    /*
   @Constructors: MasterActions
   @Purpose: To handle initialization of actions based on type of testing
   @Author: Mibin Boban, CCS Test Analyst
   @Creation: 02/11/2021
   */
    public MasterActions(WebDriver driver, HashMap<String, Object> storageMap, SoftAssert sa) {
        this.driver = driver;
        this.storageMap = storageMap;
        this.sa=sa;
    }

    public MasterActions(WebDriver driver, HashMap<String, Object> storageMap, BrowserMobProxy proxy, SoftAssert sa) {
        this.driver = driver;
        this.storageMap = storageMap;
        this.proxy = proxy;
        this.sa=sa;
    }

    public MasterActions() {

    }

    public WebDriver getDriver() {
        return driver;
    }

    public BrowserMobProxy getProxy() {
        return proxy;
    }

    public SoftAssert getSoftAssertObject() {
        return sa;
    }

    public HashMap<String, Object> getHashKey() {
        return storageMap;
    }

    /*
   @Method: storeKeyValue
   @Purpose: To store the keys and values in hashmap
   @Author: Mibin Boban, CCS Test Analyst
   @Creation: 02/11/2021
   */
    public synchronized void storeKeyValue(String key, Object value) {
        storageMap.put(key, value);
    }

    /*
   @Method: retrieveKeyValue
   @Purpose: To retrieve the values based on key in hashmap
   @Author: Mibin Boban, CCS Test Analyst
   @Creation: 02/11/2021
   */
    public synchronized Object retrieveKeyValue(String key) {
        Object hashValue = storageMap.get(key);
        return hashValue;
    }

    /*
   @Method: deleteKeyValue
   @Purpose: To delete the keys in hashmap
   @Author: Mibin Boban, CCS Test Analyst
   @Creation: 02/11/2021
   */
    public synchronized MasterActions deleteKeyValue(String key) {
        TestReporter.reportLog("Delete the key - " + key + " from storage hash map.");
        storageMap.remove(key);
        return this;
    }

    /*
   @Method: switchToWindow
   @Purpose: To switch to a window in web app
   @Author: Mibin Boban, CCS Test Analyst
   @Creation: 04/11/2021
   */
    public synchronized MasterActions switchToWindow(String windowTitle) {
        WebHelper.SwitchToWindow(windowTitle,driver);
        return this;
    }


}
