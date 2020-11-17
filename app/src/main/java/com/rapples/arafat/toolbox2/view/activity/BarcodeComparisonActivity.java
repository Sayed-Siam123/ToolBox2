package com.rapples.arafat.toolbox2.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

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
                        binding.masterbarCodeFromSCET.setText(data);
                        binding.masterScanDigitCount.setText(data.length() + " Digits");
                        flag = true;
                    }else{
                        binding.barCodeFromSCET.setText(data);
                        binding.scanDigitCount.setText(data.length() + " Digits");
                        flag = false;
                    }


                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_barcode_comparison);


        setDefaultFocus();

        inputMasterTypeBarcode();

        inputReferenceBarcode();

        inputMasterTypeBarcodeForScanner();

        inputReferenceBarcodeForScanner();




    }

    private void setDefaultFocus() {
        binding.masterbarCodeFromSCET.requestFocus();
        disableFocus();
        hideKeyboard(this);
    }

    private void inputReferenceBarcode() {
        binding.barCodeET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    binding.masterBarCodeET.setSelectAllOnFocus(true);
                    binding.masterBarCodeET.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(binding.edittextLL, InputMethodManager.SHOW_IMPLICIT);

                    binding.masterBarCodeET.setSelectAllOnFocus(true);
                    binding.scanDigitCount.setText(binding.barCodeET.getText().toString().length() + " Digits");
                    compareBarcode();

                    handled = true;
                }
                return handled;
            }
        });



    }

    private void inputMasterTypeBarcode() {

        binding.masterBarCodeET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    binding.barcodeLL.setVisibility(View.VISIBLE);
                    binding.barCodeET.setText("");
                    binding.barCodeET.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(binding.edittextLL, InputMethodManager.SHOW_IMPLICIT);
                    binding.resultRL.setVisibility(View.GONE);
                    binding.masterScanDigitCount.setText(binding.masterBarCodeET.getText().toString().length() + " Digits");


                    handled = true;
                }
                return handled;
            }
        });

    }

    private void inputReferenceBarcodeForScanner() {
        binding.barCodeFromSCET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.masterbarCodeFromSCET.setSelectAllOnFocus(true);
                binding.masterbarCodeFromSCET.requestFocus();
                binding.scanDigitCount.setText(binding.masterbarCodeFromSCET.getText().toString().length() + " Digits");
                compareBarcodeForScanner();

            }
        });



    }

    private void inputMasterTypeBarcodeForScanner() {

        binding.masterbarCodeFromSCET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.barcodeLL.setVisibility(View.VISIBLE);
                binding.barCodeFromSCET.setText("");
                binding.barCodeFromSCET.requestFocus();
                binding.resultRL.setVisibility(View.GONE);
                binding.masterScanDigitCount.setText(binding.masterbarCodeFromSCET.getText().toString().length() + " Digits");

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(barcodeDataReceiver, new IntentFilter(ACTION_BARCODE_DATA));
        claimScanner();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(barcodeDataReceiver);
        releaseScanner();
    }

    private void claimScanner() {
        Bundle properties = new Bundle();
        properties.putBoolean("DPR_DATA_INTENT", true);
        properties.putString("DPR_DATA_INTENT_ACTION", ACTION_BARCODE_DATA);
        sendBroadcast(new Intent(ACTION_CLAIM_SCANNER).setPackage("com.intermec.datacollectionservice")
                .putExtra(EXTRA_SCANNER, "dcs.scanner.imager").putExtra(EXTRA_PROFILE, "MyProfile1").putExtra(EXTRA_PROPERTIES, properties)
        );
    }

    private void releaseScanner() {
        sendBroadcast(new Intent(ACTION_RELEASE_SCANNER)
        );
    }


    private void focusEditText() {
        binding.edittextLL.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.showSoftInput(binding.edittextLL, InputMethodManager.SHOW_IMPLICIT);
    }



    private void compareBarcode() {
        String code = binding.masterBarCodeET.getText().toString();
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

    private void compareBarcodeForScanner() {
        String code = binding.masterbarCodeFromSCET.getText().toString();
        binding.resultRL.setVisibility(View.VISIBLE);
        if (binding.barCodeFromSCET.getText().toString().equals(code)) {
            binding.resultRL.setBackgroundColor(getResources().getColor(R.color.green));
            binding.statusTV.setText("OK");
            binding.resultRL.setVisibility(View.VISIBLE);
        } else {
            binding.resultRL.setBackgroundColor(getResources().getColor(R.color.red));
            binding.statusTV.setText("Fail");
            binding.resultRL.setVisibility(View.VISIBLE);
        }

    }


    public void openKeyboard(View view) {

        binding.edittextLL.setVisibility(View.VISIBLE);
        binding.sannnerLL.setVisibility(View.GONE);
        binding.barCodeET.requestFocus();
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

    }

    public void openScaaner(View view) {

        binding.edittextLL.setVisibility(View.GONE);
        binding.sannnerLL.setVisibility(View.VISIBLE);
        binding.masterbarCodeFromSCET.requestFocus();
        disableFocus();
        hideKeyboard(this);
    }

    private void disableFocus() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public void onSettings(View view) {
        startActivity(new Intent(BarcodeComparisonActivity.this, ApplicationSettingsActivity.class));
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void openScaanerMaster(View view) {
        binding.masterEdittextLL.setVisibility(View.GONE);
        binding.masterSannnerLL.setVisibility(View.VISIBLE);
        binding.masterbarCodeFromSCET.requestFocus();
        disableFocus();
        hideKeyboard(this);
    }

    public void openKeyboardMaster(View view) {
        binding.masterEdittextLL.setVisibility(View.VISIBLE);
        binding.masterSannnerLL.setVisibility(View.GONE);
        binding.masterBarCodeET.requestFocus();
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

    }
}