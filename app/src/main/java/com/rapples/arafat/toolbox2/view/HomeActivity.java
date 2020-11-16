package com.rapples.arafat.toolbox2.view;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;
import com.rapples.arafat.toolbox2.R;
import com.rapples.arafat.toolbox2.databinding.ActivityHome2Binding;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private ActivityHome2Binding binding;

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Toolbar toolbar;

    public boolean status = false;
    ImageView back_button,menu_button;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_home2);
        back_button = (ImageView) findViewById(R.id.back_to_menu_icon);
        menu_button = (ImageView) findViewById(R.id.menu_image);
        DrawerLayout drawer = binding.drawerLayout;
//        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//        if(!drawer.isDrawerOpen(Gravity.START))
//            drawer.openDrawer(Gravity.START);
//        else drawer.closeDrawer(Gravity.END);
        NavigationView navigationView = binding.navView;
        navigationView.setNavigationItemSelectedListener(this);

    }

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
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = binding.drawerLayout;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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