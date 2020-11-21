package com.rapples.arafat.toolbox2.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;



@Entity(tableName = "MASTERDATA")
public class Masterdata {
    @PrimaryKey(autoGenerate = true)
    int id;
    private String barcode;
    private String description;
    private String price;
    private String image;

    @Ignore
    public Masterdata(int id, String barcode, String description, String price, String image) {
        this.id = id;
        this.barcode = barcode;
        this.description = description;
        this.price = price;
        this.image = image;
    }

    public Masterdata(String barcode, String description, String price, String image) {
        this.barcode = barcode;
        this.description = description;
        this.price = price;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
