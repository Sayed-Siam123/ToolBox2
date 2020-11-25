package com.rapples.arafat.toolbox2.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.content.Context;
import android.util.Log;

import com.rapples.arafat.toolbox2.R;
import com.rapples.arafat.toolbox2.databinding.ActivitySplashBinding;
import com.rapples.arafat.toolbox2.util.SharedPref;

import java.io.File;
import java.io.*;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this,R.layout.activity_splash);
        setContentView(R.layout.activity_splash);

        createDirectory();

        init();

        setDefaultValue();

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                finish();
            }
        },3000);

    }

    private void setDefaultValue() {
        editor.putBoolean(SharedPref.BARCODE_COMPARISON_FUNTION,true);
        editor.apply();
    }

    private void init() {
        sharedPreferences = getSharedPreferences(SharedPref.SETTING_PREFERENCE,MODE_PRIVATE);
        editor = sharedPreferences.edit();


    }

    private void createDirectory() {
        File mydir = this.getDir("toolbox_directory", Context.MODE_PRIVATE); //Creating an internal dir;

        //creating folder in external storage
        File f = new File(Environment.getExternalStorageDirectory(), "ToolBox");
        Log.e("External Dir", f.toString());
        if (!f.exists()) {
            Log.e("External Created", f.toString());
            f.mkdirs();

        }

        Log.e("File dir", mydir.toString());
        File fileWithinMyDir = new File(mydir, "test"); //Getting a file within the dir.
        try {
            FileOutputStream out = new FileOutputStream(fileWithinMyDir); //Use the stream as usual to write into the file.
            Log.e("Dir create", out.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("Dir faild", e.toString());
        }

    }


}