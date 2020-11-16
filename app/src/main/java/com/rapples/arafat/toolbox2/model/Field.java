package com.rapples.arafat.toolbox2.model;

public class Field {
    private String fieldType;
    private String fieldName;

    public Field(String fieldType, String fieldName) {
        this.fieldType = fieldType;
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
}
