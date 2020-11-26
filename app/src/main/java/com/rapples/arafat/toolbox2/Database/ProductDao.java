package com.rapples.arafat.toolbox2.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.rapples.arafat.toolbox2.model.DataAcquisition;
import com.rapples.arafat.toolbox2.model.Product;

import java.util.List;

@Dao
public interface ProductDao {

    @Query("SELECT * FROM PRODUCT WHERE fileId = :fileName")
    List<Product> loadAllproduct(String fileName);

    @Insert
    void insertProduct(Product product);

    @Update
    void updateProduct(Product product);

    @Delete
    void deleteProduct(Product product);

    @Query("SELECT * FROM PRODUCT WHERE id = :id")
    Product loadProductById(int id);

}
