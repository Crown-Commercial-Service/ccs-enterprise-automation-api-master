/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor:
 * Authors:
 * Contributors:
 */
package ccs.master.support_centre.filehelper;

import java.io.*;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
@Class: ConfigFileHelper
@Purpose: This class manages the config file operations
@Author: Mibin Boban, CCS Test Analyst
@Creation: 03/11/2021
*/
public class ConfigFileHelper {
    private final static Logger LOGGER = Logger.getLogger(ConfigFileHelper.class.getName());

    /*
    @Method: generateAllureReport
    @Purpose: To handle allure reporting logs
    @Author: Mibin Boban, CCS Test Analyst
    @Creation: 03/11/2021
    */
    public static void generateAllureReport()
    {
        try
        {
            String cmd="";
            Process p = Runtime.getRuntime().exec(cmd);
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            @SuppressWarnings("unused")
            String line = null;
            while ((line = input.readLine()) != null) {}
            while ((line = error.readLine()) != null) {}
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /*
    @Method: initialize
    @Purpose: To initialize the config property file
    @Author: Mibin Boban, CCS Test Analyst
    @Creation: 03/11/2021
    */
    public static Properties initialize(String filePath)  {
        SortedProperties testdataConfig = new SortedProperties();
        try {
            testdataConfig.load(new FileInputStream(filePath));
        }
        catch (Exception e) {LOGGER.log(Level.SEVERE,"script exception"+e);}
        return testdataConfig;
    }

    /*
    @Method: readConfigFile
    @Purpose: To read the config file
    @Author: Mibin Boban, CCS Test Analyst
    @Creation: 03/11/2021
    */
    public static String readConfigFile(String key,String filePath) {
        /*
         * Input Type : String
         * Return Type : String
         * Description : This method will fetch the property value for given key
         */
        String propertyValue=null;
        try{

            Properties testdataConfig=initialize(filePath);

            propertyValue = testdataConfig.getProperty(key);
        }
        catch (Exception e) {LOGGER.log(Level.SEVERE,"script exception"+e);}
        return propertyValue;
    }

    /*
    @Method: writeConfigFile
    @Purpose: To write the config files
    @Author: Mibin Boban, CCS Test Analyst
    @Creation: 03/11/2021
    */
    public static void writeConfigFile(String filePath,String key,String value){
        try {
            Properties testdataConfig =initialize(filePath);
            File file = new File(filePath);
            OutputStream writeContents=new FileOutputStream(file);
            testdataConfig.setProperty(key, value);
            testdataConfig.store(writeContents, "");
            writeContents.close();
        } catch (Exception e){LOGGER.log(Level.SEVERE,"script exception"+e);}
    }
}

/*
@Class: SortedProperties
@Purpose: This class sorts the properties in config files
@Author: Mibin Boban, CCS Test Analyst
@Creation: 03/11/2021
*/
@SuppressWarnings("serial")
class SortedProperties extends Properties {
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Enumeration keys() {
        Enumeration keysEnum = super.keys();
        Vector<String> keyList = new Vector<String>();
        while(keysEnum.hasMoreElements()){
            keyList.add((String)keysEnum.nextElement());
        }
        Collections.sort(keyList);
        return keyList.elements();
    }
}