 package com.rapples.arafat.toolbox2.view.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.rapples.arafat.toolbox2.Database.MasterData_DB;
import com.rapples.arafat.toolbox2.Database.MasterExecutor;
import com.rapples.arafat.toolbox2.R;
import com.rapples.arafat.toolbox2.databinding.ActivityAddMasterdataBinding;
import com.rapples.arafat.toolbox2.model.Masterdata;
import com.rapples.arafat.toolbox2.util.SharedPref;
import com.rapples.arafat.toolbox2.view.adapter.CustomMasterDataAdapter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import static android.telephony.PhoneNumberUtils.formatNumber;

public class AddMasterDataActivity extends AppCompatActivity {
    private static final String ACTION_BARCODE_DATA = "com.honeywell.sample.action.BARCODE_DATA";
    private static final String ACTION_CLAIM_SCANNER = "com.honeywell.aidc.action.ACTION_CLAIM_SCANNER";
    private static final String ACTION_RELEASE_SCANNER = "com.honeywell.aidc.action.ACTION_RELEASE_SCANNER";
    private static final String EXTRA_SCANNER = "com.honeywell.aidc.extra.EXTRA_SCANNER";
    private static final String EXTRA_PROFILE = "com.honeywell.aidc.extra.EXTRA_PROFILE";
    private static final String EXTRA_PROPERTIES = "com.honeywell.aidc.extra.EXTRA_PROPERTIES";

    private ActivityAddMasterdataBinding binding;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String barcode;
    private String description;
    private String price;
    private String image;
    private boolean isScannerOpenTrue = true;
    boolean priceVisibility;
    private String current = "";
    private List<Masterdata> masterDataList;
    boolean isFound = false;
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
                        mediaPlayer = MediaPlayer.create(AddMasterDataActivity.this, R.raw.tone_one);
                        mediaPlayer.start();

                    } else if (sharedPreferences.getString(SharedPref.TONE, "").equals("Tone 2")) {
                        if (mediaPlayer != null) {
                            mediaPlayer.release();
                        }
                        mediaPlayer = MediaPlayer.create(AddMasterDataActivity.this, R.raw.tone_two);
                        mediaPlayer.start();

                    } else if (sharedPreferences.getString(SharedPref.TONE, "").equals("Tone 3")) {
                        if (mediaPlayer != null) {
                            mediaPlayer.release();
                        }
                        mediaPlayer = MediaPlayer.create(AddMasterDataActivity.this, R.raw.tone_three);
                        mediaPlayer.start();
                    }

                    if(checkBarCode(codeId)) {
                        binding.barCodeFromSCET.setText(data);
                    }else{
                        Toast.makeText(context, "Open barcode setting", Toast.LENGTH_SHORT).show();
                    }


                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_masterdata);

        init();

        checkIfAlreadyhavePermission();

        getSharedPreferencesData();

        setAutoFocusforbarCodeFormScanner();

        setDefaultFocus();


    }


    private void setAutoFocusforbarCodeFormScanner() {

        binding.barCodeFromSCET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.descriptionEt.requestFocus();
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
        if (isScannerOpenTrue) {
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
        sendBroadcast(new Intent(ACTION_RELEASE_SCANNER));
    }

    private void getSharedPreferencesData() {
        priceVisibility = sharedPreferences.getBoolean(SharedPref.MANAGE_PRICES, false);
        if (priceVisibility) {
            binding.priceLL.setVisibility(View.VISIBLE);
        } else {
            binding.priceLL.setVisibility(View.GONE);
        }

    }

    private void init() {
        sharedPreferences = getSharedPreferences(SharedPref.SETTING_PREFERENCE, MODE_PRIVATE);
        editor = sharedPreferences.edit();

    }


    public void onBack(View view) {
        onBackPressed();
    }

    public void openCamera(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == 0) {
                Bundle bundle = data.getExtras();
                final Bitmap bitmap = (Bitmap) bundle.get("data");
                binding.clearCV.setVisibility(View.VISIBLE);
                binding.pictureLL.setVisibility(View.GONE);
                binding.imageView.setVisibility(View.VISIBLE);
                image = encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 100);
                binding.imageView.setImageBitmap(bitmap);
            } else if (requestCode == 1) {

                Uri uri = data.getData();
                binding.clearCV.setVisibility(View.VISIBLE);
                binding.imageView.setVisibility(View.VISIBLE);
                binding.pictureLL.setVisibility(View.GONE);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    image = encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 100);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                binding.imageView.setImageURI(uri);
            }

        }
    }

    public void openGallery(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }


    public void clearImage(View view) {
        binding.imageView.setVisibility(View.GONE);
        binding.pictureLL.setVisibility(View.VISIBLE);
        binding.clearCV.setVisibility(View.GONE);
    }

    public void fabButtonClick(View view) {

        if (isScannerOpenTrue) {
            barcode = binding.barCodeFromSCET.getText().toString();
        } else {
            barcode = binding.barCodeET.getText().toString();
        }
        description = binding.descriptionEt.getText().toString();
        price = binding.priceEt.getText().toString();


        if (barcode.isEmpty()) {
            Toast.makeText(this, "Enter barcode", Toast.LENGTH_SHORT).show();
        } else if (description.isEmpty()) {
            Toast.makeText(this, "Enter description", Toast.LENGTH_SHORT).show();
        } else if(isFound){
            Toast.makeText(this, "Barcode already exists", Toast.LENGTH_SHORT).show();
        }
        else {
            saveDataIntoroom();
        }


    }

    private void checkValidity() {
        if(isScannerOpenTrue){
            barcode = binding.barCodeFromSCET.getText().toString();
        }else{
            barcode = binding.barCodeET.getText().toString();
        }


        MasterExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                masterDataList = MasterData_DB.getInstance(getApplicationContext()).MasterdataDao().loadAllData();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isFound = false;
                        for (Masterdata data : masterDataList) {
                            if (data.getBarcode().equals(barcode)) {
                                Toast.makeText(AddMasterDataActivity.this, "Barcode already exists", Toast.LENGTH_SHORT).show();
                                isFound = true;

                                if (isScannerOpenTrue) {
                                    binding.descriptionEt.requestFocus();
                                    binding.barCodeFromSCET.setSelectAllOnFocus(true);
                                    binding.barCodeFromSCET.requestFocus();
                                    binding.barCodeFromSCET.setSelectAllOnFocus(true);
                                } else {
                                    binding.descriptionEt.requestFocus();
                                    binding.barCodeET.setSelectAllOnFocus(true);
                                    binding.barCodeET.requestFocus();
                                    binding.barCodeET.setSelectAllOnFocus(true);
                                    InputMethodManager imm = (InputMethodManager) getApplication().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                                }

                            }
                        }
                        if (!isFound) {
                            binding.descriptionEt.requestFocus();

                        }


                    }
                });
            }
        });
    }

    private void saveDataIntoroom() {

        final Masterdata masterdata = new Masterdata(barcode, description, price, image);

        MasterExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                MasterData_DB.getInstance(getApplicationContext()).MasterdataDao().insertPerson(masterdata);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.descriptionEt.setText("");
                        binding.barCodeET.setText("");
                        binding.barCodeFromSCET.setText("");
                        binding.priceEt.setText("");
                        binding.imageView.setImageResource(R.drawable.ic_baseline_clear_24);
                        barcode = "";
                        image = "";
                        price = "";
                        description = "";
                        binding.clearCV.setVisibility(View.GONE);
                        binding.imageView.setVisibility(View.GONE);
                        binding.pictureLL.setVisibility(View.VISIBLE);
                        if (isScannerOpenTrue) {
                            binding.barCodeFromSCET.setText("");
                            binding.barCodeFromSCET.requestFocus();
                        } else {
                            binding.barCodeET.setText("");
                            binding.barCodeET.requestFocus();
                            InputMethodManager imm = (InputMethodManager) getApplication().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                        }
                    }
                });
            }
        });


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


    public void openKeyboardForNewValue(View view) {
        binding.edittextLL.setVisibility(View.VISIBLE);
        binding.sannnerLL.setVisibility(View.GONE);
        binding.barCodeET.requestFocus();
        isScannerOpenTrue = false;
        unRegisteredScanner();
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

    }

    public void openScannerForNewData(View view) {
        binding.edittextLL.setVisibility(View.GONE);
        binding.sannnerLL.setVisibility(View.VISIBLE);
        binding.barCodeFromSCET.requestFocus();
        isScannerOpenTrue = true;
        registeredScanner();
        disableFocus();
        hideKeyboard(this);
    }

    private void setDefaultFocus() {
        binding.barCodeFromSCET.requestFocus();
        disableFocus();
        hideKeyboard(this);

        binding.barCodeET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    checkValidity();
                    handled = true;
                }
                return handled;
            }
        });

        binding.barCodeFromSCET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    checkValidity();
                    handled = true;
                }
                return handled;
            }
        });

        binding.priceEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    disableFocus();
                    hideKeyboard(AddMasterDataActivity.this);
                    handled = true;

                    if (binding.priceEt.getText().toString() != null && !binding.priceEt.getText().toString().equals("")) {
                        binding.priceEt.setText(NumberFormat.getNumberInstance(Locale.GERMANY).format(Double.parseDouble(binding.priceEt.getText().toString())));
                    }
                }
                return handled;
            }
        });

        binding.descriptionEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (priceVisibility) {
                        binding.priceEt.requestFocus();
                    } else {
                        disableFocus();
                        hideKeyboard(AddMasterDataActivity.this);
                    }
                    handled = true;
                }
                return handled;
            }
        });

    }


    private void checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,}, 101);
        }
    }

    public void registeredScanner() {
        registerReceiver(barcodeDataReceiver, new IntentFilter(ACTION_BARCODE_DATA));
        claimScanner();
    }

    public void unRegisteredScanner() {
        unregisterReceiver(barcodeDataReceiver);
        releaseScanner();

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