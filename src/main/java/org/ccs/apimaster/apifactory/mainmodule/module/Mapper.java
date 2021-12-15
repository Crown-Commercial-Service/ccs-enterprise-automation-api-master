/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.mainmodule.module;

import org.ccs.apimaster.apifactory.mainmodule.support.ObjectMapperProvider;
import org.ccs.apimaster.apifactory.mainmodule.support.YamlObjectMapperProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.name.Names;


import javax.inject.Singleton;

/*
@Purpose: This class manages mapping between field objects
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 09/12/2021
*/
public class Mapper implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(ObjectMapper.class).toProvider(ObjectMapperProvider.class).in(Singleton.class);
        binder.bind(ObjectMapper.class)
                .annotatedWith(Names.named("YamlMapper"))
                .toProvider(YamlObjectMapperProvider.class).in(Singleton.class);
    }
}

