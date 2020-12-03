package com.rapples.arafat.toolbox2.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.rapples.arafat.toolbox2.OnFieldChangeListener;
import com.rapples.arafat.toolbox2.R;
import com.rapples.arafat.toolbox2.databinding.ActivityCustomFunctionSettingsBinding;
import com.rapples.arafat.toolbox2.model.Field;
import com.rapples.arafat.toolbox2.util.SharedPref;
import com.rapples.arafat.toolbox2.view.adapter.CustomFieldAdapter;

import java.util.ArrayList;
import java.util.List;

public class CustomFunctionSettingsActivity extends AppCompatActivity implements OnFieldChangeListener {

    private ActivityCustomFunctionSettingsBinding binding;
    private CustomFieldAdapter adapter;
    private List<Field> fieldList;
    private int fieldSize = 1;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private boolean functionIsActive;
    private String name;
    private String description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_custom_function_settings);

        init();

        configRecyclearview();

        changeCustomFuntionStatus();

        setFileFormatDropDown();

        setSharedPreferenceData();

    }

    private void setSharedPreferenceData() {
        functionIsActive = sharedPreferences.getBoolean(SharedPref.CUSTOM_FUNCTION, false);
        name = sharedPreferences.getString(SharedPref.CUSTOM_FUNCTION_NAME, "");
        description = sharedPreferences.getString(SharedPref.CUSTOM_FUNCTION_DESCRIPTION, "");

        binding.nameEt.setText(name);
        binding.descriptionEt.setText(description);
        binding.customFuntionSwitch.setChecked(functionIsActive);
    }

    private void configRecyclearview() {

        if(fieldList.size() == 0){
            fieldList.add(new Field("", ""));
        }
        adapter = new CustomFieldAdapter(fieldList, this, this);
        binding.fieldRecyclearview.setAdapter(adapter);


    }

    private void init() {
        sharedPreferences = getSharedPreferences(SharedPref.CUSTOM_FUNCTION_PREFERENCE, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        fieldList = new ArrayList<>();
        binding.fieldRecyclearview.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setFileFormatDropDown() {
        String[] items = new String[]{"jpg", "pdf", "csv", "docx", "txt"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        binding.fileFormateDropdown.setAdapter(adapter);
    }


    public void addField(View view) {

        if (fieldList.size() < 5) {
            fieldList.add(new Field("", ""));
            configRecyclearview();
        }
    }

    public void saveData(View view) {
        checkField();

    }

    private void checkField() {
        if (binding.nameEt.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter function name", Toast.LENGTH_SHORT).show();
        } else if (binding.descriptionEt.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter function description", Toast.LENGTH_SHORT).show();
        } else {
            savaDataIntoSharedPreference();
        }
    }

    private void savaDataIntoSharedPreference() {
        Gson gson = new Gson();
        String itemList = gson.toJson(fieldList);

        editor.putString(SharedPref.CUSTOM_FIELD_LIST, itemList);
        editor.putString(SharedPref.CUSTOM_FUNCTION_NAME, binding.nameEt.getText().toString());
        editor.putString(SharedPref.CUSTOM_FUNCTION_DESCRIPTION, binding.descriptionEt.getText().toString());



        editor.apply();
        binding.nameEt.setText("");
        binding.descriptionEt.setText("");
    }

    private void changeCustomFuntionStatus() {

        binding.customFuntionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean(SharedPref.CUSTOM_FUNCTION, isChecked);
                editor.apply();
            }
        });
    }

    public void back(View view) {
        onBackPressed();
    }

    @Override
    public void updateFieldList(List<Field> updateList) {
        if(updateList.size() <6) {
            fieldList.clear();
            fieldList.addAll(updateList);
        }
    }
}