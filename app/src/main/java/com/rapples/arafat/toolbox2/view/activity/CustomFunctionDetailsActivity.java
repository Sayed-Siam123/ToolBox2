package com.rapples.arafat.toolbox2.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.rapples.arafat.toolbox2.Database.Acquisition_DB;
import com.rapples.arafat.toolbox2.Database.CustomFunction_DB;
import com.rapples.arafat.toolbox2.Database.MasterExecutor;
import com.rapples.arafat.toolbox2.R;
import com.rapples.arafat.toolbox2.databinding.ActivityCustomFunctionDetailsBinding;
import com.rapples.arafat.toolbox2.model.CustomFunctionProduct;
import com.rapples.arafat.toolbox2.model.Product;
import com.rapples.arafat.toolbox2.util.SharedPref;
import com.rapples.arafat.toolbox2.view.adapter.CustomDataAcquisitionAdapter;
import com.rapples.arafat.toolbox2.view.adapter.CustomDataAcquisitionEditAdapter;
import com.rapples.arafat.toolbox2.view.adapter.CustomFunctionProductAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomFunctionDetailsActivity extends AppCompatActivity {

    private ActivityCustomFunctionDetailsBinding binding;
    private String fileName;
    private String date;
    private int id;
    private String editable;
    private List<CustomFunctionProduct> customFunctionProductList;
    private CustomFunctionProductAdapter adapter;
    private CustomDataAcquisitionEditAdapter editAdapter;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_custom_function_details);

        init();

        getIntentData();

        getProductList();
    }

    private void getIntentData() {
        fileName = getIntent().getStringExtra(SharedPref.FILE_NAME);
        date = getIntent().getStringExtra(SharedPref.DATE);
        id = Integer.parseInt(getIntent().getStringExtra(SharedPref.ID));
        editable = getIntent().getStringExtra(SharedPref.EDITABLE);


        binding.fileNameTv.setText(fileName);
        binding.dateTv.setText(date);

    }

    private void init() {
        customFunctionProductList = new ArrayList<>();
        sharedPreferences = getSharedPreferences(SharedPref.SETTING_PREFERENCE,MODE_PRIVATE);
        binding.toolBarTitleTv.setText(sharedPreferences.getString(SharedPref.CUSTOM_FUNCTION_NAME,""));

    }

    private void getProductList() {

        MasterExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                customFunctionProductList = CustomFunction_DB.getInstance(CustomFunctionDetailsActivity.this).CustomFunctionProductDao().loadAllproduct(fileName);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.valueCountTv.setText(customFunctionProductList.size() + " Values");
                        for (int i = 0; i < customFunctionProductList.size(); i++) {
                        }
                        if (customFunctionProductList.size() > 0) {
                            Collections.reverse(customFunctionProductList);
                        }
                        binding.customFunctionalityRecyclerview.setLayoutManager(new LinearLayoutManager(CustomFunctionDetailsActivity.this));

                        adapter = new CustomFunctionProductAdapter(customFunctionProductList, CustomFunctionDetailsActivity.this);
                        binding.customFunctionalityRecyclerview.setAdapter(adapter);


//                        if (editable.equals("true")) {
////                            editAdapter = new CustomDataAcquisitionEditAdapter(customFunctionProductList, CustomFunctionDetailsActivity.this,fileName);
////                            binding.dataAcquisitionDetailsRecyclerView.setAdapter(editAdapter);
//
//                        } else {
//                            adapter = new CustomFunctionProductAdapter(customFunctionProductList, CustomFunctionDetailsActivity.this);
//                            binding.dataAcquisitionDetailsRecyclerView.setAdapter(adapter);
//
//                        }


//                        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
//                        itemTouchHelper.attachToRecyclerView(binding.dataAcquisitionDetailsRecyclerView);


                    }
                });
            }
        });

    }

    public void onBackCustomFunctionDetails(View view) {
        onBackPressed();
    }

    public void onSettingsFromCustomFunctionDetails(View view) {
    }

    public void deleteFile(View view) {
    }

    public void addNewElement(View view) {
    }
}