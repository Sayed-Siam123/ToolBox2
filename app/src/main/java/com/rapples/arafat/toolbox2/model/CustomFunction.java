package com.rapples.arafat.toolbox2.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "CUSTOMFUNCTION")
public class CustomFunction {
    @PrimaryKey(autoGenerate = true)
    public int id;
    private String fileName;
    private String date;


    public CustomFunction(String fileName, String date) {
        this.fileName = fileName;
        this.date = date;
    }
    @Ignore
    public CustomFunction(int id, String fileName, String date) {
        this.id = id;
        this.fileName = fileName;
        this.date = date;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
