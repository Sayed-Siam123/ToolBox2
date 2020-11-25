package com.rapples.arafat.toolbox2.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;
//@Entity(tableName = "DataAcquisition")
public class DataAcquisition {
    @PrimaryKey(autoGenerate = true)
    int id;
    String fileName;
    String date;
    List<Product> productList;


    public DataAcquisition(String fileName, String date, List<Product> productList) {
        this.fileName = fileName;
        this.date = date;
        this.productList = productList;
    }

//    @Ignore
    public DataAcquisition(int id, String fileName, String date, List<Product> productList) {
        this.id = id;
        this.fileName = fileName;
        this.date = date;
        this.productList = productList;
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

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }
}
