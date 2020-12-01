package com.rapples.arafat.toolbox2.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rapples.arafat.toolbox2.R;
import com.rapples.arafat.toolbox2.model.Field;
import com.rapples.arafat.toolbox2.util.SharedPref;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AddCustomFunctionDataAcquisition extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private List<Field> fieldList;
    private String fieldListInString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_custom_function_data_acquisition);

        init();

        getSharedPreferencesData();
    }

    private void getSharedPreferencesData() {

        fieldListInString = sharedPreferences.getString(SharedPref.CUSTOM_FIELD_LIST,"");

        if(!fieldListInString.isEmpty()){
            Gson gson = new Gson();
            Type type = new TypeToken<List<Field>>(){}.getType();
            fieldList = gson.fromJson(fieldListInString, type);
        }

        for(int i = 0; i<fieldList.size();i++){
            Log.d("sajib", "getSharedPreferencesData: "+fieldList.get(i).getFieldName());
            Log.d("sajib", "getSharedPreferencesData: "+fieldList.get(i).getFieldType());

            Log.d("sajib", "________________________________");



        }



    }

    private void init() {
        sharedPreferences =getSharedPreferences(SharedPref.SETTING_PREFERENCE,MODE_PRIVATE);
        editor = sharedPreferences.edit();
        fieldList = new ArrayList<>();
    }
}