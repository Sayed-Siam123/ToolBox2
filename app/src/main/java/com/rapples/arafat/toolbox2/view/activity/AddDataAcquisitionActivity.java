package com.rapples.arafat.toolbox2.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.rapples.arafat.toolbox2.Database.Acquisition_DB;
import com.rapples.arafat.toolbox2.Database.MasterExecutor;
import com.rapples.arafat.toolbox2.R;
import com.rapples.arafat.toolbox2.databinding.ActivityAddDataAcquisitionBinding;
import com.rapples.arafat.toolbox2.model.Product;
import com.rapples.arafat.toolbox2.util.SharedPref;
import com.rapples.arafat.toolbox2.view.adapter.CustomDataAcquisitionAdapter;
import com.rapples.arafat.toolbox2.view.adapter.LastDataAcquisitionAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class AddDataAcquisitionActivity extends AppCompatActivity {
    private static final String ACTION_BARCODE_DATA = "com.honeywell.sample.action.BARCODE_DATA";
    private static final String ACTION_CLAIM_SCANNER = "com.honeywell.aidc.action.ACTION_CLAIM_SCANNER";
    private static final String ACTION_RELEASE_SCANNER = "com.honeywell.aidc.action.ACTION_RELEASE_SCANNER";
    private static final String EXTRA_SCANNER = "com.honeywell.aidc.extra.EXTRA_SCANNER";
    private static final String EXTRA_PROFILE = "com.honeywell.aidc.extra.EXTRA_PROFILE";
    private static final String EXTRA_PROPERTIES = "com.honeywell.aidc.extra.EXTRA_PROPERTIES";
    private ActivityAddDataAcquisitionBinding binding;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private boolean isScannerOpen = true;
    private CustomDataAcquisitionAdapter adapter;
    private LastDataAcquisitionAdapter lastDataAcquisitionAdapter;
    private List<Product> productList;
    private List<Product> showList;
    private String isAdded = "false";
    private String fileName;
    private boolean allowDuplicate;
    private boolean quantityStatus;
    private boolean isFound;
    private MediaPlayer mediaPlayer;


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

                    if (sharedPreferences.getString(SharedPref.TONE, "").equals("Tone 1")) {
                        if (mediaPlayer != null) {
                            mediaPlayer.release();
                        }
                        mediaPlayer = MediaPlayer.create(AddDataAcquisitionActivity.this, R.raw.tone_one);
                        mediaPlayer.start();

                    } else if (sharedPreferences.getString(SharedPref.TONE, "").equals("Tone 2")) {
                        if (mediaPlayer != null) {
                            mediaPlayer.release();
                        }
                        mediaPlayer = MediaPlayer.create(AddDataAcquisitionActivity.this, R.raw.tone_two);
                        mediaPlayer.start();

                    } else if (sharedPreferences.getString(SharedPref.TONE, "").equals("Tone 3")) {
                        if (mediaPlayer != null) {
                            mediaPlayer.release();
                        }
                        mediaPlayer = MediaPlayer.create(AddDataAcquisitionActivity.this, R.raw.tone_three);
                        mediaPlayer.start();
                    }

                    if (checkBarCode(codeId)) {
                        if (isScannerOpen) {
                            binding.barCodeFromSCET.setText(data);
                        }
                    } else {
                        Toast.makeText(context, "Open barcode setting", Toast.LENGTH_SHORT).show();
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

        checkSetting();

        getFileName();

        defaultFocus();

        editTextTeco();

        configproductList();

    }


    private void checkSetting() {
        allowDuplicate = sharedPreferences.getBoolean(SharedPref.ALLOW_DUPLICATE_CODES, false);
        quantityStatus = sharedPreferences.getBoolean(SharedPref.QUANTITY_FIELD, false);

        if (quantityStatus) {
            binding.quantityLL.setVisibility(View.VISIBLE);
        } else {
            binding.quantityLL.setVisibility(View.GONE);
        }

    }


    private void init() {
        sharedPreferences = getSharedPreferences(SharedPref.SETTING_PREFERENCE, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        productList = new ArrayList<>();
        showList = new ArrayList<>();
    }

    private void configproductList() {

        if (productList.size() > 0) {
            showList.clear();
            showList.addAll(productList);
            Collections.reverse(showList);
            showLastBarcode(showList);
            binding.counterTv.setText(String.valueOf(showList.size()));
            binding.dataAcquisitionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new CustomDataAcquisitionAdapter(showList, this);
            binding.dataAcquisitionRecyclerView.setAdapter(adapter);


        }

    }

    private void showLastBarcode(List<Product> showLists) {
        binding.dataAcquisitionLastDataRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        lastDataAcquisitionAdapter = new LastDataAcquisitionAdapter(showLists, this);
        binding.dataAcquisitionLastDataRecyclerView.setAdapter(lastDataAcquisitionAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(binding.dataAcquisitionLastDataRecyclerView);

    }

    private void editTextTeco() {

        binding.barCodeET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                isFound = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (!binding.barCodeET.getText().toString().isEmpty()) {

                        if (checkDuplicate()) {
                            if (quantityStatus) {
                                binding.quantityEt.requestFocus();
                                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                            } else {
                                saveData();
                            }
                        }
                    }
                    handled = true;
                }
                return handled;
            }
        });

        binding.quantityEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (!binding.quantityEt.getText().toString().isEmpty()) {
                        if(isScannerOpen){
                            productList.add(new Product(fileName, binding.barCodeFromSCET.getText().toString(), "", binding.quantityEt.getText().toString()));
                            binding.lastBarLL.setVisibility(View.VISIBLE);
                            configproductList();
                            saveIntoDb(fileName, binding.barCodeFromSCET.getText().toString(), binding.quantityEt.getText().toString());
                            binding.barCodeFromSCET.setText("");
                            binding.quantityEt.setText("");
                            binding.barCodeFromSCET.requestFocus();
                        }else{
                            productList.add(new Product(fileName, binding.barCodeET.getText().toString(), "", binding.quantityEt.getText().toString()));
                            binding.lastBarLL.setVisibility(View.VISIBLE);
                            configproductList();
                            saveIntoDb(fileName, binding.barCodeET.getText().toString(), binding.quantityEt.getText().toString());
                            binding.barCodeET.setText("");
                            binding.quantityEt.setText("");
                            binding.barCodeET.requestFocus();
                        }

                    }

                    handled = true;
                }
                return handled;
            }
        });

        binding.barCodeFromSCET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (checkDuplicate()) {
                    if (quantityStatus) {
                        binding.quantityEt.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    } else {
                        productList.add(new Product(fileName, binding.barCodeFromSCET.getText().toString(), "", ""));
                        binding.lastBarLL.setVisibility(View.VISIBLE);
                        configproductList();
                        saveIntoDb(fileName, binding.barCodeFromSCET.getText().toString(), binding.quantityEt.getText().toString());
                        binding.barCodeFromSCET.setText("");
                    }
                }


            }
        });
    }

    private boolean checkDuplicate() {
        boolean status = true;
        String data = "";

        if (isScannerOpen) {
            data = binding.barCodeFromSCET.getText().toString().trim();
        } else {
            data = binding.barCodeET.getText().toString().trim();
        }

        if (productList.size() > 0) {
            if (allowDuplicate) {
                status = true;
            } else {
                for (int i = 0; i < productList.size(); i++) {
                    if (productList.get(i).getBarcode().equals(data)) {
                        Toast.makeText(AddDataAcquisitionActivity.this, "Duplicate barcode", Toast.LENGTH_SHORT).show();
                        isFound = true;
                        if (isScannerOpen) {
                            binding.barCodeFromSCET.requestFocus();
                            binding.barCodeFromSCET.setSelectAllOnFocus(true);
                        } else {
                            binding.barCodeFromSCET.requestFocus();
                            binding.barCodeET.setSelectAllOnFocus(true);
                            binding.barCodeET.requestFocus();
                            binding.barCodeET.setSelectAllOnFocus(true);
                        }
                    }
                }
                if (isFound) {
                    status = false;
                } else {
                    status = true;
                }

            }
        }
        return status;
    }


    private void saveData() {
        if (quantityStatus) {
            productList.add(new Product(fileName, binding.barCodeET.getText().toString(), "", binding.quantityEt.getText().toString()));
        } else {
            productList.add(new Product(fileName, binding.barCodeET.getText().toString(), "", ""));
        }
        binding.lastBarLL.setVisibility(View.VISIBLE);
        configproductList();
        saveIntoDb(fileName, binding.barCodeET.getText().toString(), binding.quantityEt.getText().toString());
        binding.barCodeET.setText("");
        binding.quantityEt.setText("");


    }

    private void saveIntoDb(String fileName, String barcode, String quantity) {
        final Product product = new Product(fileName, barcode, "", quantity);

        Toast.makeText(this, ""+barcode, Toast.LENGTH_SHORT).show();
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
        binding.barCodeFromSCET.requestFocus();
        disableFocus();
        hideKeyboard(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkSetting();
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
        isAdded = getIntent().getStringExtra(SharedPref.IS_ADDED);
        binding.fileNameTv.setText(fileName);
        if (isAdded != null) {
            if (isAdded.equals("true")) {
                MasterExecutor.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        productList = Acquisition_DB.getInstance(AddDataAcquisitionActivity.this).ProductDao().loadAllproduct(fileName);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                configproductList();
                                binding.listCardView.setVisibility(View.GONE);
                                binding.dataAcquisitionRecyclerView.setVisibility(View.VISIBLE);
                                binding.lastBarLL.setVisibility(View.VISIBLE);
                                binding.showListItemLL.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                });
            }
        }
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


    public void showList(View view) {
        binding.dataAcquisitionRecyclerView.setVisibility(View.VISIBLE);
        binding.listCardView.setVisibility(View.GONE);
        binding.showListItemLL.setVisibility(View.VISIBLE);

    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return true;
        }


        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            final int position = viewHolder.getAdapterPosition();

            switch (direction) {
                case ItemTouchHelper.RIGHT:


                    break;

                case ItemTouchHelper.LEFT:
                    deleteProduct(showList.get(position));

                    break;
            }

        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(AddDataAcquisitionActivity.this, c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(AddDataAcquisitionActivity.this, R.color.red))
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
                    .setActionIconTint(ContextCompat.getColor(recyclerView.getContext(), android.R.color.white))
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    private void deleteProduct(final Product product) {

        MasterExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Acquisition_DB.getInstance(AddDataAcquisitionActivity.this).ProductDao().deleteProduct(product);
                productList.remove(product);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        configproductList();
                    }
                });
            }
        });
    }

    private boolean checkBarCode(String codeId) {
        boolean value;

        if (codeId.equals("b") && sharedPreferences.getBoolean(SharedPref.CODE_39, false)) {
            value = true;
        } else if (codeId.equals("j") && sharedPreferences.getBoolean(SharedPref.CODE_39, false)) {
            value = true;
        } else if (codeId.equals("d") && sharedPreferences.getBoolean(SharedPref.EAN_13, false)) {
            value = true;
        } else if (codeId.equals("w") && sharedPreferences.getBoolean(SharedPref.DATA_MATRIX, false)) {
            value = true;
        } else if (codeId.equals("s") && sharedPreferences.getBoolean(SharedPref.QR_CODE, false)) {
            value = true;
        } else {
            value = false;
        }

        return value;
    }
}