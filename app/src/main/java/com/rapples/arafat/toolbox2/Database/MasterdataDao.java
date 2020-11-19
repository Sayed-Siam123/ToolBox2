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

    @Query("SELECT * FROM MASTERDATA ORDER BY ID")
    List<Masterdata> loadAllPersons();

    @Insert
    void insertPerson(Masterdata person);

    @Update
    void updatePerson(Masterdata person);

    @Delete
    void delete(Masterdata person);

    @Query("SELECT * FROM MASTERDATA WHERE ID = :id")
    Masterdata loadPersonById(int id);

}
