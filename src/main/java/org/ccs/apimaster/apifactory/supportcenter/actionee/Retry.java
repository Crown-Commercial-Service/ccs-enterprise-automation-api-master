/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.supportcenter.actionee;

/*
@Purpose: This class acts as actionee for retry mechanism
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 15/12/2021
*/
public class Retry {
    private Integer max;
    private Integer delay;

    public Integer getMax() {
        return max;
    }

    public Integer getDelay() {
        return delay;
    }

    public Retry() {}

    public Retry(Integer max, Integer delay) {
        this.max = max;
        this.delay = delay;
    }
}
