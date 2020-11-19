package com.rapples.arafat.toolbox2.Database;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.rapples.arafat.toolbox2.model.Masterdata;

@Database(entities = {Masterdata.class}, version = 1, exportSchema = false)
public abstract class MasterData_DB extends RoomDatabase {

    private static final String LOG_TAG = MasterData_DB.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "Masterdata";
    private static MasterData_DB sInstance;

    public static MasterData_DB getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        MasterData_DB.class, MasterData_DB.DATABASE_NAME)
                        .build();
            }
        }
        Log.d(LOG_TAG, "Getting the database instance");
        return sInstance;
    }

    public abstract MasterdataDao MasterdataDao();
}
