package com.rapples.arafat.toolbox2.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.rapples.arafat.toolbox2.model.CustomFunctionProduct;
import com.rapples.arafat.toolbox2.model.Product;

import java.util.List;

@Dao
public interface CustomFunctionProductDao {

    @Query("SELECT * FROM CUSTOMFUNCTIONPRODUCT WHERE fileId = :fileName")
    List<CustomFunctionProduct> loadAllproduct(String fileName);

    @Insert
    void insertProduct(CustomFunctionProduct customFunctionProduct);

    @Update
    void updateProduct(CustomFunctionProduct customFunctionProduct);

    @Delete
    void deleteProduct(CustomFunctionProduct customFunctionProduct);

    @Query("SELECT * FROM CUSTOMFUNCTIONPRODUCT WHERE id = :id")
    Product loadProductById(int id);

}
