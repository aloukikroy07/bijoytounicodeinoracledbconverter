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
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        String columnName="father_name";
        String unionID="2302";
        ArrayList<String> beneficiaryName = new ArrayList<>();
        ResultSet rs = null;
        Statement stmt = getDbConnection().createStatement();
        String sql = "select distinct "+columnName+" from t_beneficiaries where union_id="+unionID;
        rs = stmt.executeQuery(sql);

        int count=0;

        while (rs.next()){
            beneficiaryName.add(rs.getString(columnName));
        }

        Characters characters = new Characters();
        String[] characterList = characters.listOfCharacters;

        int length = characterList.length;
        int listLength = beneficiaryName.size();

        for(int i=0; i< beneficiaryName.size(); i++){
            for(int j=0; j< length; j++){
                if(beneficiaryName.get(i) != null){
                    if(beneficiaryName.get(i).contains(characterList[j])){
                        String unicodeData = unicode(beneficiaryName.get(i));
                        String sql1 = "update t_beneficiaries set "+columnName+"='"+unicodeData+"' where "+columnName+"='"+beneficiaryName.get(i)+"'";
                        stmt.executeUpdate(sql1);
                        count=count+1;
                        break;
                    }
                }
            }
        }
        System.out.println(count);
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

    public static Connection getDbConnection() throws SQLException, ClassNotFoundException {
        Connection connection = null ;
        try {
            Class.forName("com.oracle.jdbc.Driver");
        } catch (ClassNotFoundException e) {
        }
        try {
            connection = DriverManager.getConnection("jdbc:oracle:thin:@192.168.90.137:1521/FOODDRDB?user=foodprod&password=foodprod");

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return connection;
    }
}


