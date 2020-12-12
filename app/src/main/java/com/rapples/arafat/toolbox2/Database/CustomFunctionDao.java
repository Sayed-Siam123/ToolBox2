package com.rapples.arafat.toolbox2.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.rapples.arafat.toolbox2.model.CustomFunction;
import com.rapples.arafat.toolbox2.model.DataAcquisition;

import java.util.List;

@Dao
public interface CustomFunctionDao {

    @Query("SELECT * FROM CUSTOMFUNCTION ORDER BY id")
    List<CustomFunction> loadAllData();

    @Insert
    void insertCustomFunctionData(CustomFunction customFunction);

    @Update
    void updateCustomFunctionData(CustomFunction customFunction);

    @Delete
    void deleteCustomFunctionData(CustomFunction customFunction);

    @Query("SELECT * FROM CUSTOMFUNCTION WHERE id = :id")
    DataAcquisition loadCustomFunctionDataById(int id);

}
