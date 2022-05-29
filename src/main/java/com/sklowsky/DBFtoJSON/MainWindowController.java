package com.sklowsky.DBFtoJSON;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.*;

import com.linuxense.javadbf.*;

public class MainWindowController {

    public static ArrayList<Map<String, Object>> OpenCurrentFile(String PathDBF, String filename, Boolean onlyData, Boolean onlyStruc){
        DBFReader reader = null;
        try {

            reader = new DBFReader(new FileInputStream(PathDBF), Charset.forName("CP1251"));
//            reader.setCharactersetName("CP1251");

            int numberOfFields = reader.getFieldCount();

            ArrayList<Map<String, Object>> itemsData = new ArrayList<Map<String, Object>>();
            ArrayList<Map<String, Object>> itemsStruc = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < numberOfFields; i++) {

                DBFField field = reader.getField(i);
                if(field.getType()==DBFDataType.MEMO){
                    String filenameMemo;
                    String IBType = MainWindow.IBType.toUpperCase();
                    if (IBType.equals("FOX")) {
                        filenameMemo = filename.toLowerCase().replace(".dbf",".fpt");
                    }
                    else {
                        filenameMemo = filename.toLowerCase().replace(".dbf",".dbt");
                    }
                    String memoPath = Paths.get(MainWindow.IBPath,filenameMemo).toString();
                    File fileMemo = new File(memoPath);
                    if (fileMemo.exists()) {
                        reader.setMemoFile(fileMemo);
                        if (MainWindow.MesLevel==2 || onlyStruc) {
                            System.out.println("MEMO: " + field.getName());
                        }
                    }
                    else{
                        continue;
                    }
                }
                Map<String, Object> item = new HashMap<>();
                item.put("fldName",field.getName());
                item.put("fldType",field.getType());
                item.put("fldLenth",field.getLength());
                item.put("fldPrec",field.getDecimalCount());
                itemsStruc.add(item);
            }
            if(onlyStruc){
                return itemsStruc;
            }

            Object[] rowObjects;

            while ((rowObjects = reader.nextRecord()) != null) {
                Map<String, Object> item = new HashMap<>();
//                    item.clear();
                for (int i = 0; i < rowObjects.length; i++) {
                    item.put(reader.getField(i).getName(), rowObjects[i]);
                }
                itemsData.add(item);
            }
            if (!onlyData) {
                return null;
            }
            else  {
                return itemsData;
            }


        } catch (DBFException | FileNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            DBFUtils.close(reader);
        }
        return null;
    }
}