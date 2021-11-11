/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor:
 * Authors:
 * Contributors:
 */
package ccs.master.execution_manager.management;

import ccs.master.support_centre.filehelper.ExcelFileHelper;
import ccs.master.support_centre.propertymanager.CcsMasterPropertyHandler;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.poi.ss.usermodel.*;

import java.util.HashMap;
import java.util.Iterator;

/*
@Class: TestCaseRepository
@Purpose: This class manages the test cases in excel format
@Author: Mibin Boban, CCS Test Analyst
@Creation: 03/11/2021
*/
public class TestCaseRepository {
    public static HashMap<String, String> testcaseHashMap=new HashMap<String, String>();
    public static MultiValuedMap<String, String> testcaseFailedHashMap=new ArrayListValuedHashMap<>();
    public static MultiValuedMap<String, String> testcasePassedHashMap=new ArrayListValuedHashMap<>();

    /*
    @Method: loadTestCaseHashMap
    @Purpose: To load the test cases to a hashmap
    @Author: Mibin Boban, CCS Test Analyst
    @Creation: 03/11/2021
    */
    public static void loadTestCaseHashMap(String testcaseExcelPath, String sheetName){
        ExcelFileHelper excelUtils = new ExcelFileHelper();
        ClassLoader classLoader = TestCaseRepository.class.getClassLoader();
        Workbook workbook=excelUtils.openExcelWorkBook(testcaseExcelPath);
        Sheet sheet=excelUtils.openExcelWorkSheet(workbook,sheetName);
        fetchTestCaseDetails(sheet);
        excelUtils.closeExcelWorkBook(workbook);
    }

    /*
    @Method: fetchTestCaseDetails
    @Purpose: To get the test case and linked details from excel file
    @Author: Mibin Boban, CCS Test Analyst
    @Creation: 03/11/2021
    */
    public static void fetchTestCaseDetails(Sheet sheetName) {
        int testCaseIDIndex=0;
        boolean startFlag=true;
        try {
            Iterator<Row> rowIterator = sheetName.rowIterator();
            DataFormatter dataFormatter = new DataFormatter();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if(startFlag==true) {
                    Iterator<Cell> columnIterator=row.cellIterator();
                    while(columnIterator.hasNext()) {
                        Cell cell=columnIterator.next();
                        if(cell.getStringCellValue().toString().equalsIgnoreCase("TestCase_ID")==true) {
                            testCaseIDIndex=cell.getColumnIndex();
                            break;
                        }
                    }
                    startFlag=false;
                }
                testcaseHashMap.put(dataFormatter.formatCellValue(row.getCell(0)).toString(), dataFormatter.formatCellValue(row.getCell(testCaseIDIndex)).toString());
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    /*
    @Method: getTestcaseID
    @Purpose: To get the testcase id, the primary column in excel
    @Author: Mibin Boban, CCS Test Analyst
    @Creation: 03/11/2021
    */
    public static String getTestcaseID(String key) {
        return testcaseHashMap.get(key);
    }

    /*
    @Method: updateTestCaseStatusOnPass
    @Purpose: To update the test case status when execution is passed
    @Author: Mibin Boban, CCS Test Analyst
    @Creation: 03/11/2021
    */
    public static void updateTestCaseStatusOnPass(String script,Integer testCaseId) {
        if(CcsMasterPropertyHandler.TESTMANAGEMENT_RESULT_UPDATE.toBoolean()){
            MultiValuedMap<String, String> testcaseFailedHashMapTemp=testcaseFailedHashMap;
            for(String testCaseIdMappedToTheScript : testcaseFailedHashMapTemp.get(script)) {
                if(testCaseIdMappedToTheScript.equalsIgnoreCase(testCaseId.toString())){
                    testcasePassedHashMap.put(script, testCaseId.toString());
                    testcaseFailedHashMap.removeMapping(script, testCaseId);
                    return;
                }
            }
        }
    }
}
