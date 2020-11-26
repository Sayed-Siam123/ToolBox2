package com.rapples.arafat.toolbox2.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.rapples.arafat.toolbox2.Database.Acquisition_DB;
import com.rapples.arafat.toolbox2.Database.MasterExecutor;
import com.rapples.arafat.toolbox2.R;
import com.rapples.arafat.toolbox2.databinding.ActivityDataAcquisitionDetailsBinding;
import com.rapples.arafat.toolbox2.model.Product;
import com.rapples.arafat.toolbox2.util.SharedPref;
import com.rapples.arafat.toolbox2.view.adapter.CustomDataAcquisitionAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataAcquisitionDetailsActivity extends AppCompatActivity {

    private ActivityDataAcquisitionDetailsBinding binding;
    private String fileName;
    private String date;
    private List<Product> productList;
    private CustomDataAcquisitionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_data_acquisition_details);

        init();

        getFileName();

        getProductList();


    }

    private void init() {
        productList = new ArrayList<>();
    }

    private void getProductList() {

        MasterExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                productList = Acquisition_DB.getInstance(DataAcquisitionDetailsActivity.this).ProductDao().loadAllproduct(fileName);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.valueCountTv.setText(productList.size() + " Values");
                        for(int i = 0;i<productList.size();i++){
                        }
                        if(productList.size() > 0){
                            Collections.reverse(productList);
                        }
                        binding.dataAcquisitionDetailsRecyclerView.setLayoutManager(new LinearLayoutManager(DataAcquisitionDetailsActivity.this));
                        adapter = new CustomDataAcquisitionAdapter(productList, DataAcquisitionDetailsActivity.this);
                        binding.dataAcquisitionDetailsRecyclerView.setAdapter(adapter);
                    }
                });
            }
        });

    }

    private void getFileName() {
        fileName = getIntent().getStringExtra(SharedPref.FILE_NAME);
        date = getIntent().getStringExtra(SharedPref.DATE);

        binding.fileNameTv.setText(fileName);
        binding.dateTv.setText(date);

    }

    public void onBackDataAcquisitionDetails(View view) {
        onBackPressed();
    }

    public void onSettingsFromDataAcquisitionDetails(View view) {
        startActivity(new Intent(DataAcquisitionDetailsActivity.this, ApplicationSettingsActivity.class));
    }
}