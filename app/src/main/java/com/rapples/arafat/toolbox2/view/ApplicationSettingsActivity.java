package com.rapples.arafat.toolbox2.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import com.rapples.arafat.toolbox2.R;
import com.rapples.arafat.toolbox2.databinding.ActivityApplicationSettingsBinding;
import com.rapples.arafat.toolbox2.util.SharedPref;

public class ApplicationSettingsActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    ActivityApplicationSettingsBinding binding;
    private boolean barcodeComparisonFuntionStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_application_settings);


        init();

        checkSharedPref();

        setSwitchStatus();

        changebarcodeComparisonFuntionStatus();

    }

    private void setSwitchStatus() {
        binding.barcodeComparisonSwitch.setChecked(barcodeComparisonFuntionStatus);
    }

    private void changebarcodeComparisonFuntionStatus() {

        binding.barcodeComparisonSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean(SharedPref.BARCODE_COMPARISON_FUNTION,isChecked);
                editor.apply();
            }
        });
    }

    private void init() {
        sharedPreferences = getSharedPreferences(SharedPref.SETTING_PREFERENCE,MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    private void checkSharedPref() {

        barcodeComparisonFuntionStatus = sharedPreferences.getBoolean(SharedPref.BARCODE_COMPARISON_FUNTION,false);

    }

    public void onBack(View view) {
        onBackPressed();
    }
}