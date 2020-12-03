package com.rapples.arafat.toolbox2.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.rapples.arafat.toolbox2.CustomFunctionFileNameActivity;
import com.rapples.arafat.toolbox2.R;
import com.rapples.arafat.toolbox2.databinding.ActivityCustomFunctionBinding;
import com.rapples.arafat.toolbox2.util.SharedPref;

public class CustomFunctionActivity extends AppCompatActivity {

    private ActivityCustomFunctionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_custom_function);

        getIntentData();

    }

    private void getIntentData() {
        binding.titleTv.setText(getIntent().getStringExtra(SharedPref.CUSTOM_FUNCTION_NAME));
    }

    public void addCustomFunctionFile(View view) {
        startActivity(new Intent(CustomFunctionActivity.this, CustomFunctionFileNameActivity.class)
                .putExtra(SharedPref.CUSTOM_FUNCTION_NAME, getIntent().getStringExtra(SharedPref.CUSTOM_FUNCTION_NAME)));
    }

    public void onBackCustomFunction(View view) {
        onBackPressed();
    }

    public void onSettingsFromCustomFunction(View view) {
        startActivity(new Intent(CustomFunctionActivity.this, CustomFunctionSettingsActivity.class));
    }
}