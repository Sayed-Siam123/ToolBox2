package com.rapples.arafat.toolbox2.Database;


import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.rapples.arafat.toolbox2.model.CustomFunction;
import com.rapples.arafat.toolbox2.model.CustomFunctionProduct;
import com.rapples.arafat.toolbox2.model.Product;

@Database(entities = {CustomFunction.class, CustomFunctionProduct.class}, version = 1, exportSchema = false)
public abstract class CustomFunction_DB extends RoomDatabase {

    private static final String LOG_TAG = CustomFunction_DB.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "CustomFunction";
    private static CustomFunction_DB sInstance;

    public static CustomFunction_DB getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        CustomFunction_DB.class, CustomFunction_DB.DATABASE_NAME)
                        .build();
            }
        }
        Log.d(LOG_TAG, "Getting the database instance");
        return sInstance;
    }

    public abstract CustomFunctionDao CustomFunctionDao();

    public abstract CustomFunctionProductDao CustomFunctionProductDao();
}
