package com.rapples.arafat.toolbox2.Database;


import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.rapples.arafat.toolbox2.model.Masterdata;

@Database(entities = {Masterdata.class}, version = 1, exportSchema = false)
public abstract class Acquisition_DB extends RoomDatabase {

    private static final String LOG_TAG = Acquisition_DB.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "DataAcquisition";
    private static Acquisition_DB sInstance;

    public static Acquisition_DB getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        Acquisition_DB.class, Acquisition_DB.DATABASE_NAME)
                        .build();
            }
        }
        Log.d(LOG_TAG, "Getting the database instance");
        return sInstance;
    }

    public abstract AcquisitionDao AcquisitionDao();
}
