package com.rapples.arafat.toolbox2.view.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.util.Objects;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private ActivityHome2Binding binding;

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private LinearLayout barcodeComparationLL,customFunctionLL;
    private TextView customFunctionName,customFunctiondescription;
    boolean doubleBackToExitPressedOnce = false;


    public boolean status = false;
    ImageView back_button,menu_button;

    @Override
    protected void onResume() {
        super.onResume();

        checkFunction();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_home2);


        init();


        checkFunction();


        back_button = (ImageView) findViewById(R.id.back_to_menu_icon);
        TextView tvBack = (TextView) findViewById(R.id.navigationBack);
        menu_button = (ImageView) findViewById(R.id.menu_image);
        DrawerLayout drawer = binding.drawerLayout;
//        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//        if(!drawer.isDrawerOpen(Gravity.START))
//            drawer.openDrawer(Gravity.START);
//        else drawer.closeDrawer(Gravity.END);
        NavigationView navigationView = binding.navView;
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void checkFunction() {
        boolean comparisonFuntion = sharedPreferences.getBoolean(SharedPref.BARCODE_COMPARISON_FUNTION,false);
        boolean customFuntion = sharedPreferences.getBoolean(SharedPref.CUSTOM_FUNCTION,false);

        if(comparisonFuntion){
            barcodeComparationLL.setVisibility(View.VISIBLE);
        }else{
            barcodeComparationLL.setVisibility(View.GONE);
        }

        if(customFuntion){
            customFunctionLL.setVisibility(View.VISIBLE);
            customFunctionName.setText(sharedPreferences.getString(SharedPref.CUSTOM_FUNCTION_NAME,""));
            customFunctiondescription.setText(sharedPreferences.getString(SharedPref.CUSTOM_FUNCTION_DESCRIPTION,""));
        }else{
            customFunctionLL.setVisibility(View.GONE);
        }

    }

    private void init() {
        sharedPreferences = getSharedPreferences(SharedPref.SETTING_PREFERENCE,MODE_PRIVATE);
        editor = sharedPreferences.edit();
        barcodeComparationLL = findViewById(R.id.barCodeComparisonButtonLL);
        customFunctionLL = findViewById(R.id.customFunctionLL);
        customFunctionName = findViewById(R.id.customFunctionNameTv);
        customFunctiondescription = findViewById(R.id.customFunctionDescriptionTv);


    }

    @SuppressLint("LongLogTag")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Log.d("item id", "onNavigationItemSelected: "+id);
        if (id == R.id.item_masterdata) {

            Log.d("item name", "onNavigationItemSelected: masterdata");


        } else if (id == R.id.item_settings) {
            NavigationView navigationView = binding.navView;
            navigationView.getMenu().clear(); //clear old inflated items.
            navigationView.inflateMenu(R.menu.menu_item_settings);
            navigationView.setNavigationItemSelectedListener(this);
            status = true;
        }


        else if (id == R.id.item_file_upload) {
            Log.d("File upload", "onNavigationItemSelected: File upload");
        }

        else if (id == R.id.item_language) {
            Log.d("item_language", "onNavigationItemSelected: Item Language");
        }

        else if (id == R.id.item_about) {
            Log.d("item_about", "onNavigationItemSelected: Item About");
        }

        else if (id == R.id.item_barcode_settings) {
            Log.d("item_barcode_settings", "onNavigationItemSelected: Item Barcode Settings");
        }

        else if (id == R.id.item_application_settings) {
            Log.d("item_application_settings", "onNavigationItemSelected: item_application_settings");
            startActivity(new Intent(HomeActivity.this,ApplicationSettingsActivity.class));
        }

        else if (id == R.id.item_custom_settings) {
            Log.d("item_custom_settings", "onNavigationItemSelected: item_custom_settings");
            startActivity(new Intent(HomeActivity.this,CustomFunctionSettingsActivity.class));
        }




        return true;
    }


    //Home page menu item

    public void barcode_compare(View v) {
        Log.d("Barcode  Cmp", "onNavigationItemSelected: Barcode compare");
        startActivity(new Intent(HomeActivity.this,BarcodeComparisonActivity.class));

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
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }
    }

    public void backButton(View v) {
        if(!status) {
            onBackPressed();
        }

        else{
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

}