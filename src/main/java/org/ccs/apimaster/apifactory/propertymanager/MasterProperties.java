package org.ccs.apimaster.apifactory.propertymanager;

import static org.ccs.apimaster.apifactory.utils.SmartUtils.readJsonAsString;

/*
@Purpose: This class manages properties
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 09/12/2021
*/
public interface MasterProperties {
    String PROPERTY_KEY_HOST = "restful.application.endpoint.host";
    String PROPERTY_KEY_PORT = "restful.application.endpoint.context";

    String OK = "Ok";
    String FAILED = "Failed";

    String DSL_FORMAT = readJsonAsString("dsl_formats/dsl_parameterized_values.json");
}
