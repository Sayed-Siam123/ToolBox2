package com.rapples.arafat.toolbox2.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.content.Context;

import com.rapples.arafat.toolbox2.R;
import com.rapples.arafat.toolbox2.databinding.ActivitySplashBinding;
import java.io.File;
import java.io.*;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this,R.layout.activity_splash);
        setContentView(R.layout.activity_splash);

        createDirectory();

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this,HomeActivity.class));
            }
        },3000);

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