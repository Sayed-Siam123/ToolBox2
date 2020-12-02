package com.rapples.arafat.toolbox2.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.rapples.arafat.toolbox2.R;
import com.rapples.arafat.toolbox2.databinding.ActivityApplicationSettingsBinding;
import com.rapples.arafat.toolbox2.util.SharedPref;

public class ApplicationSettingsActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ActivityApplicationSettingsBinding binding;
    private boolean barcodeComparisonFuntionStatus;
    private boolean barcodeInformation;
    private boolean showDescription;
    private boolean managePrices;
    private boolean showPrices;
    private boolean showPictures;
    private boolean showBarcodeType;
    private boolean showNumberOfDigits;
    private boolean dataAcquisitionFunctionStatus;
    private boolean quantityFields;
    private boolean allowDuplicateCodes;
    private boolean barcodeLabelFunction;
    private String printBarcodeType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_application_settings);


        init();

        checkSharedPref();

        setSwitchStatus();

        setFileFormatDropDown();

        setSeparatorDropDown();

        setBarcodeTypeDropDown();

        changeFunctionStatus();


    }

    private void setSwitchStatus() {
        binding.barcodeComparisonSwitch.setChecked(barcodeComparisonFuntionStatus);
        binding.barcodeInformationSwitch.setChecked(barcodeInformation);
        binding.showDescriptionSwitch.setChecked(showDescription);
        binding.showPricesSwitch.setChecked(showPrices);
        binding.showPicturesSwitch.setChecked(showPictures);
        binding.showBarcodeTypeSwitch.setChecked(showBarcodeType);
        binding.showNumberofDigitsSwitch.setChecked(showNumberOfDigits);
        binding.managePricesSwitch.setChecked(managePrices);
        binding.dataAcquisitionSwitch.setChecked(dataAcquisitionFunctionStatus);
        binding.quantityFieldSwitch.setChecked(quantityFields);
        binding.allowDuplicateCodesSwitch.setChecked(allowDuplicateCodes);
        binding.barCodeLabelPrintingSwitch.setChecked(barcodeLabelFunction);

    }

    private void changeFunctionStatus() {

        binding.barcodeComparisonSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean(SharedPref.BARCODE_COMPARISON_FUNTION, isChecked);
                editor.apply();
            }
        });

        binding.barcodeInformationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean(SharedPref.BARCODE_INFORMATION, isChecked);
                editor.apply();
            }
        });

        binding.showDescriptionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean(SharedPref.SHOW_DESCRIPTION, isChecked);
                editor.apply();
            }
        });

        binding.showPricesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean(SharedPref.SHOW_PRICES, isChecked);
                editor.apply();
            }
        });

        binding.showPicturesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean(SharedPref.SHOW_PICTURES, isChecked);
                editor.apply();
            }
        });

        binding.showBarcodeTypeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean(SharedPref.SHOW_BARCODETYPE, isChecked);
                editor.apply();
            }
        });

        binding.showNumberofDigitsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean(SharedPref.SHOW_NUMBER_OF_DIGITS, isChecked);
                editor.apply();
            }
        });

        binding.managePricesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean(SharedPref.MANAGE_PRICES, isChecked);
                editor.apply();
            }
        });

        binding.dataAcquisitionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean(SharedPref.DATA_ACQUISITION_FUNCTION, isChecked);
                editor.apply();
            }
        });

        binding.quantityFieldSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean(SharedPref.QUANTITY_FIELD, isChecked);
                editor.apply();
            }
        });

        binding.allowDuplicateCodesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean(SharedPref.ALLOW_DUPLICATE_CODES, isChecked);
                editor.apply();
            }
        });

        binding.barCodeLabelPrintingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean(SharedPref.BARCODE_LABEL_FUNCTION, isChecked);
                editor.apply();
            }
        });


    }

    private void init() {
        sharedPreferences = getSharedPreferences(SharedPref.SETTING_PREFERENCE, MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    private void checkSharedPref() {

        barcodeComparisonFuntionStatus = sharedPreferences.getBoolean(SharedPref.BARCODE_COMPARISON_FUNTION, false);
        barcodeInformation = sharedPreferences.getBoolean(SharedPref.BARCODE_INFORMATION, false);
        showDescription = sharedPreferences.getBoolean(SharedPref.SHOW_DESCRIPTION, false);
        showPrices = sharedPreferences.getBoolean(SharedPref.SHOW_PRICES, false);
        showPictures = sharedPreferences.getBoolean(SharedPref.SHOW_PICTURES, false);
        showBarcodeType = sharedPreferences.getBoolean(SharedPref.SHOW_BARCODETYPE, false);
        showNumberOfDigits = sharedPreferences.getBoolean(SharedPref.SHOW_NUMBER_OF_DIGITS, false);
        managePrices = sharedPreferences.getBoolean(SharedPref.MANAGE_PRICES, false);
        dataAcquisitionFunctionStatus = sharedPreferences.getBoolean(SharedPref.DATA_ACQUISITION_FUNCTION, false);
        quantityFields = sharedPreferences.getBoolean(SharedPref.QUANTITY_FIELD, false);
        allowDuplicateCodes = sharedPreferences.getBoolean(SharedPref.ALLOW_DUPLICATE_CODES, false);
        barcodeLabelFunction = sharedPreferences.getBoolean(SharedPref.BARCODE_LABEL_FUNCTION, false);


    }

    public void onBack(View view) {
        onBackPressed();
    }

    private void setSeparatorDropDown() {
        String[] separatoritems = new String[]{";", ".", ":"};
        ArrayAdapter<String> sep_adapter = new ArrayAdapter<>(ApplicationSettingsActivity.this, android.R.layout.simple_spinner_dropdown_item, separatoritems);
        binding.separatorDropdown.setAdapter(sep_adapter);
    }

    private void setFileFormatDropDown() {
        String[] fileformatitems = new String[]{"Text", "Excel", "CSV"};
        ArrayAdapter<String> file_adapter = new ArrayAdapter<>(ApplicationSettingsActivity.this, android.R.layout.simple_spinner_dropdown_item, fileformatitems);
        binding.fileFormateDropdown.setAdapter(file_adapter);
    }

    private void setBarcodeTypeDropDown() {
        String[] fileformatitems = new String[]{"Code 128", "2D Datamatrix",};
        ArrayAdapter<String> file_adapter = new ArrayAdapter<>(ApplicationSettingsActivity.this, android.R.layout.simple_spinner_dropdown_item, fileformatitems);
        binding.barcodeTypeDropdown.setAdapter(file_adapter);
    }


    @Override
    public void onBackPressed() {

        editor.putString(SharedPref.PRINT_BARCODE_TYPE,binding.barcodeTypeDropdown.getSelectedItem().toString());
        editor.apply();

        super.onBackPressed();
    }
}