package com.bijoy2unicode;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        Scanner sc= new Scanner(System.in);
        System.out.print("Enter path of the file: ");
        String path = sc.nextLine();

        FileInputStream fis=new FileInputStream(new File(path));
        File file = new File(path);
        XSSFWorkbook wb=new XSSFWorkbook(fis);
        int sheetCount = wb.getNumberOfSheets();
        Workbook workbook = new XSSFWorkbook();
        System.out.println("Please wait. This may take a while.");
        for(int k=0; k<sheetCount; k++){
            XSSFSheet sheet=wb.getSheetAt(k);
            FormulaEvaluator formulaEvaluator=wb.getCreationHelper().createFormulaEvaluator();
            String cellValue="0";
            int i = 0;
            String sheetName = wb.getSheetName(k);
            Sheet createSheet = workbook.createSheet(sheetName);
            for(Row row: sheet)
            {
                int j = 0;
                Row createRow = createSheet.createRow(i);
                for (Cell cell : row)
                {
                    Cell createCell = createRow.createCell(j);
                    switch (cell.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            cellValue = cell.getStringCellValue();
                            break;

                        case Cell.CELL_TYPE_FORMULA:
                            cellValue = cell.getCellFormula();
                            break;

                        case Cell.CELL_TYPE_NUMERIC:
                            if (DateUtil.isCellDateFormatted(cell)) {
                                cellValue = cell.getDateCellValue().toString();
                            } else {
                                BigDecimal b = new BigDecimal(cell.getNumericCellValue(), MathContext.DECIMAL64);
                                cellValue = String.valueOf(b);
                            }
                            break;

                        case Cell.CELL_TYPE_BLANK:
                            cellValue = "";
                            break;

                        case Cell.CELL_TYPE_BOOLEAN:
                            cellValue = Boolean.toString(cell.getBooleanCellValue());
                            break;
                    }
                    createCell.setCellValue(unicode(cellValue));
                    j++;
                }
                i++;
            }
        }

//        File currDir = new File(".");
//        String filePath = currDir.getAbsolutePath();
//        String fileLocation = filePath.substring(0, filePath.length() - 1) + file.getName();
        Scanner enterPath= new Scanner(System.in);
        System.out.print("Enter path of the file: ");
        String outPutPath = sc.nextLine();
        File currDir = new File(outPutPath);
        String filePath = currDir.getPath()+"\\";
        String fileLocation = filePath + file.getName();

        FileOutputStream outputStream = new FileOutputStream(fileLocation);
        workbook.write(outputStream);
        workbook.close();
        System.out.println("Successfully converted to unicode and stored in "+fileLocation);
    }

    public static String unicode(String data){
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        try {
            Characters characters = new Characters();
            File currDir = new File(".");
            String filePath = currDir.getAbsolutePath();
            engine.eval(new FileReader(filePath.substring(0, filePath.length() - 1) + "js\\converter.js"));
            Invocable invocable = (Invocable) engine;
            String convertedFrom = "bijoy";
            for(int i=0; i<characters.listOfCharacters.length; i++){
                boolean check = data.contains(characters.listOfCharacters[i]);
                if(check==true){
                    String result;
                    result = (String) invocable.invokeFunction("ConvertToUnicode", convertedFrom, data);
                    data = result;
                    //data = ReArrangeUnicodeConvertedText(result, false);
                    break;
                }
            }
            return data;
        }
        catch (FileNotFoundException | NoSuchMethodException | ScriptException e) {
            return data;
        }
        catch (IOException e) {
            return data;
        }
    }
}


