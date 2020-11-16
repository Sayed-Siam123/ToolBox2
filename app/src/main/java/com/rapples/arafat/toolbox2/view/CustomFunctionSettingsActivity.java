package com.rapples.arafat.toolbox2.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.rapples.arafat.toolbox2.R;
import com.rapples.arafat.toolbox2.databinding.ActivityCustomFunctionSettingsBinding;
import com.rapples.arafat.toolbox2.model.Field;
import com.rapples.arafat.toolbox2.view.adapter.CustomFieldAdapter;

import java.util.ArrayList;
import java.util.List;

public class CustomFunctionSettingsActivity extends AppCompatActivity {

    private ActivityCustomFunctionSettingsBinding binding;
    private CustomFieldAdapter adapter;
    private List<Field> fieldList;
    private int fieldSize = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_custom_function_settings);

        init();

        configRecyclearview();


        setFileFormatDropDown();

    }

    private void configRecyclearview() {
        fieldList.clear();
        for(int i=0;i<fieldSize;i++){
            fieldList.add(new Field("",""));
        }
        adapter = new CustomFieldAdapter(fieldList,this);
        binding.fieldRecyclearview.setAdapter(adapter);



    }

    private void init() {
        fieldList = new ArrayList<>();
        binding.fieldRecyclearview.setLayoutManager(new LinearLayoutManager(this));

    }

    private void setFileFormatDropDown() {
        String[] items = new String[]{"jpg", "pdf", "csv","docx"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        binding.fileFormateDropdown.setAdapter(adapter);
    }


    public void addField(View view) {
        fieldSize +=1;
        configRecyclearview();
    }
}