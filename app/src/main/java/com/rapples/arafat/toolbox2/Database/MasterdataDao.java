package com.rapples.arafat.toolbox2.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.rapples.arafat.toolbox2.model.Masterdata;

import java.util.List;

@Dao
public interface MasterdataDao {

    @Query("SELECT * FROM MASTERDATA ORDER BY id")
    List<Masterdata> loadAllData();

    @Insert
    void insertPerson(Masterdata masterdata);

    @Update
    void updatePerson(Masterdata masterdata);

    @Delete
    void delete(Masterdata masterdata);

    @Query("SELECT * FROM MASTERDATA WHERE id = :id")
    Masterdata loadPersonById(int id);

}
