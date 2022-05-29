package com.sklowsky.DBFtoJSON;

import org.ini4j.Wini;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Setting {

    private final Wini ini;

    public Setting() throws IOException {
        String path = System.getProperty("user.dir");
        File fileSetting = new File(path, "IBSetting.ini");
        ini = new Wini(fileSetting);
    }

    public Boolean getSetting() {
        MainWindow.IBPath = ini.get("Connection","path");
        MainWindow.IBType = ini.get("Connection","type");
        MainWindow.outputPath = ini.get("Output","path");
        return true;
    }

    public Boolean setSetting(HashMap<String, String> mapSet) {
        for (HashMap.Entry<String, String> item:mapSet.entrySet()) {

            ini.put(secName(item.getKey()),itemName(item.getKey()),item.getValue());
        }
        boolean retVal = true;
        try {
            ini.store();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return retVal;
    }

    private String secName(String varName){
        return switch (varName){
            case "IBPath", "IBType" ->"Connection";
            case "outputPath"->"Output";
            default ->"";
        };

    }

    private String itemName(String varName){
        return switch (varName){
            case "IBPath", "outputPath" ->"path";
            case "IBType"->"type";
            default ->"";
        };

    }
}

