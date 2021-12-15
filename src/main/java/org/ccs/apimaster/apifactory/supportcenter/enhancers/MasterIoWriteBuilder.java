/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.supportcenter.enhancers;

import org.ccs.apimaster.apifactory.mainmodule.support.ObjectMapperProvider;
import org.ccs.apimaster.apifactory.supportcenter.reportutils.MasterExecResult;
import org.ccs.apimaster.apifactory.supportcenter.reportutils.MasterReport;
import org.ccs.apimaster.apifactory.propertymanager.MasterReportProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.slf4j.LoggerFactory.getLogger;

/*
@Purpose: This class manages IO write
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 15/12/2021
*/
public class MasterIoWriteBuilder {
    private static final org.slf4j.Logger LOGGER = getLogger(MasterIoWriteBuilder.class);
    public static final int REPORT_WRITING_THREAD_POOL = 5;

    private LocalDateTime timeStamp;
    private List<MasterExecResult> results = Collections.synchronizedList(new ArrayList());
    private MasterReport built;

    private ExecutorService executorService = Executors.newFixedThreadPool(REPORT_WRITING_THREAD_POOL);

    public static MasterIoWriteBuilder newInstance() {
        return new MasterIoWriteBuilder();
    }

    public MasterReport build() {
        MasterReport built = new MasterReport(timeStamp, results);
        this.built = built;

        return built;
    }

    public MasterIoWriteBuilder timeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    public MasterIoWriteBuilder results(List<MasterExecResult> results) {
        this.results = results;
        return this;
    }

    public MasterIoWriteBuilder result(MasterExecResult result) {
        this.results.add(result);
        return this;
    }

    public synchronized void printToFile(String fileName) {
        try {
            this.build();

            final ObjectMapper mapper = new ObjectMapperProvider().get();

            File file = new File(MasterReportProperties.TARGET_REPORT_DIR + fileName);
            file.getParentFile().mkdirs();
            mapper.writeValue(file, this.built);
            delay(100L);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            LOGGER.warn("### Report Generation Problem: There was a problem during JSON parsing. Details: " + e);

        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.warn("### Report Generation Problem: There was a problem during writing the report. Details: " + e);
        }
    }

    private void delay(long miliSec) {
        try {
            Thread.sleep(miliSec);
        } catch (InterruptedException e) {
            throw new RuntimeException("Error providing delay");
        }
    }


    public void printToFileAsync(String fileName) {
        this.build();
        final ObjectMapper mapper = new ObjectMapperProvider().get();

        LOGGER.info("executorService(hashCode)>>" + executorService.hashCode());

        executorService.execute(() -> {
            LOGGER.info("Writing to file async - " + fileName);
            File file = new File(MasterReportProperties.TARGET_REPORT_DIR + fileName);
            file.getParentFile().mkdirs();
            try {
                mapper.writeValue(file, built);
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.warn("### Report Generation Problem: There was a problem during writing the report. Details: " + e);
            }
        });

        shutDownExecutorGraceFully();
    }

    private void shutDownExecutorGraceFully() {
        executorService.shutdown();
        while (!executorService.isTerminated()) {
        }
        LOGGER.info("Pass-Fail JSON report written target -done. Finished all threads");
    }
}
