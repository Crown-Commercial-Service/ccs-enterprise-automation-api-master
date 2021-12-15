/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.coreactions.datamocker;

import com.github.jknack.handlebars.Options;
import com.github.tomakehurst.wiremock.extension.responsetemplating.helpers.DateOffset;
import com.github.tomakehurst.wiremock.extension.responsetemplating.helpers.HandlebarsHelper;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/*
@Purpose: This class helps in local date handling
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 29/11/2021
*/
public class HandlebarsLocalDateHelper extends HandlebarsHelper {

    public Object apply(Object context, Options options) {
        String offset = options.hash("offset", null);
        Date date = new Date();
        if (offset != null) {
            date = (new DateOffset(offset)).shift(date);
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME);
    }
}
