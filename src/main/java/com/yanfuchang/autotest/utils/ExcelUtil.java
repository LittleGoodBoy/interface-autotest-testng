package com.yanfuchang.autotest.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * excel工具类,提供读取指定路径下的excel为二维数组,为testng测试用例提供数据驱动
 */
public class ExcelUtil {

    //	private String filePath = ".\\testData\\testCaseData.xlsx";
    private String filePath;
    public static final String EXTENSION_XLS = "xls";
    public static final String EXTENSION_XLSX = "xlsx";

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public ExcelUtil(String filePath) {
        this.setFilePath(filePath);
    }

    /**
     * 根据excel后缀判断获取哪种excel解析对象 例如：.xls | .xlsx
     *
     * @return
     */
    public Workbook getWorkbook() {
        Workbook workbook = null;
        try {
            InputStream is = new FileInputStream(filePath);
            if (filePath.endsWith(EXTENSION_XLS)) {
                workbook = new HSSFWorkbook(is);
            } else if (filePath.endsWith(EXTENSION_XLSX)) {
                workbook = new XSSFWorkbook(is);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return workbook;
    }

    /**
     * 获取指定页对象
     *
     * @param sheetIndex
     * @return
     */
    public Sheet getSheet(int sheetIndex) {
        return getWorkbook().getSheetAt(sheetIndex);
    }

    /**
     * 获取指定页、行、列的值
     *
     * @param sheetIndex
     * @param rowIndex
     * @param cellIndex
     * @return
     */
    public Object getValue(int sheetIndex, int rowIndex, int cellIndex) {
        Cell cell = getSheet(sheetIndex).getRow(rowIndex).getCell(cellIndex);
        return getCellValue(cell);
    }

    private static Object getCellValue(Cell cell) {
        Object value = "";
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_BLANK:
                value = "";
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                value = cell.getBooleanCellValue();
                break;
            case Cell.CELL_TYPE_ERROR:
                value = "";
                break;
            case Cell.CELL_TYPE_FORMULA:
                value = cell.getCellFormula();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                value = cell.getNumericCellValue();
                break;
            case Cell.CELL_TYPE_STRING:
                value = cell.getStringCellValue();
                break;
            default:
                value = cell.getDateCellValue();
                break;
        }
        return value;
    }

    /**
     * 读取excel某一页的数据存储到二维数组中
     *
     * @return
     */
    public Object[][] getArrayCellValue(int sheetIndex) {
        // 初始化返回值
        Object[][] testCaseData = null;
        // 用例总行数 getLastRowNum()行数从0开始
        int totalRowIndex = getSheet(sheetIndex).getLastRowNum();
        // 以第一行标题为基准获取用例的列数 getLastCellNum()列数从1开始
        short lastCellNum = getSheet(sheetIndex).getRow(0).getLastCellNum();
        // 初始化二维数组
        testCaseData = new Object[totalRowIndex][lastCellNum];

        // 行从1开始,不读取第一行的用例标题
        for (int rowIndex = 1; rowIndex <= totalRowIndex; rowIndex++) {
            // 通过sheet指定到rowIndex那行
            Row row = getSheet(sheetIndex).getRow(rowIndex);
            // 空行跳过
            if (row == null) {
                continue;
            }
            // 指定到行后,遍历每列值
            for (int cellIndex = 0; cellIndex < row.getLastCellNum(); cellIndex++) {
                Cell cell = row.getCell(cellIndex);
                if (cell == null) {
                    testCaseData[rowIndex - 1][cellIndex] = "";
                } else {
                    testCaseData[rowIndex - 1][cellIndex] = getCellValue(cell);
                }
            }
        }
        return testCaseData;
    }

    // 测试
    public static void main(String[] args) {
//		Object[][] cellValueToArray = getArrayCellValue(0);
//		for (int i = 0; i < cellValueToArray.length; i++) {
//			for (int j = 0; j < cellValueToArray[i].length; j++) {
//				System.out.println(cellValueToArray[i][j]);
//			}
//		}
    }
}
