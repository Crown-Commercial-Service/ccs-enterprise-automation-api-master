/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor:
 * Authors:
 * Contributors:
 */
package ccs.master.support_centre.propertymanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/*
@Purpose: This class manage the master framework level and individial project level properties
@Author: Mibin Boban, CCS Test Analyst
@Creation: 29/10/2021
*/
public enum CcsMasterPropertyHandler {

    //#TEST PROPERTIES DETAILS
    SELENIUM_HOST("selenium.grid.host","localhost", Type.String),
    SELENIUM_PORT("selenium.grid.port",4444,Type.Integer),
    TESTNG_PACKAGE("testng.testcase.package","",Type.String),
    TESTNG_GROUP_INCLUDE("testng.group.include","",Type.String),
    TESTNG_GROUP_EXCLUDE("testng.group.exclude","",Type.String),
    TESTNG_THREAD_COUNT("testng.thread.count",1,Type.Integer),
    TESTNG_DATA_PROVIDER_COUNT("testng.dataprovider.thread.count",1,Type.Integer),
    TESTCASE_RETRY("testng.retry.count",1,Type.Integer),
    TEST_ENVIRONMENT("test.environment","environment",Type.String),
    TEST_TIMEOUT("test.timeout",90,Type.Integer),
    TEST_BROWSER("test.browser","",Type.String),
    TEST_TYPE("test.type","",Type.String),

    //#TEST MANAGMENT DETAILS
    TESTMANAGEMENT_RESULT_UPDATE("testmanagement.result.update",false,Type.Boolean),
    TEST_URL_EXCEL_PATH("test.url.excel.path","./TestUrl.xlsx",Type.String),
    TESTDATA_EXCEL_PATH("testdata.excel.path","./TestData.xlsx",Type.String),
    MOBILE_EXECUTION("","false",Type.Boolean),
    MOBILE_DEVICE("","",Type.String),
    HEADER_NAME("header.name",true,Type.String),
    HEADER_VALUE("header.value",true,Type.String),

    //#DB DETAILS
    DB_HOST_NAME("db.hostname","",Type.String),
    DB_PORT_NO("db.portno","",Type.Integer),
    DB_USER_NAME("db.username","admin",Type.String),
    DB_PASSWORD("db.password","admin",Type.String),
    ;

    private String key;
    private Object value;
    private Type type;
    private enum Type{Integer,String,Boolean,Long};

    private CcsMasterPropertyHandler(String key,Object value,Type type) {
        this.key=key;
        this.value=value;
        this.type=type;
    }

    public void setProperty(Object val){
        this.value=val;
    }
    public Object getProperty(){
        return this.value;
    }
    @Override
    public String toString() {
        return this.value.toString();
    }
    public Integer toInteger() {
        return Integer.parseInt(this.value.toString());
    }
    public Long toLong() {
        return Long.parseLong(this.value.toString());
    }
    public Boolean toBoolean() {
        return Boolean.parseBoolean(this.value.toString());
    }

    /*
    @Method: loadProjectLevelProperties
    @Purpose: To read and handle project level test properties
    @Author: Mibin Boban, CCS Test Analyst
    @Creation: 29/10/2021
    */
    public static void loadProjectLevelProperties() throws FileNotFoundException, IOException{
        Properties p=new Properties();
        InputStream input = new FileInputStream(System.getProperty("user.dir")+"/src/main/resources/properties/test.properties");
        p.load(input);
        //p.forEach((keys,vals)->System.out.println(keys+"="+vals));
        for(CcsMasterPropertyHandler projPrty: CcsMasterPropertyHandler.values()){
            String valFromFile=p.getProperty(projPrty.key);
            if(valFromFile!=null){
                if(projPrty.type==Type.Integer){
                    projPrty.setProperty(Integer.parseInt(valFromFile));
                }
                else if(projPrty.type==Type.Long){
                    projPrty.setProperty(Long.parseLong(valFromFile));
                }
                else if(projPrty.type==Type.Boolean){
                    projPrty.setProperty(Boolean.parseBoolean(valFromFile));
                }
                else
                    projPrty.setProperty(valFromFile);
            }
        }
    }

    /*
    @Method: loadMasterLevelProperties
    @Purpose: To read and handle framework level test properties
    @Author: Mibin Boban, CCS Test Analyst
    @Creation: 29/10/2021
    */
    public static void loadMasterLevelProperties() throws FileNotFoundException, IOException{
        Properties p=new Properties();
        p.load(CcsMasterPropertyHandler.class.getClassLoader().getResourceAsStream("properties/frameworkTest.properties"));
        //p.forEach((keys,vals)->System.out.println(keys+"="+vals));
        for(CcsMasterPropertyHandler masterPrty: CcsMasterPropertyHandler.values()){
            String valFromFile=p.getProperty(masterPrty.key);
            if(valFromFile!=null){
                if(masterPrty.type==Type.Integer){
                    masterPrty.setProperty(Integer.parseInt(valFromFile));
                }
                else if(masterPrty.type==Type.Long){
                    masterPrty.setProperty(Long.parseLong(valFromFile));
                }
                else if(masterPrty.type==Type.Boolean){
                    masterPrty.setProperty(Boolean.parseBoolean(valFromFile));
                }
                else
                    masterPrty.setProperty(valFromFile);
            }
        }
    }
    public static void main(String arg[]) throws FileNotFoundException, IOException{
        CcsMasterPropertyHandler.loadMasterLevelProperties();
    }
}
