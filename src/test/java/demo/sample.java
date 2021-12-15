/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package demo;

import org.ccs.apimaster.apifactory.supportcenter.actionee.CCS_Master_Scenario;
import org.ccs.apimaster.apifactory.supportcenter.actionee.CCS_Master_Properties;
import org.ccs.apimaster.apifactory.runner.CCSUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@CCS_Master_Properties("demo.properties")
@RunWith(CCSUnitRunner.class)
public class sample{
    @Test
    @CCS_Master_Scenario("demo/sample1.json")
    public void testSampleEndpoint() throws Exception{

    }
}
