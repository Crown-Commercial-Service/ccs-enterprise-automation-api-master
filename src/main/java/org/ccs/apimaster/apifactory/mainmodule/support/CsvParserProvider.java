/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.mainmodule.support;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import javax.inject.Provider;

/*
@Purpose: This class supports CSV file parse
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 09/12/2021
*/
public class CsvParserProvider implements Provider<CsvParser> {
    public static final String LINE_SEPARATOR = "\n";

    @Override
    public CsvParser get() {
        CsvParserSettings settings = createCsvSettings();
        return new CsvParser(settings);
    }

    private CsvParserSettings createCsvSettings() {
        CsvParserSettings settings = new CsvParserSettings();
        settings.getFormat().setDelimiter(",");
        settings.getFormat().setQuote('\'');
        settings.getFormat().setQuoteEscape('\'');
        settings.setEmptyValue("");
        settings.getFormat().setLineSeparator("\n");
        settings.setAutoConfigurationEnabled(false);
        return settings;
    }

}
