package com.rapples.arafat.toolbox2.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.rapples.arafat.toolbox2.model.DataAcquisition;
import java.util.List;

@Dao
public interface AcquisitionDao {

    @Query("SELECT * FROM DATAACQUISITION ORDER BY id")
    List<DataAcquisition> loadAllData();

    @Insert
    void insertAcquisitionData(DataAcquisition dataAcquisition);

    @Update
    void updateAcquisitionData(DataAcquisition dataAcquisition);

    @Delete
    void deleteAcquisitionData(DataAcquisition dataAcquisition);

    @Query("SELECT * FROM DATAACQUISITION WHERE id = :id")
    DataAcquisition loadAcquisitionDataById(int id);

}
