/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.utils;

import static java.util.UUID.randomUUID;

/*
@Purpose: This class generates random uuid
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 15/12/2021
*/
public class UUIDGenerator {

    @Override
    public String toString(){
        return randomUUID().toString();
    }


}
