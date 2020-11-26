package com.rapples.arafat.toolbox2.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.rapples.arafat.toolbox2.Database.Acquisition_DB;
import com.rapples.arafat.toolbox2.Database.MasterExecutor;
import com.rapples.arafat.toolbox2.R;
import com.rapples.arafat.toolbox2.databinding.ActivityAddDataAcquisitionBinding;
import com.rapples.arafat.toolbox2.model.DataAcquisition;
import com.rapples.arafat.toolbox2.model.Product;
import com.rapples.arafat.toolbox2.util.SharedPref;
import com.rapples.arafat.toolbox2.view.adapter.CustomDataAcquisitionAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AddDataAcquisitionActivity extends AppCompatActivity {
    private static final String ACTION_BARCODE_DATA = "com.honeywell.sample.action.BARCODE_DATA";
    private static final String ACTION_CLAIM_SCANNER = "com.honeywell.aidc.action.ACTION_CLAIM_SCANNER";
    private static final String ACTION_RELEASE_SCANNER = "com.honeywell.aidc.action.ACTION_RELEASE_SCANNER";
    private static final String EXTRA_SCANNER = "com.honeywell.aidc.extra.EXTRA_SCANNER";
    private static final String EXTRA_PROFILE = "com.honeywell.aidc.extra.EXTRA_PROFILE";
    private static final String EXTRA_PROPERTIES = "com.honeywell.aidc.extra.EXTRA_PROPERTIES";
    private ActivityAddDataAcquisitionBinding binding;
    private boolean isScannerOpen = true;
    private CustomDataAcquisitionAdapter adapter;
    private List<Product> productList;
    private String fileName;

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

                    if (isScannerOpen) {
                        binding.barCodeFromSCET.setText(data);
                    }

                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_data_acquisition);

        init();

        getFileName();

        defaultFocus();

        submitCode();

        configproductList();

    }

    private void init() {
        productList = new ArrayList<>();
    }

    private void configproductList() {


        if (productList.size() >0) {
            Collections.reverse(productList);
            binding.lastbarcodeTv.setText(productList.get(0).getBarcode());
            binding.counterTv.setText(String.valueOf(productList.size()));
            binding.dataAcquisitionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new CustomDataAcquisitionAdapter(productList, this);
            binding.dataAcquisitionRecyclerView.setAdapter(adapter);
        }

    }

    private void submitCode() {
        binding.barCodeET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (!binding.barCodeET.getText().toString().isEmpty()) {
                        productList.add(new Product(fileName,binding.barCodeET.getText().toString(), ""));
                        binding.lastBarLL.setVisibility(View.VISIBLE);
                        configproductList();
                        saveIntoDb(fileName,binding.barCodeET.getText().toString());
                        binding.barCodeET.setText("");

                    }

                    handled = true;
                }
                return handled;
            }
        });
    }

    private void saveIntoDb(String fileName, String barcode) {
        final Product product = new Product(fileName,barcode, "");

        MasterExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Acquisition_DB.getInstance(getApplicationContext()).ProductDao().insertProduct(product);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        });
    }

    private void defaultFocus() {
        disableFocus();
        hideKeyboard(this);
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

        if (isScannerOpen) {
            unregisterReceiver(barcodeDataReceiver);
            releaseScanner();
        }

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

    private void getFileName() {

        fileName = getIntent().getStringExtra(SharedPref.FILE_NAME);
        binding.fileNameTv.setText(fileName);
    }

    public void onBackDataAcquisitionAdd(View view) {
        onBackPressed();
    }

    public void onSettingsFromDataAcquisitionAdd(View view) {
        startActivity(new Intent(AddDataAcquisitionActivity.this, ApplicationSettingsActivity.class));
    }


    private void disableFocus() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }


    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void openScannerForAddData(View view) {
        binding.edittextLL.setVisibility(View.GONE);
        binding.sannnerLL.setVisibility(View.VISIBLE);
        binding.barCodeFromSCET.requestFocus();
        registeredScanner();
        isScannerOpen = true;
        disableFocus();
        hideKeyboard(this);
    }

    public void openKeyboardForAddValue(View view) {
        binding.edittextLL.setVisibility(View.VISIBLE);
        binding.sannnerLL.setVisibility(View.GONE);
        binding.barCodeET.requestFocus();
        unRegisteredScanner();
        isScannerOpen = false;
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }


    public void registeredScanner() {
        registerReceiver(barcodeDataReceiver, new IntentFilter(ACTION_BARCODE_DATA));
        claimScanner();
    }

    public void unRegisteredScanner() {
        unregisterReceiver(barcodeDataReceiver);
        releaseScanner();

    }


}