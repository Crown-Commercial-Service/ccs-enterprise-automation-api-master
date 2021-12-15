/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.mainmodule.module;

import org.ccs.apimaster.apifactory.mainmodule.support.CsvParserProvider;
import com.google.inject.Binder;
import com.google.inject.Module;


import javax.inject.Singleton;

/*
@Purpose: This class manages configuration for execution with CSV
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 09/12/2021
*/
public class CsvParser implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(com.univocity.parsers.csv.CsvParser.class).toProvider(CsvParserProvider.class).in(Singleton.class);
    }
}

