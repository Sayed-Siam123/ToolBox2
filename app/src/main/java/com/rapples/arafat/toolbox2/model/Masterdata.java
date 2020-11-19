package com.rapples.arafat.toolbox2.model;

import android.graphics.drawable.Icon;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;



@Entity(tableName = "MASTERDATA")
public class Masterdata {
    @PrimaryKey(autoGenerate = true)
    int ID;
    private String BARCODE;
    private String PROD_DESC;
    private String PRICE;
    private int IMAGE_LOCATION;

    public Masterdata(int ID, String BARCODE, String PROD_DESC, String PRICE, int IMAGE_LOCATION) {
        this.ID = ID;
        this.BARCODE = BARCODE;
        this.PROD_DESC = PROD_DESC;
        this.PRICE = PRICE;
        this.IMAGE_LOCATION = IMAGE_LOCATION;
    }

    @Ignore
    public Masterdata(String BARCODE, String PROD_DESC, String PRICE, int IMAGE_LOCATION) {
        this.BARCODE = BARCODE;
        this.PROD_DESC = PROD_DESC;
        this.PRICE = PRICE;
        this.IMAGE_LOCATION = IMAGE_LOCATION;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getBARCODE() {
        return BARCODE;
    }

    public void setBARCODE(String BARCODE) {
        this.BARCODE = BARCODE;
    }

    public String getPROD_DESC() {
        return PROD_DESC;
    }

    public void setPROD_DESC(String PROD_DESC) {
        this.PROD_DESC = PROD_DESC;
    }

    public String getPRICE() {
        return PRICE;
    }

    public void setPRICE(String PRICE) {
        this.PRICE = PRICE;
    }

    public int getIMAGE_LOCATION() {
        return IMAGE_LOCATION;
    }

    public void setIMAGE_LOCATION(int IMAGE_LOCATION) {
        this.IMAGE_LOCATION = IMAGE_LOCATION;
    }
}
