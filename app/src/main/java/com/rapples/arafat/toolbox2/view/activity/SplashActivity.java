package com.rapples.arafat.toolbox2.view.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.content.Context;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;

import com.rapples.arafat.toolbox2.R;
import com.rapples.arafat.toolbox2.databinding.ActivitySplashBinding;
import com.rapples.arafat.toolbox2.util.SharedPref;

import java.io.File;
import java.io.*;
import java.util.Locale;

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

        //openLicenceDialog();

        checkLicence();

        createDir();

        setDefaultValue();

        setDefaultLanguageValue();


    }

    private void openLicenceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Licence Key");
        final View customLayout = getLayoutInflater().inflate(R.layout.licence_key_dialog, null);
        final EditText licenceEdT = customLayout.findViewById(R.id.licenceET);
        builder.setView(customLayout);
        builder.setCancelable(false);
        builder.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Log.d("TAG", "onClick: OK! "+licenceEdT.getText().toString());
                SharedPreferences sp = getSharedPreferences(SharedPref.LICENCE,MODE_PRIVATE);
                SharedPreferences.Editor preferencesEditor = sp.edit();
                preferencesEditor.putString(SharedPref.SET_LICENCE,licenceEdT.getText().toString());
                preferencesEditor.apply();
                Log.d("TAG", "onClick: OK! "+licenceEdT.getText().toString());
                Log.d("TAG", "onClick: OK! saved");
                dialog.dismiss();
                handleScreen();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void checkLicence(){
        sharedPreferences = getSharedPreferences(SharedPref.LICENCE, Activity.MODE_PRIVATE);
        String licence = sharedPreferences.getString(SharedPref.SET_LICENCE,"null");
        Log.d("TAG", "checkLicence: "+licence);
        if(licence.equals("null")){
            Log.d("TAG", "checkLicence: licence  is null");
            openLicenceDialog();
        }

        else{
            Log.d("TAG", "checkLicence: licence  is not null "+ licence);
            handleScreen();
        }
    }

    private void handleScreen(){
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

    private void setDefaultLanguageValue() {
        sharedPreferences = getSharedPreferences(SharedPref.LANGUAGE, Activity.MODE_PRIVATE);
        String language = sharedPreferences.getString(SharedPref.SET_LANGUAGE,"en");
        setLocal(language);
    }

    private void setLocal(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());
        editor = getSharedPreferences(SharedPref.LANGUAGE,MODE_PRIVATE).edit();
        editor.putString(SharedPref.SET_LANGUAGE,language);
        editor.apply();
    }





    private void init() {
        sharedPreferences = getSharedPreferences(SharedPref.SETTING_PREFERENCE,MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }



    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void createDir() {

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