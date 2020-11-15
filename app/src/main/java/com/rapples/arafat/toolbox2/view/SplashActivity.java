package com.rapples.arafat.toolbox2.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.rapples.arafat.toolbox2.HomeActivity;
import com.rapples.arafat.toolbox2.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.*;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        createDirectory();

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, ApplicationSettingsActivity.class));
                finish();
            }
        },2000);

    }

    private void createDirectory() {
        File mydir = this.getDir("folder", Context.MODE_PRIVATE); //Creating an internal dir;
        File fileWithinMyDir = new File(mydir, "myfile"); //Getting a file within the dir.
        try {
            FileOutputStream out = new FileOutputStream(fileWithinMyDir); //Use the stream as usual to write into the file.
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }


}