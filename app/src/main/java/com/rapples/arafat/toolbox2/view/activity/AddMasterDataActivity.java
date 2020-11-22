package com.rapples.arafat.toolbox2.view.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.rapples.arafat.toolbox2.Database.MasterData_DB;
import com.rapples.arafat.toolbox2.Database.MasterExecutor;
import com.rapples.arafat.toolbox2.R;
import com.rapples.arafat.toolbox2.databinding.ActivityAddMasterdataBinding;
import com.rapples.arafat.toolbox2.model.Masterdata;
import com.rapples.arafat.toolbox2.util.SharedPref;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

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

                    binding.barCodeFromSCET.setText(data);



                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_masterdata);

        init();

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

    private void getSharedPreferencesData() {
        boolean priceVisibility = sharedPreferences.getBoolean(SharedPref.MANAGE_PRICES, false);
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
        } else {
            saveDataIntoroom();
        }


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
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

    }

    public void openScannerForNewData(View view) {
        binding.edittextLL.setVisibility(View.GONE);
        binding.sannnerLL.setVisibility(View.VISIBLE);
        binding.barCodeFromSCET.requestFocus();
        isScannerOpenTrue = true;
        disableFocus();
        hideKeyboard(this);
    }

    private void setDefaultFocus() {
        binding.barCodeFromSCET.requestFocus();
        disableFocus();
        hideKeyboard(this);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (isScannerOpenTrue) {
            barcode = binding.barCodeFromSCET.getText().toString();
        } else {
            barcode = binding.barCodeET.getText().toString();
        }
        description = binding.descriptionEt.getText().toString();
        price = binding.priceEt.getText().toString();


        if (!barcode.isEmpty() && !description.isEmpty()) {
            saveDataIntoroom();
        }
        disableFocus();
        hideKeyboard(this);

    }
}