package com.rapples.arafat.toolbox2;

import com.rapples.arafat.toolbox2.model.DataAcquisition;
import com.rapples.arafat.toolbox2.model.Field;

import java.util.List;

public interface OnFieldChangeListener {
    void updateFieldList(List<Field> fieldList);
}
