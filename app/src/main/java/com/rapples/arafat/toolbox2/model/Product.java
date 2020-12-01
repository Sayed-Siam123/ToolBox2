package com.rapples.arafat.toolbox2.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
@Entity(tableName = "PRODUCT")
public class Product {
    @PrimaryKey(autoGenerate = true)
    public int id;
    private String fileId;
    private String barcode;
    private String description;
    private String quantity;

    @Ignore
    public Product(int id, String fileId, String barcode, String description,String quantity) {
        this.id = id;
        this.fileId = fileId;
        this.barcode = barcode;
        this.description = description;
        this.quantity = quantity;
    }

    public Product(String fileId, String barcode, String description,String quantity) {
        this.fileId = fileId;
        this.barcode = barcode;
        this.description = description;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
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

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
