package com.sklowsky.DBFtoJSON;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import com.google.gson.stream.JsonWriter;

public class WriteJSON {
    private String TableName;
    private final ArrayList<Map<String, Object>> data;
    private final ArrayList<Map<String, Object>> struc;
    public WriteJSON(String tableName, ArrayList<Map<String, Object>> dataDBF, ArrayList<Map<String, Object>> strucDBF) {
        TableName = tableName;
        data = dataDBF;
        struc = strucDBF;
    }

    public String save() throws Exception{
        OutputStream outputStream = new ByteArrayOutputStream();
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(outputStream));
        writer.beginObject(); // main object
        writer.name(TableName);
        writer.beginArray();
            writer.beginObject();
            writer.name("structure");
            writer.beginArray();
            for(Map<String, Object> field:struc){
                writer.beginObject();
                writer.name("name").value(field.get("fldName").toString());
                writer.name("type").value(field.get("fldType").toString());
                writer.name("len").value((Integer) field.get("fldLenth"));
                writer.name("prec").value((Integer) field.get("fldPrec"));
                writer.endObject();
            }
            writer.endArray();
            writer.endObject();//StructureTable

            writer.beginObject();
            writer.name("data");
            writer.beginArray();
            for(Map<String, Object> row:data){
                writer.beginObject();
                for(Map<String, Object> field:struc){
                    String nameField = field.get("fldName").toString();
                    String strVal = "";
                    if (row.get(nameField)!=null){
                        String fldType = field.get("fldType").toString();
                        switch (fldType) {
                            case "DATE":
                                LocalDateTime dateVal = LocalDateTime.parse(row.get(nameField).toString(), DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH));
                                strVal = dateVal.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
                                break;
                            default:
                                Object val = row.get(nameField);
                                strVal = new String(val.toString().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
                        }
                    }
                    writer.name(nameField).value(strVal);
                }
                writer.endObject();
            }
            writer.endArray();
            writer.endObject();//data
        writer.endArray();
        writer.endObject(); //TableName
        writer.close();
        return outputStream.toString();
    }

    public String getTableName() {
        return TableName;
    }

    public void setTableName(String tableName) {
        TableName = tableName;
    }


}
