/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.mainmodule.configuration;

import org.ccs.apimaster.apifactory.mainmodule.module.*;
import org.ccs.apimaster.apifactory.coreactions.scriptexecutor.ApiServiceExecutor;
import org.ccs.apimaster.apifactory.coreactions.scriptexecutor.ApiServiceExecutorImpl;
import org.ccs.apimaster.apifactory.coreactions.scriptexecutor.httpapi.HttpApiExecutor;
import org.ccs.apimaster.apifactory.coreactions.scriptexecutor.httpapi.HttpApiExecutorImpl;
import org.ccs.apimaster.apifactory.coreactions.scriptexecutor.javaapi.JavaMethodExecutor;
import org.ccs.apimaster.apifactory.coreactions.scriptexecutor.javaapi.JavaMethodExecutorImpl;
import org.ccs.apimaster.apifactory.coreactions.preparation.*;
import org.ccs.apimaster.apifactory.coreactions.validators.MasterCodeValidator;
import org.ccs.apimaster.apifactory.coreactions.validators.MasterCodeValidatorImpl;
import org.ccs.apimaster.apifactory.reporter.MasterReportGenerator;
import org.ccs.apimaster.apifactory.reporter.MasterReportGeneratorImpl;
import org.ccs.apimaster.apifactory.runner.MasterMultiStepsScenarioRunner;
import org.ccs.apimaster.apifactory.runner.MasterMultiStepsScenarioRunnerImpl;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;


import java.util.Properties;
import java.util.logging.Logger;

import static org.ccs.apimaster.apifactory.utils.PropertiesProviderUtils.checkAndLoadOldProperties;
import static org.ccs.apimaster.apifactory.utils.PropertiesProviderUtils.loadAbsoluteProperties;
import static org.ccs.apimaster.apifactory.utils.SmartUtils.isValidAbsolutePath;

/*
@Purpose: This class configures the execution thread
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 09/12/2021
*/
public class RunConfiguration extends AbstractModule {
    private static final Logger LOGGER = Logger.getLogger(RunConfiguration.class.getName());

    private final String serverEnv;

    public RunConfiguration(String serverEnv) {
        this.serverEnv = serverEnv;
    }

    /*
   @Method: configure
   @Purpose: To configure the thread by binding required classes
   @Author: Mibin Boban, CCS Senior QAT Analyst
   @Creation: 09/12/2021
   */
    @Override
    public void configure() {
        install(new Mapper());
        install(new HttpClientModule());
        install(new GsonHandler());
        install(new PropertiesInjector(serverEnv));
        install(new CsvParser());

        bind(MasterMultiStepsScenarioRunner.class).to(MasterMultiStepsScenarioRunnerImpl.class);
        bind(ApiServiceExecutor.class).to(ApiServiceExecutorImpl.class);
        bind(HttpApiExecutor.class).to(HttpApiExecutorImpl.class);
        bind(JavaMethodExecutor.class).to(JavaMethodExecutorImpl.class);
        bind(MasterAssertionsProcessor.class).to(MasterAssertionsProcessorImpl.class);
        bind(MasterCodeValidator.class).to(MasterCodeValidatorImpl.class);
        bind(MasterReportGenerator.class).to(MasterReportGeneratorImpl.class);
        bind(MasterExternalFileProcessor.class).to(MasterExternalFileProcessorImpl.class);
        bind(MasterParameterizedProcessor.class).to(MasterParameterizedProcessorImpl.class);

        Names.bindProperties(binder(), getProperties(serverEnv));
    }

    /*
   @Method: getProperties
   @Purpose: To read the properties
   @Author: Mibin Boban, CCS Senior QAT Analyst
   @Creation: 09/12/2021
   */
    public Properties getProperties(String host) {
        final Properties properties = new Properties();

        if(isValidAbsolutePath(host)){
            return loadAbsoluteProperties(host, properties);
        }

        try {
            properties.load(getClass().getClassLoader().getResourceAsStream(host));
            checkAndLoadOldProperties(properties);

        } catch (Exception e) {
            LOGGER.info("[CCS Master] ###Oops!Exception### while reading target env file: " + host + ". Have you mentioned env details?");
            LOGGER.info(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("[CCS Master] could not read the target-env properties file --" + host + "-- from the classpath.");
        }

        return properties;
    }

}
