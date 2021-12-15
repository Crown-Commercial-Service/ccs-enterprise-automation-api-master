/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.runner.parallel;

import org.ccs.apimaster.apifactory.supportcenter.actionee.LoadSource;
import org.ccs.apimaster.apifactory.supportcenter.actionee.TestMapping;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static org.ccs.apimaster.apifactory.utils.RunnerUtils.validateTestMethod;
import static org.junit.runner.Description.createTestDescription;

/*
@Purpose: This class manages functions for handling load when running tests in parallel
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 15/12/2021
*/
public class MasterLoadRunner extends ParentRunner<TestMapping> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MasterLoadRunner.class);
    public static final String LOAD_LABEL = "<<Load>>";

    private final Class<?> testClass;
    private LoadProcessor loadProcessor;

    private String loadPropertiesFile;
    private Description testDescription;

    public MasterLoadRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
        this.testClass = testClass;
        this.loadPropertiesFile = validateAndGetLoadPropertiesFile();
        loadProcessor = createLoadProcessor();
    }

    public LoadProcessor createLoadProcessor() {
        return new LoadProcessor(loadPropertiesFile);
    }

    @Override
    protected List<TestMapping> getChildren() {
        validateAnnotationPresence();
        validateTestMethod(testClass);
        return Arrays.asList(testClass.getAnnotationsByType(TestMapping.class));
    }

    @Override
    protected Description describeChild(TestMapping child) {
        this.testDescription = createTestDescription(testClass, LOAD_LABEL + child.testMethod());
        return testDescription;
    }

    @Override
    protected void runChild(TestMapping child, RunNotifier notifier) {
        notifier.fireTestStarted(testDescription);

        boolean hasFailed = loadProcessor
                .addTest(child.testClass(), child.testMethod())
                .process();

        if(hasFailed){
            String failureMessage = testClass.getName() + "." + child.testMethod() + " Failed";
            LOGGER.error(failureMessage + ". See target/logs -or- junit granular failure report(csv) -or- fuzzy search and filter report(html) for details");
            notifier.fireTestFailure(new Failure(testDescription, new RuntimeException(failureMessage)));
        }
        notifier.fireTestFinished(testDescription);
    }

    @Override
    public void run(RunNotifier notifier) {
        super.run(notifier);
    }

    public String getLoadPropertiesFile() {
        return loadPropertiesFile;
    }

    private String validateAndGetLoadPropertiesFile() {
        LoadSource loadSourceAnno = testClass.getAnnotation(LoadSource.class);
        if(loadSourceAnno == null){
            throw new RuntimeException("Ah! You missed to put the @LoadWith(...) on the load-generating test class >> "
                    + testClass.getName());
        }

        return loadSourceAnno.value();
    }

    private void validateAnnotationPresence() {
        TestMapping methodMapping = testClass.getAnnotation(TestMapping.class);
        TestMapping[] testMappings = testClass.getAnnotationsByType(TestMapping.class);

        if (testMappings.length > 1){
            throw new RuntimeException("Oops! Needs single @TestMapping, but found multiple of it on the load-generating test class >>"
                    + testClass.getName() + ". \n For running multiple tests as load use @RunWith(MasterMultiLoadRunner.class)");

        } else if (methodMapping == null) {
            throw new RuntimeException("Ah! You missed to put the @TestMapping on the load-generating test class >> "
                    + testClass.getName());

        }

    }

}
