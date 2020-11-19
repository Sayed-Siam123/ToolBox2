package com.rapples.arafat.toolbox2.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;

import com.rapples.arafat.toolbox2.R;
import com.rapples.arafat.toolbox2.databinding.ActivityBarcodeSettingsBinding;
import com.rapples.arafat.toolbox2.util.SharedPref;

public class BarcodeSettingsActivity extends AppCompatActivity {

    private ActivityBarcodeSettingsBinding binding;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private boolean codeTwoByFive;
    private boolean code39;
    private boolean code128;
    private boolean ean13;
    private boolean dataMatrix;
    private boolean qrCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_barcode_settings);

        init();

        checkSharedPref();

        setSwitchStatus();

        changeFunctionStatus();

        setSoundDropDown();


    }

    private void setSwitchStatus() {
        binding.codeTwoByFiveSwitch.setChecked(codeTwoByFive);
        binding.code39Switch.setChecked(code39);
        binding.code128Switch.setChecked(code128);
        binding.ean13Switch.setChecked(ean13);
        binding.dataMatrixSwitch.setChecked(dataMatrix);
        binding.qrCodeSwitch.setChecked(qrCode);


    }

    private void changeFunctionStatus() {

        binding.codeTwoByFiveSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean(SharedPref.CODE_TWO_BY_FIVE, isChecked);
                editor.apply();
            }
        });

        binding.code39Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean(SharedPref.CODE_39, isChecked);
                editor.apply();
            }
        });

        binding.code128Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean(SharedPref.CODE_128, isChecked);
                editor.apply();
            }
        });

        binding.ean13Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean(SharedPref.EAN_13, isChecked);
                editor.apply();
            }
        });

        binding.dataMatrixSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean(SharedPref.DATA_MATRIX, isChecked);
                editor.apply();
            }
        });

        binding.qrCodeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean(SharedPref.QR_CODE, isChecked);
                editor.apply();
            }
        });


    }

    private void init() {
        sharedPreferences = getSharedPreferences(SharedPref.SETTING_PREFERENCE, MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    private void checkSharedPref() {

        codeTwoByFive = sharedPreferences.getBoolean(SharedPref.CODE_TWO_BY_FIVE, false);
        code39 = sharedPreferences.getBoolean(SharedPref.CODE_39, false);
        code128 = sharedPreferences.getBoolean(SharedPref.CODE_128, false);
        ean13 = sharedPreferences.getBoolean(SharedPref.EAN_13, false);
        dataMatrix = sharedPreferences.getBoolean(SharedPref.DATA_MATRIX, false);
        qrCode = sharedPreferences.getBoolean(SharedPref.QR_CODE, false);

    }

    private void setSoundDropDown() {
        String[] items = new String[]{"Tone 1", "Tone 2", "Tone 3"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        binding.soundDropdown.setAdapter(adapter);
    }

    public void onBackbarcode(View view) {
        onBackPressed();
    }
}