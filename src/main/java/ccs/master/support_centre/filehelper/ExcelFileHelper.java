/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor:
 * Authors:
 * Contributors:
 */
package ccs.master.support_centre.filehelper;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.TestNGException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Iterator;

/*
@Class: ExcelFileHelper
@Purpose: This class is the support class for excel file based operations
@Author: Mibin Boban, CCS Test Analyst
@Creation: 01/11/2021
*/
public class ExcelFileHelper {

    /*
    @Method: openExcelWorkBook
    @Purpose: To open the excel workbook based on file format
    @Author: Mibin Boban, CCS Test Analyst
    @Creation: 03/11/2021
    */
    @SuppressWarnings("resource")
    public Workbook openExcelWorkBook(String translationExcelPath) {
        Workbook workbook;
        FileInputStream excelFile;
        try {
            excelFile = new FileInputStream(new File(translationExcelPath));
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new TestNGException("The specified excel not found in the path "+translationExcelPath,e);
        }
        try {
            if(translationExcelPath.toLowerCase().contains(".xlsx")) {
                workbook = new XSSFWorkbook(excelFile);
            }
            else if(translationExcelPath.toLowerCase().contains(".xls")) {
                workbook = new HSSFWorkbook(excelFile);
            }
            else {
                throw new TestNGException(translationExcelPath+" File format is not supported . Only .xls / .xlsx will be supported");
            }
        }
        catch(Exception e) {
            workbook=null;
            throw new TestNGException("Exception occured while opening the excel",e);

        }
        return workbook;
    }

    /*
   @Method: openExcelWorkSheet
   @Purpose: To open the excel worksheet in a workbook based on sheet name
   @Author: Mibin Boban, CCS Test Analyst
   @Creation: 03/11/2021
   */
    public Sheet openExcelWorkSheet(Workbook workbook, String sheetName) {
        Sheet excelSheet;
        try {
            excelSheet = workbook.getSheet(sheetName);
        }
        catch(Exception e) {
            sheetName=null;
            throw new TestNGException(sheetName+" sheet not found in the excel",e);
        }
        return excelSheet;
    }

    /*
   @Method: openExcelWorkSheet
   @Purpose: To open the excel worksheet in a workbook based on sheet index
   @Author: Mibin Boban, CCS Test Analyst
   @Creation: 03/11/2021
   */
    public Sheet openExcelWorkSheet(Workbook workbook,int sheetIndex) {
        Sheet sheetName;
        try {
            sheetName = workbook.getSheetAt(sheetIndex);
        }
        catch(Exception e) {
            sheetName=null;
            throw new TestNGException("No sheet not found in the excel at index "+sheetIndex,e);
        }
        return sheetName;
    }

    /*
   @Method: writeValueToExcelCell
   @Purpose: To write value to excel
   @Author: Mibin Boban, CCS Test Analyst
   @Creation: 03/11/2021
   */
    public static void writeValueToExcelCell(String testCaseExcelPath, String excelSheetName,String rowKey,String columnName,String cellValue) {
        try {
            ExcelFileHelper excelUtils=new ExcelFileHelper();
            Workbook workbook=excelUtils.openExcelWorkBook(testCaseExcelPath);
            Sheet sheetName=excelUtils.openExcelWorkSheet(workbook,excelSheetName);
            Iterator<Row> rowIterator = sheetName.rowIterator();
            int rowNumber=0;
            int columnNumber=0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if(rowNumber==0) {
                    Iterator<Cell> columnIterator=row.cellIterator();
                    while(columnIterator.hasNext()) {
                        Cell cell=columnIterator.next();
                        if(cell.getStringCellValue().toString().equalsIgnoreCase(columnName)==true) {
                            columnNumber=cell.getColumnIndex();
                            break;
                        }
                    }
                }
                if(row.getCell(0).toString().equalsIgnoreCase(rowKey)==true) {
                    Cell cell=row.getCell(columnNumber);
                    if (cell == null)
                        cell = row.createCell(columnNumber);
                    cell.setCellValue(cellValue);
                    break;
                }
                rowNumber++;
            }

            FileOutputStream fileOut = new FileOutputStream(testCaseExcelPath);
            workbook.write(fileOut);
            fileOut.close();
            excelUtils.closeExcelWorkBook(workbook);
        }
        catch(Exception e) {
            throw new TestNGException("Exception occured while writing to the excel. sheet path-- "+testCaseExcelPath
                    +" sheet Name-->"+excelSheetName+" Row Key-->"+rowKey+" Column Key-->"+columnName+" Cell value-->"+cellValue,e);
        }
    }

    /*
   @Method: closeExcelWorkBook
   @Purpose: To close the excel workbook
   @Author: Mibin Boban, CCS Test Analyst
   @Creation: 03/11/2021
   */
    public void closeExcelWorkBook(Workbook workbook) {
        try {
            workbook.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
