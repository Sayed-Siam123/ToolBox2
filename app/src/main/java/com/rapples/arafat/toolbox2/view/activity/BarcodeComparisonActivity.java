package com.rapples.arafat.toolbox2.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.rapples.arafat.toolbox2.R;
import com.rapples.arafat.toolbox2.databinding.ActivityBarcodeComparisonBinding;
import com.rapples.arafat.toolbox2.util.SharedPref;

public class BarcodeComparisonActivity extends AppCompatActivity {
    private static final String ACTION_BARCODE_DATA = "com.honeywell.sample.action.BARCODE_DATA";
    private static final String ACTION_CLAIM_SCANNER = "com.honeywell.aidc.action.ACTION_CLAIM_SCANNER";
    private static final String ACTION_RELEASE_SCANNER = "com.honeywell.aidc.action.ACTION_RELEASE_SCANNER";
    private static final String EXTRA_SCANNER = "com.honeywell.aidc.extra.EXTRA_SCANNER";
    private static final String EXTRA_PROFILE = "com.honeywell.aidc.extra.EXTRA_PROFILE";
    private static final String EXTRA_PROPERTIES = "com.honeywell.aidc.extra.EXTRA_PROPERTIES";
    private ActivityBarcodeComparisonBinding binding;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String barcode;
    private boolean flag = false;


    private BroadcastReceiver barcodeDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_BARCODE_DATA.equals(intent.getAction())) {
                int version = intent.getIntExtra("version", 0);
                if (version >= 1) {
                    String aimId = intent.getStringExtra("aimId");
                    String charset = intent.getStringExtra("charset");
                    String codeId = intent.getStringExtra("codeId");
                    String data = intent.getStringExtra("data");
                    byte[] dataBytes = intent.getByteArrayExtra("dataBytes");
                    String timestamp = intent.getStringExtra("timestamp");

                    if(!flag){
                        editor.putString(SharedPref.BARCODE, data);
                        editor.apply();
                        binding.masterCodeDat.setText(data);
                        binding.digitCount.setText(data.length() +" Digits");
                        binding.barCodeFromSC.setText("");
                        binding.firstProductLL.setVisibility(View.VISIBLE);
                        if(codeId.equals("d")){
                            binding.codeType.setText("EAN13");
                        }else if(codeId.equals("s")){
                            binding.codeType.setText("QRCODE");
                        }else if(codeId.equals("j")){
                            binding.codeType.setText("CODE 128");
                        }else if(codeId.equals("s")){
                            binding.codeType.setText("QRCODE");
                        }else if(codeId.equals("s")){
                            binding.codeType.setText("QRCODE");
                        }
                        flag = true;
                    }else{
                        String code = sharedPreferences.getString(SharedPref.BARCODE, "");
                        binding.resultRL.setVisibility(View.VISIBLE);
                        if (data.equals(code)) {
                            binding.resultRL.setBackgroundColor(getResources().getColor(R.color.green));
                            binding.statusTV.setText("OK");
                            binding.resultRL.setVisibility(View.VISIBLE);
                        } else {
                            binding.resultRL.setBackgroundColor(getResources().getColor(R.color.red));
                            binding.statusTV.setText("Fail");
                            binding.resultRL.setVisibility(View.VISIBLE);
                        }

                    }

                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_barcode_comparison);

        init();

        checkSharedPref();

        getSharedPreferencesData();

        setBarcode();

        inputBarcode();


    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(barcodeDataReceiver, new IntentFilter(ACTION_BARCODE_DATA));
        claimScanner();
        checkSharedPref();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(barcodeDataReceiver);

        releaseScanner();
    }

    private void claimScanner() {
        Bundle properties = new Bundle();
        properties.putBoolean("DPR_DATA_INTENT", true); properties.putString("DPR_DATA_INTENT_ACTION", ACTION_BARCODE_DATA);
        sendBroadcast(new Intent(ACTION_CLAIM_SCANNER) .setPackage("com.intermec.datacollectionservice")
                .putExtra(EXTRA_SCANNER, "dcs.scanner.imager") .putExtra(EXTRA_PROFILE, "MyProfile1") .putExtra(EXTRA_PROPERTIES, properties)
        );
    }
    private void releaseScanner() {
        sendBroadcast(new Intent(ACTION_RELEASE_SCANNER)
        );
    }

    private void checkSharedPref() {
        boolean isActivate = sharedPreferences.getBoolean(SharedPref.BARCODE_COMPARISON_FUNTION,false);

        if(isActivate == true){
            binding.funtionLL.setEnabled(true);
            focusEditText();

        }else{
            for (int i = 0; i < binding.funtionLL.getChildCount(); i++) {
                View child = binding.funtionLL.getChildAt(i);
                child.setEnabled(false);
            }
        }
    }

    private void focusEditText() {
        binding.edittextLL.requestFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.showSoftInput(binding.edittextLL, InputMethodManager.SHOW_IMPLICIT);
    }

    private void inputBarcode() {
        binding.barCodeET.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                binding.scanDigitCount.setText(binding.barCodeET.getText().toString().length() +" Digits");

                // TODO Auto-generated method stub
                if (s.length() == 5) {
                    if (!flag) {
                        storeBarcode();
                    } else {
                        compareBarcode();
                    }

                } else if (flag && s.length() > 0) {
                    showFailMessage();
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                // TODO Auto-generated method stub
            }
        });

    }

    private void showFailMessage() {
        binding.resultRL.setBackgroundColor(getResources().getColor(R.color.red));
        binding.statusTV.setText("Fail");
        binding.resultRL.setVisibility(View.VISIBLE);
    }

    private void storeBarcode() {
        editor.putString(SharedPref.BARCODE, binding.barCodeET.getText().toString());
        editor.apply();
        binding.masterCodeDat.setText(binding.barCodeET.getText().toString());
        binding.digitCount.setText(binding.barCodeET.getText().toString().length() +" Digits");
        binding.barCodeET.setText("");
        binding.firstProductLL.setVisibility(View.VISIBLE);
        flag = true;
    }

    private void compareBarcode() {
        String code = sharedPreferences.getString(SharedPref.BARCODE, "");
        binding.resultRL.setVisibility(View.VISIBLE);
        if (binding.barCodeET.getText().toString().equals(code)) {
            binding.resultRL.setBackgroundColor(getResources().getColor(R.color.green));
            binding.statusTV.setText("OK");
            binding.resultRL.setVisibility(View.VISIBLE);
        } else {
            binding.resultRL.setBackgroundColor(getResources().getColor(R.color.red));
            binding.statusTV.setText("Fail");
            binding.resultRL.setVisibility(View.VISIBLE);
        }

    }

    private void setBarcode() {
        if (!barcode.isEmpty()) {
            binding.firstProductLL.setVisibility(View.VISIBLE);
            binding.masterCodeDat.setText(barcode);
            flag = true;
        }
    }

    private void getSharedPreferencesData() {
        barcode = sharedPreferences.getString(SharedPref.BARCODE, "");

    }

    private void init() {
        sharedPreferences = getSharedPreferences(SharedPref.COMPARE_PREFERENCE, MODE_PRIVATE);
        editor = sharedPreferences.edit();

    }

    public void openKeyboard(View view) {

        binding.sannnerLL.setVisibility(View.GONE);
        binding.edittextLL.setVisibility(View.VISIBLE);
        focusEditText();

    }

    public void openScaaner(View view) {

        binding.sannnerLL.setVisibility(View.VISIBLE);
        binding.edittextLL.setVisibility(View.GONE);
        disableFocus();
    }

    private void disableFocus() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public void onSettings(View view) {
        startActivity(new Intent(BarcodeComparisonActivity.this,ApplicationSettingsActivity.class));
    }
}