/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor:
 * Authors:
 * Contributors:
 */
package ccs.master.framework_chief.initializer;

import ccs.master.framework_chief.actionee.MasterActions;
import org.openqa.selenium.WebDriver;

/*
@Class: PageCentre
@Purpose: This class handles the linking between framework factory and Page Object Model
@Author: Mibin Boban, CCS Test Analyst
@Creation: 01/11/2021
*/
public class PageCentre {
    public WebDriver getCurrentDriver() {
        return FrameworkFactory.getDriver();
    }
    public MasterActions getAction() {
        return FrameworkFactory.getActionee();
    }
}
