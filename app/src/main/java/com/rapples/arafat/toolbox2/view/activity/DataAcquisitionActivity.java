package com.rapples.arafat.toolbox2.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.rapples.arafat.toolbox2.R;

public class DataAcquisitionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_acquisition);
    }

    public void onBackDataAcquisition(View view) {
        onBackPressed();
    }

    public void onSettingsFromBarcodeAcquisition(View view) {
        startActivity(new Intent(DataAcquisitionActivity.this,ApplicationSettingsActivity.class));
    }

    public void addDList(View view) {
        startActivity(new Intent(DataAcquisitionActivity.this,DataAcqusitionFileNameActivity.class));
    }
}