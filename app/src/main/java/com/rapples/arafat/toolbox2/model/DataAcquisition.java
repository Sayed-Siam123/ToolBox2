package com.rapples.arafat.toolbox2.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;
@Entity(tableName = "DATAACQUISITION")
public class DataAcquisition {
    @PrimaryKey(autoGenerate = true)
    public int id;
    private String fileName;
    private String date;


    public DataAcquisition(String fileName, String date) {
        this.fileName = fileName;
        this.date = date;
    }
    @Ignore
    public DataAcquisition(int id, String fileName, String date) {
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
