package com.rapples.arafat.toolbox2.model;

public class Product {
    String barcode;
    String description;

    public Product(String barcode, String description) {
        this.barcode = barcode;
        this.description = description;
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
}
