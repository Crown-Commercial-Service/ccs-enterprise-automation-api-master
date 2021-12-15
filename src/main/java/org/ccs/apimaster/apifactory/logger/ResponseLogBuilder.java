/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.logger;

import java.time.LocalDateTime;

/*
@Purpose: This class manages logging for responses
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 09/12/2021
*/
public class ResponseLogBuilder {
    String relationshipId;
    LocalDateTime responseTimeStamp;
    String response;
    String exceptionMsg;
    String assertion = "{CCS Master: No! Not decided. Possibly due to non JSON content was encountered. See log for details}";
    String customLog;


    public ResponseLogBuilder customLog(String customLog) {
        this.customLog = customLog;
        return this;
    }

    public ResponseLogBuilder relationshipId(String relationshipId) {
        this.relationshipId = relationshipId;
        return this;
    }

    public ResponseLogBuilder responseTimeStamp(LocalDateTime responseTimeStamp) {
        this.responseTimeStamp = responseTimeStamp;
        return this;
    }

    public ResponseLogBuilder response(String response) {
        this.response = response;
        return this;
    }

    public ResponseLogBuilder exceptionMessage(String exceptionMsg) {
        this.exceptionMsg = exceptionMsg;
        return this;
    }

    public LocalDateTime getResponseTimeStamp() {
        return responseTimeStamp;
    }
    public String getResponse() {
		return response;
	}

    @Override
    public String toString() {
        return relationshipId +
                "\nResponse:\n" + response +
                "\n*responseTimeStamp:" + responseTimeStamp;
                //"\n\n---------> Assertion: <----------\n" + assertion;
    }

    public ResponseLogBuilder assertionSection(String assertionJson) {
        this.assertion = assertionJson;
        return this;
    }

    public String getAssertion() {
        return assertion;
    }

    public String getCustomLog() {
        return customLog;
    }

}
