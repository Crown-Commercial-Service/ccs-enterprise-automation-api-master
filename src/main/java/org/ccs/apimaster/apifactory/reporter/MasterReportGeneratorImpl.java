/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.reporter;

import org.ccs.apimaster.apifactory.supportcenter.enhancers.*;
import org.ccs.apimaster.apifactory.supportcenter.reportutils.MasterExecResult;
import org.ccs.apimaster.apifactory.supportcenter.reportutils.MasterReport;
import org.ccs.apimaster.apifactory.supportcenter.reportutils.MasterReportStep;
import org.ccs.apimaster.apifactory.supportcenter.reportutils.chart.HighChartColumnHtml;
import org.ccs.apimaster.apifactory.supportcenter.reportutils.csv.MasterCsvReport;
import org.ccs.apimaster.apifactory.propertymanager.MasterReportProperties;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.CodeLanguage;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static org.ccs.apimaster.apifactory.supportcenter.enhancers.ExtentReportsFactory.getReportName;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang.StringUtils.substringBetween;

/*
@Purpose: This class manages functions for reporting with High Chart Coulmn html
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 09/12/2021
*/
public class MasterReportGeneratorImpl implements MasterReportGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MasterReportGeneratorImpl.class);

    private static String spikeChartFileName;

    @Inject(optional = true)
    @Named("report.spike.chart.enabled")
    private boolean spikeChartReportEnabled;

    @Inject(optional = true)
    @Named("interactive.html.report.disabled")
    private boolean interactiveHtmlReportDisabled;

    private final ObjectMapper mapper;

    private List<MasterReport> treeReports;

    private List<MasterCsvReport> masterCsvFlattenedRows;

    private List<MasterCsvReport> csvRows = new ArrayList<>();

    @Inject
    public MasterReportGeneratorImpl(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /*
   @Method: getUniqueSteps
   @Purpose: To get unique steps from scenarios
   @Author: Mibin Boban, CCS Senior QAT Analyst
   @Creation: 09/12/2021
   */
    List<MasterReportStep> getUniqueSteps(List<MasterReportStep> steps) {
        Map<String, MasterReportStep> result = new LinkedHashMap<>();
        steps.forEach(step -> {
            result.merge(step.getCorrelationId(), step,
                    (s1, s2) -> MasterReportProperties.RESULT_PASS.equals(s1.getResult()) ? s1 : s2);
        });
        return new ArrayList<>(result.values());
    }

    /*
    @Method: generateExtentReport
    @Purpose: To generate extent report
    @Author: Mibin Boban, CCS Senior QAT Analyst
    @Creation: 09/12/2021
    */
    @Override
    public void generateExtentReport() {

        if (interactiveHtmlReportDisabled) {
            return;
        }

        ExtentReports extentReports = ExtentReportsFactory.createReportTheme(MasterReportProperties.TARGET_FILE_NAME);

        linkToSpikeChartIfEnabled();

        treeReports.forEach(thisReport -> {

            thisReport.getResults().forEach(thisScenario -> {
                ExtentTest test = extentReports.createTest(thisScenario.getScenarioName());
                test.assignCategory(MasterReportProperties.DEFAULT_REGRESSION_CATEGORY); //Super set
                test.assignCategory(optionalCategory(thisScenario.getScenarioName())); //Sub sets

                test.assignAuthor(optionalAuthor(thisScenario.getScenarioName()));
                List<MasterReportStep> thisScenarioUniqueSteps = getUniqueSteps(thisScenario.getSteps());
                thisScenarioUniqueSteps.forEach(thisStep -> {
                    test.getModel().setStartTime(utilDateOf(thisStep.getRequestTimeStamp()));
                    test.getModel().setEndTime(utilDateOf(thisStep.getResponseTimeStamp()));

                    final Status testStatus = thisStep.getResult().equals(MasterReportProperties.RESULT_PASS) ? Status.PASS : Status.FAIL;

                    ExtentTest step = test.createNode(thisStep.getName(), MasterReportProperties.TEST_STEP_CORRELATION_ID + " " + thisStep.getCorrelationId());

                    if (testStatus.equals(Status.PASS)) {
                        step.pass(thisStep.getResult());
                    } else {
                        step.info(MarkupHelper.createCodeBlock(thisStep.getOperation() + "\t" + thisStep.getUrl()));
                        //step.info(MarkupHelper.createCodeBlock(thisStep.getRequest(), CodeLanguage.JSON));
                        step.info(MarkupHelper.createCodeBlock(thisStep.getResponse(), CodeLanguage.JSON));
                        step.fail(MarkupHelper.createCodeBlock("Reason:\n" + thisStep.getAssertions()));
                    }
                    extentReports.flush();
                });

            });

        });
    }

    /*
    @Method: linkToSpikeChartIfEnabled
    @Purpose: To handle spike chart
    @Author: Mibin Boban, CCS Senior QAT Analyst
    @Creation: 09/12/2021
    */
    public void linkToSpikeChartIfEnabled() {

        if (spikeChartReportEnabled || spikeChartFileName != null) {
            final String reportName = getReportName();

            String linkCodeToTargetSpikeChartHtml =
                    String.format("<code>&nbsp;&nbsp;<a href='%s' style=\"color: #006; background: #ff6;\"> %s </a></code>",
                            spikeChartFileName,
                            MasterReportProperties.LINK_LABEL_NAME);

            ExtentReportsFactory.reportName(reportName + linkCodeToTargetSpikeChartHtml);
        }
    }

    /*
    @Method: optionalAuthor
    @Purpose: To manage author name in test case
    @Author: Mibin Boban, CCS Senior QAT Analyst
    @Creation: 09/12/2021
    */
    protected String optionalAuthor(String scenarioName) {
        String authorName = deriveName(scenarioName, MasterReportProperties.AUTHOR_MARKER_OLD);
        authorName = MasterReportProperties.ANONYMOUS_CAT.equals(authorName) ? deriveName(scenarioName, MasterReportProperties.AUTHOR_MARKER_NEW) : authorName;
        return authorName;
    }

    /*
    @Method: optionalCategory
    @Purpose: To manage test case category
    @Author: Mibin Boban, CCS Senior QAT Analyst
    @Creation: 09/12/2021
    */
    protected String optionalCategory(String scenarioName) {
        return deriveName(scenarioName, MasterReportProperties.CATEGORY_MARKER);
    }

    private String deriveName(String scenarioName, String marker) {
        String authorName = substringBetween(scenarioName, marker, marker);

        if (authorName == null) {
            authorName = substringBetween(scenarioName, marker, ",");
        }

        if (authorName == null) {
            authorName = substringBetween(scenarioName, marker, " ");
        }

        if (authorName == null) {
            authorName = scenarioName.substring(scenarioName.lastIndexOf(marker) + marker.length());
        }

        if (scenarioName.lastIndexOf(marker) == -1 || StringUtils.isEmpty(authorName)) {
            authorName = MasterReportProperties.ANONYMOUS_CAT;
        }

        return authorName;
    }

    protected String onlyScenarioName(String scenarioName) {

        int index = scenarioName.indexOf(MasterReportProperties.AUTHOR_MARKER_OLD);
        if (index == -1) {
            return scenarioName;
        } else {
            return scenarioName.substring(0, index - 1);
        }
    }

    /*
    @Method: generateCsvReport
    @Purpose: To generate CSV report
    @Author: Mibin Boban, CCS Senior QAT Analyst
    @Creation: 09/12/2021
    */
    @Override
    public void generateCsvReport() {

        treeReports = readMasterReportsByPath(MasterReportProperties.TARGET_REPORT_DIR);
        masterCsvFlattenedRows = buildCsvRows();
        generateCsvReport(masterCsvFlattenedRows);
    }

    /*
    @Method: generateHighChartReport
    @Purpose: To generate high chart report
    @Author: Mibin Boban, CCS Senior QAT Analyst
    @Creation: 09/12/2021
    */
    @Override
    public void generateHighChartReport() {
        LOGGER.info("####spikeChartReportEnabled: " + spikeChartReportEnabled);
        if (spikeChartReportEnabled) {
            HighChartColumnHtml highChartColumnHtml = convertCsvRowsToHighChartData(masterCsvFlattenedRows);
            generateHighChartReport(highChartColumnHtml);
        }
    }

    /*
   @Method: convertCsvRowsToHighChartData
   @Purpose: To convert data in csv to high chart data
   @Author: Mibin Boban, CCS Senior QAT Analyst
   @Creation: 09/12/2021
   */
    private HighChartColumnHtml convertCsvRowsToHighChartData(List<MasterCsvReport> masterCsvReportRows) {

        HighChartColumnHtmlBuilder highChartColumnHtmlBuilder = HighChartColumnHtmlBuilder.newInstance()
                .chartSeriesName("Test Results")
                .chartTitleTop("Request Vs Response Delay Chart")
                .textYaxis("Response Delay in Milli Sec")
                .chartTitleTopInABox("Spike Chart ( Milli Seconds )");

        MasterChartKeyValueArrayBuilder dataArrayBuilder = MasterChartKeyValueArrayBuilder.newInstance();

        masterCsvReportRows.forEach(thisRow ->
                dataArrayBuilder.kv(MasterChartKeyValueBuilder.newInstance()
                        .key(thisRow.getScenarioName() + "->" + thisRow.getStepName())
                        .value(thisRow.getResponseDelayMilliSec())
                        .result(thisRow.getResult())
                        .build())
        );

        highChartColumnHtmlBuilder.testResult(dataArrayBuilder.build());

        return highChartColumnHtmlBuilder.build();

    }

    /*
   @Method: generateHighChartReport
   @Purpose: To convert data in csv to high chart data
   @Author: Mibin Boban, CCS Senior QAT Analyst
   @Creation: 09/12/2021
   */
    public void generateHighChartReport(HighChartColumnHtml highChartColumnHtml) {

        HighChartColumnHtmlWriter highChartColumnHtmlWriter = new HighChartColumnHtmlWriter();

        spikeChartFileName = createTimeStampedFileName();

        highChartColumnHtmlWriter.generateHighChart(highChartColumnHtml, spikeChartFileName);
    }

    /*
   @Method: generateCsvReport
   @Purpose: To generate report in csv format
   @Author: Mibin Boban, CCS Senior QAT Analyst
   @Creation: 09/12/2021
   */
    public void generateCsvReport(List<MasterCsvReport> masterCsvReportRows) {

        CsvSchema schema = CsvSchema.builder()
                .setUseHeader(true)
                .addColumn("scenarioName")
                .addColumn("scenarioLoop", CsvSchema.ColumnType.NUMBER)
                .addColumn("stepName")
                .addColumn("stepLoop", CsvSchema.ColumnType.NUMBER)
                .addColumn("correlationId")
                .addColumn("requestTimeStamp")
                .addColumn("responseDelayMilliSec", CsvSchema.ColumnType.NUMBER)
                .addColumn("responseTimeStamp")
                .addColumn("result")
                .addColumn("method")
                .build();

        CsvMapper csvMapper = new CsvMapper();
        csvMapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);

        ObjectWriter writer = csvMapper.writer(schema.withLineSeparator("\n"));
        try {
            writer.writeValue(
                    new File(MasterReportProperties.TARGET_FULL_REPORT_DIR +
                            MasterReportProperties.TARGET_FULL_REPORT_CSV_FILE_NAME
                            //"_" +
                            //LocalDateTime.now().toString().replace(":", "-") +
                            //".csv"
                    ),
                    masterCsvReportRows);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Exception while Writing full CSV report. Details: " + e);
        }
    }

    /*
   @Method: buildCsvRows
   @Purpose: To map the java lists to CSV pojo
   @Author: Mibin Boban, CCS Senior QAT Analyst
   @Creation: 09/12/2021
   */
    public List<MasterCsvReport> buildCsvRows() {

        MasterCsvReportBuilder csvFileBuilder = MasterCsvReportBuilder.newInstance();

        treeReports.forEach(thisReport ->
                thisReport.getResults().forEach(thisResult -> {

                    csvFileBuilder.scenarioLoop(thisResult.getLoop());
                    csvFileBuilder.scenarioName(thisResult.getScenarioName());

                    thisResult.getSteps().forEach(thisStep -> {
                        csvFileBuilder.stepLoop(thisStep.getLoop());
                        csvFileBuilder.stepName(thisStep.getName());
                        csvFileBuilder.correlationId(thisStep.getCorrelationId());
                        csvFileBuilder.result(thisStep.getResult());
                        csvFileBuilder.method(thisStep.getOperation());
                        csvFileBuilder.requestTimeStamp(thisStep.getRequestTimeStamp().toString());
                        csvFileBuilder.responseTimeStamp(thisStep.getResponseTimeStamp().toString());
                        csvFileBuilder.responseDelayMilliSec(thisStep.getResponseDelay());
                        csvRows.add(csvFileBuilder.build());

                    });
                })
        );

        return csvRows;
    }

    /*
   @Method: readMasterReportsByPath
   @Purpose: To read master report with path assigned
   @Author: Mibin Boban, CCS Senior QAT Analyst
   @Creation: 09/12/2021
   */
    public List<MasterReport> readMasterReportsByPath(String reportsFolder) {

        validateReportsFolderAndTheFilesExists(reportsFolder);

        List<String> allEndPointFiles = getAllEndPointFilesFrom(reportsFolder);

        List<MasterReport> scenarioReports = allEndPointFiles.stream()
                .map(reportJsonFile -> {
                    try {
                        return mapper.readValue(new File(reportJsonFile), MasterReport.class);
                    } catch (IOException e) {
                        e.printStackTrace();

                        throw new RuntimeException("Exception while deserializing to Master Report. Details: " + e);

                    }
                })
                .collect(Collectors.toList());

        for (MasterReport masterReport : scenarioReports) {
            for (MasterExecResult masterExecResult : masterReport.getResults()) {
                masterExecResult.setSteps(getUniqueSteps(masterExecResult.getSteps()));
            }
        }
        return scenarioReports;
    }


    public static List<String> getAllEndPointFilesFrom(String folderName) {

        File[] files = new File(folderName).listFiles((dir, name) -> {
            return name.endsWith(".json");
        });

        if (files == null || files.length == 0) {

            LOGGER.error("\n\t\t\t************\nNow files were found in folder:{}, hence could not proceed. " +
                    "\n(If this was intentional, then you can safely ignore this error)" +
                    " \n\t\t\t************** \n\n", folderName);
            return emptyList();

        } else {
            return ofNullable(Arrays.asList(files)).orElse(emptyList()).stream()
                    .map(thisFile -> thisFile.getAbsolutePath())
                    .collect(Collectors.toList());
        }
    }

    /*
   @Method: validateReportsFolderAndTheFilesExists
   @Purpose: To validate folder path and files exists
   @Author: Mibin Boban, CCS Senior QAT Analyst
   @Creation: 09/12/2021
   */
    protected void validateReportsFolderAndTheFilesExists(String reportsFolder) {

        try {
            File[] files = new File(reportsFolder).listFiles((dir, fileName) -> fileName.endsWith(".json"));

            ofNullable(files).orElseThrow(() -> new RuntimeException("Somehow the '" + reportsFolder + "' has got no files."));

        } catch (Exception e) {
            final String message = "\n----------------------------------------------------------------------------------------\n" +
                    "Somehow the '" + reportsFolder + "' is not present or has no report JSON files. \n" +
                    "Possible reasons- \n" +
                    "   1) No tests were activated or made to run via CCS Master runner. -or- \n" +
                    "   2) You have simply used @RunWith(...) and ignored all tests -or- \n" +
                    "   3) Permission issue to create/write folder/files \n" +
                    "   4) Please fix it by adding/activating at least one test case or fix the file permission issue\n" +
                    "   5) If you are not concerned about reports, you can safely ignore this\n" +
                    "----------------------------------------------------------------------------------------\n";

            throw new RuntimeException(message + e);
        }

    }

    private static Date utilDateOf(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    private String createTimeStampedFileName() {
        return MasterReportProperties.HIGH_CHART_HTML_FILE_NAME +
                LocalDateTime.now().toString().replace(":", "-") +
                ".html";
    }

}
