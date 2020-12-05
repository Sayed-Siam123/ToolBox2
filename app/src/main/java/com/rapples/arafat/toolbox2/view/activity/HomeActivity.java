package com.rapples.arafat.toolbox2.view.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;
import com.rapples.arafat.toolbox2.R;
import com.rapples.arafat.toolbox2.databinding.ActivityHome2Binding;
import com.rapples.arafat.toolbox2.util.SharedPref;

import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityHome2Binding binding;
    private static final String ACTION_BARCODE_DATA = "com.honeywell.sample.action.BARCODE_DATA";
    private static final String ACTION_CLAIM_SCANNER = "com.honeywell.aidc.action.ACTION_CLAIM_SCANNER";
    private static final String ACTION_RELEASE_SCANNER = "com.honeywell.aidc.action.ACTION_RELEASE_SCANNER";
    private static final String EXTRA_SCANNER = "com.honeywell.aidc.extra.EXTRA_SCANNER";
    private static final String EXTRA_PROFILE = "com.honeywell.aidc.extra.EXTRA_PROFILE";
    private static final String EXTRA_PROPERTIES = "com.honeywell.aidc.extra.EXTRA_PROPERTIES";

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private LinearLayout barcodeComparationLL, customFunctionLL, dataAquistionLL;
    private TextView customFunctionName, customFunctiondescription;
    boolean doubleBackToExitPressedOnce = false;
    boolean dataAcquisitionFunction;
    boolean barCodeLabelPrintingStatus;


    int checked = -1;

    public boolean status = false;
    ImageView back_button, menu_button;

    @Override
    protected void onResume() {
        super.onResume();
        checkFunction();
        claimScanner();
        releaseScanner();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(HomeActivity.this,R.layout.activity_home2);
        init();
        checkFunction();
        setDefaultLanguageValue();
        back_button = (ImageView) findViewById(R.id.back_to_menu_icon);
        menu_button = (ImageView) findViewById(R.id.menu_image);
        drawer = binding.drawerLayout;
//        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//        if(!drawer.isDrawerOpen(Gravity.START))
//            drawer.openDrawer(Gravity.START);
//        else drawer.closeDrawer(Gravity.END);
        NavigationView navigationView = binding.navView;
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void checkFunction() {
        boolean comparisonFuntion = sharedPreferences.getBoolean(SharedPref.BARCODE_COMPARISON_FUNTION, false);
        boolean customFuntion = sharedPreferences.getBoolean(SharedPref.CUSTOM_FUNCTION, false);
        dataAcquisitionFunction = sharedPreferences.getBoolean(SharedPref.DATA_ACQUISITION_FUNCTION,false);
        barCodeLabelPrintingStatus = sharedPreferences.getBoolean(SharedPref.BARCODE_LABEL_FUNCTION,false);


        if (comparisonFuntion) {
            barcodeComparationLL.setVisibility(View.VISIBLE);
        } else {
            barcodeComparationLL.setVisibility(View.GONE);
        }

        if (customFuntion) {
            customFunctionLL.setVisibility(View.VISIBLE);
            customFunctionName.setText(sharedPreferences.getString(SharedPref.CUSTOM_FUNCTION_NAME, ""));
            customFunctiondescription.setText(sharedPreferences.getString(SharedPref.CUSTOM_FUNCTION_DESCRIPTION, ""));
        } else {
            customFunctionLL.setVisibility(View.GONE);
        }

        customFunctionLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,CustomFunctionActivity.class)
                        .putExtra(SharedPref.CUSTOM_FUNCTION_NAME,sharedPreferences.getString(SharedPref.CUSTOM_FUNCTION_NAME, "")));
            }
        });

//        if(dataAcquisitionFunction){
//            dataAquistionLL.setEnabled(true);
//        }else{
//            dataAquistionLL.setEnabled(false);
//        }

    }

    private void init() {
        sharedPreferences = getSharedPreferences(SharedPref.SETTING_PREFERENCE, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        barcodeComparationLL = findViewById(R.id.barCodeComparisonButtonLL);
        customFunctionLL = findViewById(R.id.customFunctionLL);
        customFunctionName = findViewById(R.id.customFunctionNameTv);
        customFunctiondescription = findViewById(R.id.customFunctionDescriptionTv);
        dataAquistionLL = findViewById(R.id.dataAcquisitionLL);


    }

    @SuppressLint("LongLogTag")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Log.d("item id", "onNavigationItemSelected: " + id);
        if (id == R.id.item_masterdata) {

            Log.d("item name", "onNavigationItemSelected: masterdata");
            startActivity(new Intent(HomeActivity.this, MasterDataActivity.class));



        } else if (id == R.id.item_settings) {
            NavigationView navigationView = binding.navView;
            navigationView.getMenu().clear(); //clear old inflated items.
            navigationView.inflateMenu(R.menu.menu_item_settings);
            navigationView.setNavigationItemSelectedListener(this);
            status = true;
        } else if (id == R.id.item_file_upload) {
            Log.d("File upload", "onNavigationItemSelected: File upload");
        } else if (id == R.id.item_language) {
            Log.d("item_language", "onNavigationItemSelected: Item Language");
            openLanguageDialog();
        } else if (id == R.id.item_about) {
            startActivity(new Intent(HomeActivity.this, AboutActivity.class));
        } else if (id == R.id.item_barcode_settings) {
            startActivity(new Intent(HomeActivity.this, BarcodeSettingsActivity.class));
        } else if (id == R.id.item_application_settings) {
            startActivity(new Intent(HomeActivity.this, ApplicationSettingsActivity.class));
        } else if (id == R.id.item_custom_settings) {
            startActivity(new Intent(HomeActivity.this, CustomFunctionSettingsActivity.class));
        }


        return true;
    }

    private void openLanguageDialog() {
        final String[] language = {"English","German"};
        final AlertDialog.Builder language_dialog_builder = new AlertDialog.Builder(this);
        language_dialog_builder.setTitle("Choose Language");
        language_dialog_builder.setSingleChoiceItems(language, checked, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    Log.d("TAG", "onClick: ENNGLISH");
                    setLocal("en");
                    checked =  0;
                    recreate();
                }
                else {
                    Log.d("TAG", "onClick: German");
                    setLocal("de");
                    checked =  1;
                    recreate();
                }
                dialog.dismiss();
            }
        });
        AlertDialog language_dialog = language_dialog_builder.create();
        language_dialog.show();
    }


    private void setDefaultLanguageValue() {
        String language = sharedPreferences.getString(SharedPref.SET_LANGUAGE,"en");
        setLocal(language);
        Log.d("TAG", "setDefaultLanguageValue: "+language);
        if(language.equals("en")){
            checked = 0;
        }
        else{
            checked = 1;
        }
    }

    private void setLocal(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());
        editor.putString(SharedPref.SET_LANGUAGE,language);
        editor.apply();
    }


    //Home page menu item

    public void barcode_compare(View v) {
        Log.d("Barcode  Cmp", "onNavigationItemSelected: Barcode compare");
        startActivity(new Intent(HomeActivity.this, BarcodeComparisonActivity.class));

    }
    public void openDataAcquisition(View v) {
        Log.d("Barcode  Cmp", "onNavigationItemSelected: Barcode compare");

        if(dataAcquisitionFunction){
            startActivity(new Intent(HomeActivity.this, DataAcquisitionActivity.class));
        }



    }




    public void label_printing(View v) {
        if(barCodeLabelPrintingStatus) {
            startActivity(new Intent(HomeActivity.this, LabelPrintActivity.class));
        }

    }


    @Override
    public void onBackPressed() {

        DrawerLayout drawer = binding.drawerLayout;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    public void backButton(View v) {
        if (!status) {
            onBackPressed();
        } else {
            NavigationView navigationView = binding.navView;
            navigationView.getMenu().clear(); //clear old inflated items.
            navigationView.inflateMenu(R.menu.menu_item);
            navigationView.setNavigationItemSelectedListener(this);
            status = false;
        }
    }

    @SuppressLint("WrongConstant")
    public void menubutton(View v) {
        DrawerLayout drawer = binding.drawerLayout;
        drawer.openDrawer(Gravity.START);
    }

    private void releaseScanner() {
        sendBroadcast(new Intent(ACTION_RELEASE_SCANNER));
    }
    private void claimScanner() {
        Bundle properties = new Bundle();
        properties.putBoolean("DPR_DATA_INTENT", false);
        properties.putString("DPR_DATA_INTENT_ACTION", ACTION_BARCODE_DATA);
        sendBroadcast(new Intent(ACTION_CLAIM_SCANNER).setPackage("com.intermec.datacollectionservice")
                .putExtra(EXTRA_SCANNER, "dcs.scanner.imager").putExtra(EXTRA_PROFILE, "MyProfile1").putExtra(EXTRA_PROPERTIES, properties)
        );
    }

}