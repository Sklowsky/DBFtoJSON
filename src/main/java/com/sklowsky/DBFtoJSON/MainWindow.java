package com.sklowsky.DBFtoJSON;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class MainWindow {
    public static String IBPath;
    public static String IBType;
    public static String outputPath;
    public static Integer MesLevel = 0;
//Коды возврата:
    //101 - командная строка пуста
    //102 - файл не найден
    //103 - ошибки синтаксиса командной строки
    //104 - не заполнен обязательный параметр -t (имя файла таблицы)
    public static void main(String[] args) {
        boolean allOk= true;
        if (args.length==0){
            System.out.println("Командная строка пуста");
            System.exit(101);
        }
        else{
            Map<String, String> mapArg = new HashMap<>();
            String keyMap = "";
            String valueMap;
            ArrayList<String> listKey = new ArrayList<>();
            boolean isKey;
             for (String item:args){
                isKey = item.startsWith("-");
                if (isKey){
                    keyMap = switch (item.substring(1).toUpperCase()){
                        case "T"->"Table";
                        case "OD"->"OutputDir";
                        case "M"->"MessageLevel";
                        default ->"NONE";
                    };
                    if (listKey.contains(keyMap)) {
                        allOk = false;
                        break;
                    }
                    listKey.add(keyMap);
                }
                else {
                    valueMap = item;
                    mapArg.put(keyMap,valueMap);
                    if (keyMap.equals("MessageLevel")){
                        MesLevel = Integer.parseInt(valueMap);
                    }
                }
             }
             if (allOk){
                 Setting setINI;
                 try {
                     setINI = new Setting();
                 } catch (IOException e) {
                     throw new RuntimeException(e);
                 }
                 setINI.getSetting();
                 saveDBFtoJSON(mapArg);
             }
             else{
                 if (MesLevel == 0){
                     System.out.println("В параметрах командной строки обнаружены ошибки");
                     System.exit(103);
                 }
             }
        }
    }

    private static void saveDBFtoJSON(Map<String, String> mapArg){
        if (mapArg.get("Table")==null){
            if (MesLevel == 0) {
                System.out.println("Отсутствует обязательный параметр -T (имя файла таблицы)");
            }
            System.exit(104);
        }
        String curTable = mapArg.get("Table").toUpperCase();
        if(!curTable.endsWith(".DBF")){
            curTable = curTable + ".dbf";
        }
        if (MesLevel == 0) {
            System.out.printf("Будет выгружена таблица %s из каталога ИБ %s", curTable, MainWindow.IBPath);
            System.out.println();
        }
        String pathIB = Paths.get(MainWindow.IBPath,curTable).toString();
        File fileIB = new File(pathIB);
        if (fileIB.exists()){
            WriteJSON writer = new WriteJSON(curTable, new ArrayList<Map<String,
                    Object>>(MainWindowController.OpenCurrentFile(pathIB, curTable, true, false)),
            new ArrayList<Map<String,Object>>(MainWindowController.OpenCurrentFile(Paths.get(MainWindow.IBPath,curTable).toString(), curTable, false, true)));
            String strJSON;
            try {
                strJSON = writer.save();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            String outDir = MainWindow.outputPath;
            Path pathJSON = Paths.get(outDir,curTable.toLowerCase().replace(".dbf",".json"));
            if (!strJSON.equals("")){
                try {
                    Files.writeString(pathJSON,strJSON, StandardCharsets.UTF_8);
                    if (MesLevel == 0) {
                        System.out.println("Данные таблицы " + curTable.toUpperCase() + " выгружены в файл " + pathJSON);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        else{
            if (MesLevel == 0) {
                System.out.println("Файл " + curTable + " отсутствует в каталоге " + MainWindow.IBPath);
                System.exit(102);
            }
        }
    }
}