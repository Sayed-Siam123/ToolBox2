package com.rapples.arafat.toolbox2.view.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.content.Context;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.rapples.arafat.toolbox2.R;
import com.rapples.arafat.toolbox2.databinding.ActivitySplashBinding;
import com.rapples.arafat.toolbox2.util.SharedPref;

import java.io.File;
import java.io.*;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this,R.layout.activity_splash);
        setContentView(R.layout.activity_splash);

        init();

        createDir();

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

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void createDir() {
        Log.d("TAG", "createDir: OK!");
        File folder = new File(Environment.getRootDirectory().getParent() +
                File.separator + "TollCulator");
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
        if (success) {
            // Do something on success
            Log.d("TAG", "createDir: ok, created");
        } else {
            // Do something else on failure
            Log.d("TAG", "createDir: not ok, not created");
        }


    }


}