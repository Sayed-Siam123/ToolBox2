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
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rapples.arafat.toolbox2.Database.Acquisition_DB;
import com.rapples.arafat.toolbox2.Database.CustomFunction_DB;
import com.rapples.arafat.toolbox2.Database.MasterExecutor;
import com.rapples.arafat.toolbox2.R;
import com.rapples.arafat.toolbox2.databinding.ActivityAddCustomFunctionDataAcquisitionBinding;
import com.rapples.arafat.toolbox2.model.CustomFunctionProduct;
import com.rapples.arafat.toolbox2.model.Field;
import com.rapples.arafat.toolbox2.model.Product;
import com.rapples.arafat.toolbox2.util.SharedPref;
import com.rapples.arafat.toolbox2.view.adapter.CustomDataAcquisitionAdapter;
import com.rapples.arafat.toolbox2.view.adapter.CustomFunctionProductAdapter;
import com.rapples.arafat.toolbox2.view.adapter.CustomFunctionSingleProductAdapter;
import com.rapples.arafat.toolbox2.view.adapter.LastDataAcquisitionAdapter;

import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class AddCustomFunctionDataAcquisition extends AppCompatActivity {
    private static final String ACTION_BARCODE_DATA = "com.honeywell.sample.action.BARCODE_DATA";
    private static final String ACTION_CLAIM_SCANNER = "com.honeywell.aidc.action.ACTION_CLAIM_SCANNER";
    private static final String ACTION_RELEASE_SCANNER = "com.honeywell.aidc.action.ACTION_RELEASE_SCANNER";
    private static final String EXTRA_SCANNER = "com.honeywell.aidc.extra.EXTRA_SCANNER";
    private static final String EXTRA_PROFILE = "com.honeywell.aidc.extra.EXTRA_PROFILE";
    private static final String EXTRA_PROPERTIES = "com.honeywell.aidc.extra.EXTRA_PROPERTIES";
    private SharedPreferences sharedPreferences;
    private ActivityAddCustomFunctionDataAcquisitionBinding binding;
    private SharedPreferences.Editor editor;
    private List<Field> fieldList;
    private List<CustomFunctionProduct> customFunctionProducts;
    private List<CustomFunctionProduct> showList;
    private CustomFunctionProductAdapter adapter;
    private CustomFunctionSingleProductAdapter lastAdapter;
    private String fieldListInString;
    private int state;
    private String field1Result = "";
    private String field2Result = "";
    private String field3Result = "";
    private String field4Result = "";
    private String field5Result = "";
    private String fileName;
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
                        mediaPlayer = MediaPlayer.create(AddCustomFunctionDataAcquisition.this, R.raw.tone_one);
                        mediaPlayer.start();

                    } else if (sharedPreferences.getString(SharedPref.TONE, "").equals("Tone 2")) {
                        if (mediaPlayer != null) {
                            mediaPlayer.release();
                        }
                        mediaPlayer = MediaPlayer.create(AddCustomFunctionDataAcquisition.this, R.raw.tone_two);
                        mediaPlayer.start();

                    } else if (sharedPreferences.getString(SharedPref.TONE, "").equals("Tone 3")) {
                        if (mediaPlayer != null) {
                            mediaPlayer.release();
                        }
                        mediaPlayer = MediaPlayer.create(AddCustomFunctionDataAcquisition.this, R.raw.tone_three);
                        mediaPlayer.start();
                    }
                    if (checkBarCode(codeId)) {
                        switch (state) {
                            case 1:
                                binding.field1barCodeFromSCET.setText(data);
                                break;
                            case 2:
                                binding.field2barCodeFromSCET.setText(data);
                                break;
                            case 3:
                                binding.field3barCodeFromSCET.setText(data);
                                break;
                            case 4:
                                binding.field4barCodeFromSCET.setText(data);
                                break;
                            case 5:
                                binding.field5barCodeFromSCET.setText(data);
                                break;

                            default:

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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_custom_function_data_acquisition);

        init();

        getSharedPreferencesData();

        setFiled1();

        setFiled2();

        setFiled3();

        setFiled4();

        setFiled5();


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
        registerReceiver(barcodeDataReceiver, new IntentFilter(ACTION_BARCODE_DATA));
        unregisterReceiver(barcodeDataReceiver);
        releaseScanner();


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

    private void setFiled1() {

        if (fieldList.size() > 0) {
            binding.field1LL.setVisibility(View.VISIBLE);
            binding.field1Title.setText(fieldList.get(0).getFieldName());

            if (fieldList.get(0).getFieldType().equals("Barcode")) {
                binding.field1sannnerLL.setVisibility(View.VISIBLE);
                binding.field1barCodeFromSCET.requestFocus();
                disableFocus();
                hideKeyboard(AddCustomFunctionDataAcquisition.this);
                state = 1;

                binding.field1barCodeFromSCET.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        field1Result = binding.field1barCodeFromSCET.getText().toString();
                        if (fieldList.size() > 1) {
                            redirectNextField2();

                        } else {
                            saveDataIntoDb();
                        }

                    }
                });

                binding.field1barCodeET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        boolean handled = false;
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            field1Result = binding.field1barCodeET.getText().toString();
                            if (fieldList.size() > 1) {
                                redirectNextField2();
                            } else {
                                saveDataIntoDb();
                            }

                            handled = true;
                        }
                        return handled;
                    }
                });
            } else {
                binding.field1Et.setVisibility(View.VISIBLE);
                binding.field1Et.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.showSoftInput(binding.field2EdittextLL, InputMethodManager.SHOW_IMPLICIT);
                if (fieldList.get(0).getFieldType().equals("Numeric")) {
                    binding.field1Et.setInputType(InputType.TYPE_CLASS_NUMBER);
                } else {
                    binding.field1Et.setInputType(InputType.TYPE_CLASS_TEXT);
                }

                binding.field1Et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        boolean handled = false;
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            field1Result = binding.field1Et.getText().toString();
                            if (fieldList.size() > 1) {
                                redirectNextField2();
                            } else {
                                saveDataIntoDb();
                            }

                            handled = true;
                        }
                        return handled;
                    }
                });


            }
        }
    }

    private void setFiled2() {

        if (fieldList.size() > 1) {


            if (fieldList.get(1).getFieldType().equals("Barcode")) {

                binding.field2barCodeFromSCET.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        field2Result = binding.field2barCodeFromSCET.getText().toString();
                        if (fieldList.size() > 2) {
                            redirectNextField3();

                        } else {
                            saveDataIntoDb();
                        }

                    }
                });

                binding.field2barCodeET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        boolean handled = false;
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            field2Result = binding.field2barCodeET.getText().toString();
                            if (fieldList.size() > 2) {
                                redirectNextField3();
                            } else {
                                saveDataIntoDb();
                            }

                            handled = true;
                        }
                        return handled;
                    }
                });
            } else {
                binding.field2Et.setVisibility(View.VISIBLE);
                binding.field2Et.requestFocus();
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                if (fieldList.get(1).getFieldType().equals("Numeric")) {
                    binding.field2Et.setInputType(InputType.TYPE_CLASS_NUMBER);
                } else {
                    binding.field2Et.setInputType(InputType.TYPE_CLASS_TEXT);
                }

                binding.field2Et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        boolean handled = false;
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            field2Result = binding.field2Et.getText().toString();
                            if (fieldList.size() > 2) {
                                redirectNextField3();
                            } else {
                                saveDataIntoDb();
                            }

                            handled = true;
                        }
                        return handled;
                    }
                });


            }
        }
    }

    private void setFiled3() {

        if (fieldList.size() > 2) {


            if (fieldList.get(2).getFieldType().equals("Barcode")) {

                binding.field3barCodeFromSCET.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        field3Result = binding.field3barCodeFromSCET.getText().toString();
                        if (fieldList.size() > 3) {
                            redirectNextField4();

                        } else {
                            saveDataIntoDb();
                        }

                    }
                });

                binding.field3barCodeET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        boolean handled = false;
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            field3Result = binding.field3barCodeET.getText().toString();
                            if (fieldList.size() > 3) {
                                redirectNextField4();
                            } else {
                                saveDataIntoDb();
                            }

                            handled = true;
                        }
                        return handled;
                    }
                });
            } else {
                binding.field3Et.setVisibility(View.VISIBLE);
                binding.field3Et.requestFocus();
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                if (fieldList.get(2).getFieldType().equals("Numeric")) {
                    binding.field3Et.setInputType(InputType.TYPE_CLASS_NUMBER);
                } else {
                    binding.field3Et.setInputType(InputType.TYPE_CLASS_TEXT);
                }

                binding.field3Et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        boolean handled = false;
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            field3Result = binding.field3Et.getText().toString();
                            if (fieldList.size() > 3) {
                                redirectNextField4();
                            } else {
                                saveDataIntoDb();
                            }

                            handled = true;
                        }
                        return handled;
                    }
                });


            }
        }
    }

    private void setFiled4() {

        if (fieldList.size() > 3) {


            if (fieldList.get(3).getFieldType().equals("Barcode")) {

                binding.field4barCodeFromSCET.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        field4Result = binding.field4barCodeFromSCET.getText().toString();
                        if (fieldList.size() > 4) {
                            redirectNextField5();

                        } else {
                            saveDataIntoDb();
                        }

                    }
                });

                binding.field4barCodeET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        boolean handled = false;
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            field4Result = binding.field4barCodeET.getText().toString();
                            if (fieldList.size() > 4) {
                                redirectNextField5();
                            } else {
                                saveDataIntoDb();
                            }

                            handled = true;
                        }
                        return handled;
                    }
                });
            } else {
                binding.field4Et.setVisibility(View.VISIBLE);
                binding.field4Et.requestFocus();
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                if (fieldList.get(3).getFieldType().equals("Numeric")) {
                    binding.field4Et.setInputType(InputType.TYPE_CLASS_NUMBER);
                } else {
                    binding.field4Et.setInputType(InputType.TYPE_CLASS_TEXT);
                }

                binding.field4Et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        boolean handled = false;
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            field4Result = binding.field4Et.getText().toString();
                            if (fieldList.size() > 4) {
                                redirectNextField5();
                            } else {
                                saveDataIntoDb();
                            }

                            handled = true;
                        }
                        return handled;
                    }
                });


            }
        }
    }

    private void setFiled5() {

        if (fieldList.size() > 4) {

            if (fieldList.get(4).getFieldType().equals("Barcode")) {

                binding.field5barCodeFromSCET.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        field5Result = binding.field5barCodeFromSCET.getText().toString();
                        saveDataIntoDb();
                    }
                });

                binding.field5barCodeET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        boolean handled = false;
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            field5Result = binding.field5barCodeET.getText().toString();
                            saveDataIntoDb();
                            handled = true;
                        }
                        return handled;
                    }
                });
            } else {
                binding.field5Et.setVisibility(View.VISIBLE);
                binding.field5Et.requestFocus();
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                if (fieldList.get(4).getFieldType().equals("Numeric")) {
                    binding.field5Et.setInputType(InputType.TYPE_CLASS_NUMBER);
                } else {
                    binding.field5Et.setInputType(InputType.TYPE_CLASS_TEXT);
                }

                binding.field5Et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        boolean handled = false;
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            field5Result = binding.field5Et.getText().toString();
                            saveDataIntoDb();
                            handled = true;
                        }
                        return handled;
                    }
                });


            }
        }
    }

    private void redirectNextField5() {
        binding.field5LL.setVisibility(View.VISIBLE);
        binding.field5Title.setText(fieldList.get(4).getFieldName());
        state = 5;

        if (fieldList.get(4).getFieldType().equals("Barcode")) {
            registeredScanner();
            binding.field5sannnerLL.setVisibility(View.VISIBLE);
            binding.field5barCodeFromSCET.requestFocus();
        } else {
            binding.field5Et.setVisibility(View.VISIBLE);
            binding.field5Et.requestFocus();
            InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            if (fieldList.get(4).getFieldType().equals("Numeric")) {
                binding.field5Et.setInputType(InputType.TYPE_CLASS_NUMBER);

            } else {
                binding.field5Et.setInputType(InputType.TYPE_CLASS_TEXT);
                binding.field5Et.requestFocus();
            }
        }

    }

    private void redirectNextField4() {
        binding.field4LL.setVisibility(View.VISIBLE);
        binding.field4Title.setText(fieldList.get(3).getFieldName());
        state = 4;

        if (fieldList.get(3).getFieldType().equals("Barcode")) {
            registeredScanner();
            binding.field4sannnerLL.setVisibility(View.VISIBLE);
            binding.field4barCodeFromSCET.requestFocus();
        } else {
            binding.field4Et.setVisibility(View.VISIBLE);
            binding.field4Et.requestFocus();
            InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            if (fieldList.get(3).getFieldType().equals("Numeric")) {
                binding.field4Et.setInputType(InputType.TYPE_CLASS_NUMBER);

            } else {
                binding.field4Et.setInputType(InputType.TYPE_CLASS_TEXT);
                binding.field4Et.requestFocus();
            }
        }

    }

    private void redirectNextField3() {
        binding.field3LL.setVisibility(View.VISIBLE);
        binding.field3Title.setText(fieldList.get(2).getFieldName());
        state = 3;

        if (fieldList.get(2).getFieldType().equals("Barcode")) {
            registeredScanner();
            binding.field3sannnerLL.setVisibility(View.VISIBLE);
            binding.field3barCodeFromSCET.requestFocus();
        } else {
            binding.field3Et.setVisibility(View.VISIBLE);
            binding.field3Et.requestFocus();
            InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            if (fieldList.get(2).getFieldType().equals("Numeric")) {
                binding.field3Et.setInputType(InputType.TYPE_CLASS_NUMBER);

            } else {
                binding.field3Et.setInputType(InputType.TYPE_CLASS_TEXT);
                binding.field3Et.requestFocus();
            }
        }
    }

    private void redirectNextField2() {
        binding.field2LL.setVisibility(View.VISIBLE);
        binding.field2Title.setText(fieldList.get(1).getFieldName());
        state = 2;

        if (fieldList.get(1).getFieldType().equals("Barcode")) {
            registeredScanner();
            binding.field2sannnerLL.setVisibility(View.VISIBLE);
            binding.field2barCodeFromSCET.requestFocus();
        } else {
            binding.field2Et.setVisibility(View.VISIBLE);
            binding.field2Et.requestFocus();
            InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            if (fieldList.get(1).getFieldType().equals("Numeric")) {
                binding.field2Et.setInputType(InputType.TYPE_CLASS_NUMBER);

            } else {
                binding.field2Et.setInputType(InputType.TYPE_CLASS_TEXT);
                binding.field2Et.requestFocus();
            }
        }
    }

    private void configproductList() {

        if (customFunctionProducts.size() > 0) {
            showList.clear();
            showList.addAll(customFunctionProducts);
            Collections.reverse(showList);
            showLastBarcode(showList);
            binding.counterTv.setText(String.valueOf(showList.size()));
            binding.customFunctionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new CustomFunctionProductAdapter(showList, this);
            binding.customFunctionRecyclerView.setAdapter(adapter);


        }

    }

    private void showLastBarcode(List<CustomFunctionProduct> showLists) {
        binding.customFunctionLastDataRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        lastAdapter = new CustomFunctionSingleProductAdapter(showLists, this);
        binding.customFunctionLastDataRecyclerView.setAdapter(lastAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(binding.customFunctionLastDataRecyclerView);

    }

    private void saveDataIntoDb() {
        final CustomFunctionProduct product = new CustomFunctionProduct(fileName, field1Result, field2Result, field3Result, field4Result, field5Result);

        customFunctionProducts.add(product);
        MasterExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                CustomFunction_DB.getInstance(getApplicationContext()).CustomFunctionProductDao().insertProduct(product);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        configproductList();
                        clearData();
                        registeredScanner();
                        binding.lastBarLL.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }

    private void clearData() {
        binding.field1barCodeET.setText("");
        binding.field1barCodeFromSCET.setText("");
        binding.field1Et.setText("");
        binding.field1EdittextLL.setVisibility(View.GONE);

        binding.field2barCodeET.setText("");
        binding.field2barCodeFromSCET.setText("");
        binding.field2Et.setText("");
        binding.field2EdittextLL.setVisibility(View.GONE);


        binding.field3barCodeET.setText("");
        binding.field3barCodeFromSCET.setText("");
        binding.field3Et.setText("");
        binding.field3EdittextLL.setVisibility(View.GONE);


        binding.field4barCodeET.setText("");
        binding.field4barCodeFromSCET.setText("");
        binding.field4Et.setText("");
        binding.field4EdittextLL.setVisibility(View.GONE);


        binding.field5barCodeET.setText("");
        binding.field5barCodeFromSCET.setText("");
        binding.field5Et.setText("");
        binding.field5EdittextLL.setVisibility(View.GONE);


        binding.field2LL.setVisibility(View.GONE);
        binding.field3LL.setVisibility(View.GONE);
        binding.field4LL.setVisibility(View.GONE);
        binding.field5LL.setVisibility(View.GONE);

        field1Result = "";
        field2Result = "";
        field3Result = "";
        field4Result = "";
        field5Result = "";


        setFiled1();


    }

    private void getSharedPreferencesData() {

        fieldListInString = sharedPreferences.getString(SharedPref.CUSTOM_FIELD_LIST, "");
        binding.toolbarTitle.setText(sharedPreferences.getString(SharedPref.CUSTOM_FUNCTION_NAME, ""));

        fileName = getIntent().getStringExtra(SharedPref.FILE_NAME);

        binding.fileNameTv.setText(fileName);


        if (!fieldListInString.isEmpty()) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Field>>() {
            }.getType();
            fieldList = gson.fromJson(fieldListInString, type);
        }


    }

    private void init() {
        sharedPreferences = getSharedPreferences(SharedPref.SETTING_PREFERENCE, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        fieldList = new ArrayList<>();
        customFunctionProducts = new ArrayList<>();
        showList = new ArrayList<>();
    }

    public void onBackCustomFunctionAdd(View view) {
        onBackPressed();
    }

    public void onSettingsFromCustomFunction(View view) {
        startActivity(new Intent(AddCustomFunctionDataAcquisition.this, CustomFunctionSettingsActivity.class));
    }

    public void registeredScanner() {
        registerReceiver(barcodeDataReceiver, new IntentFilter(ACTION_BARCODE_DATA));
        claimScanner();
    }

    public void unRegisteredScanner() {
        unregisterReceiver(barcodeDataReceiver);
        releaseScanner();

    }

    public void openScannerField1(View view) {
        registeredScanner();
        binding.field1sannnerLL.setVisibility(View.VISIBLE);
        binding.field1EdittextLL.setVisibility(View.GONE);
        binding.field1barCodeFromSCET.requestFocus();
        disableFocus();
        hideKeyboard(AddCustomFunctionDataAcquisition.this);
    }

    public void openKeyboardField1(View view) {
        unRegisteredScanner();
        binding.field1sannnerLL.setVisibility(View.GONE);
        binding.field1EdittextLL.setVisibility(View.VISIBLE);
        binding.field1barCodeET.requestFocus();
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public void openScannerField2(View view) {
        registeredScanner();
        binding.field2sannnerLL.setVisibility(View.VISIBLE);
        binding.field2EdittextLL.setVisibility(View.GONE);
        binding.field2barCodeFromSCET.requestFocus();
        disableFocus();
        hideKeyboard(AddCustomFunctionDataAcquisition.this);
    }

    public void openKeyboardField2(View view) {
        unRegisteredScanner();
        binding.field2sannnerLL.setVisibility(View.GONE);
        binding.field2EdittextLL.setVisibility(View.VISIBLE);
        binding.field2barCodeET.requestFocus();
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public void openScannerField3(View view) {
        registeredScanner();
        binding.field3sannnerLL.setVisibility(View.VISIBLE);
        binding.field3EdittextLL.setVisibility(View.GONE);
        binding.field3barCodeFromSCET.requestFocus();
        disableFocus();
        hideKeyboard(AddCustomFunctionDataAcquisition.this);
    }

    public void openKeyboardField3(View view) {
        unRegisteredScanner();
        binding.field3sannnerLL.setVisibility(View.GONE);
        binding.field3EdittextLL.setVisibility(View.VISIBLE);
        binding.field3barCodeET.requestFocus();
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }


    public void openScannerField4(View view) {
        registeredScanner();
        binding.field4sannnerLL.setVisibility(View.VISIBLE);
        binding.field4EdittextLL.setVisibility(View.GONE);
        binding.field4barCodeFromSCET.requestFocus();
        disableFocus();
        hideKeyboard(AddCustomFunctionDataAcquisition.this);
    }

    public void openKeyboardField4(View view) {
        unRegisteredScanner();
        binding.field4sannnerLL.setVisibility(View.GONE);
        binding.field4EdittextLL.setVisibility(View.VISIBLE);
        binding.field4barCodeET.requestFocus();
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public void openScannerField5(View view) {
        registeredScanner();
        binding.field5sannnerLL.setVisibility(View.VISIBLE);
        binding.field5EdittextLL.setVisibility(View.GONE);
        binding.field5barCodeFromSCET.requestFocus();
        disableFocus();
        hideKeyboard(AddCustomFunctionDataAcquisition.this);
    }

    public void openKeyboardField5(View view) {
        unRegisteredScanner();
        binding.field5sannnerLL.setVisibility(View.GONE);
        binding.field5EdittextLL.setVisibility(View.VISIBLE);
        binding.field5barCodeET.requestFocus();
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }


    public void showCustomFunctionList(View view) {
        binding.customFunctionRecyclerView.setVisibility(View.VISIBLE);
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
            new RecyclerViewSwipeDecorator.Builder(AddCustomFunctionDataAcquisition.this, c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(AddCustomFunctionDataAcquisition.this, R.color.red))
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
                    .setActionIconTint(ContextCompat.getColor(recyclerView.getContext(), android.R.color.white))
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    private void deleteProduct(final CustomFunctionProduct product) {

        MasterExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                CustomFunction_DB.getInstance(AddCustomFunctionDataAcquisition.this).CustomFunctionProductDao().deleteProduct(product);
                customFunctionProducts.remove(product);
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