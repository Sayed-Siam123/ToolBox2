package com.rapples.arafat.toolbox2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.rapples.arafat.toolbox2.databinding.ActivityHome2Binding;
import com.rapples.arafat.toolbox2.view.MainActivity;


public class HomeActivity extends AppCompatActivity {

    ActivityHome2Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_home2);




    }

    public void rfiReader(View view) {
        startActivity(new Intent(HomeActivity.this,ReaderManagerActivity.class));
    }

    public void barCode(View view) {
        startActivity(new Intent(HomeActivity.this, MainActivity.class));
    }
}