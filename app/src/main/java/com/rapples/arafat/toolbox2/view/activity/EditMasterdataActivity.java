package com.rapples.arafat.toolbox2.view.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.rapples.arafat.toolbox2.Database.MasterData_DB;
import com.rapples.arafat.toolbox2.Database.MasterExecutor;
import com.rapples.arafat.toolbox2.R;
import com.rapples.arafat.toolbox2.databinding.ActivityEditMasterdataBinding;
import com.rapples.arafat.toolbox2.model.Masterdata;
import com.rapples.arafat.toolbox2.util.SharedPref;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditMasterdataActivity extends AppCompatActivity {

    private String barcode;
    private String description;
    private String price;
    private String image;
    private int id;
    private ActivityEditMasterdataBinding binding;
    private SharedPreferences sharedPreferences;
    private boolean isScannerOpenTrue;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil. setContentView(this,R.layout.activity_edit_masterdata);

        init();

        getDataFromSharedPref();

        getIntentData();

        setValue();

        setDefaultFocus();

    }

    private void updateProduct() {
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
            updateDataInfo();
        }


    }

    private void updateDataInfo() {
        final Masterdata masterdata = new Masterdata(id,barcode, description, price, image);

        MasterExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                MasterData_DB.getInstance(getApplicationContext()).MasterdataDao().updatePerson(masterdata);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }
        });
    }

    private void getDataFromSharedPref() {
        boolean managePrice = sharedPreferences.getBoolean(SharedPref.MANAGE_PRICES,false);
        if(managePrice){
            binding.priceLL.setVisibility(View.VISIBLE);
        }else{
            binding.priceLL.setVisibility(View.GONE);
        }
    }

    private void setDefaultFocus() {
        binding.barCodeFromSCET.setSelectAllOnFocus(true);
        binding.barCodeFromSCET.requestFocus();
        binding.barCodeFromSCET.setSelectAllOnFocus(true);

    }

    private void setValue() {
        binding.barCodeFromSCET.setText(barcode);
        binding.barCodeET.setText(barcode);
        binding.descriptionEt.setText(description);
        if(price != null){
            binding.priceEt.setText(price);
        }
        if(image != null){
        binding.imageView.setImageBitmap(decodeBase64(image));
        binding.clearCV.setVisibility(View.VISIBLE);
        }
    }

    private void getIntentData() {
        barcode = getIntent().getStringExtra("barcode");
        description = getIntent().getStringExtra("description");
        price = getIntent().getStringExtra("price");
        image = getIntent().getStringExtra("image");
        id = Integer.parseInt(getIntent().getStringExtra("id"));

    }

    private void init() {
        sharedPreferences = getSharedPreferences(SharedPref.SETTING_PREFERENCE,MODE_PRIVATE);
        isScannerOpenTrue = true;
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public void onBackMasterData(View view) {
        onBackPressed();
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

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public void openKeyboardForUpdateValue(View view) {
        binding.edittextLL.setVisibility(View.VISIBLE);
        binding.sannnerLL.setVisibility(View.GONE);
        binding.barCodeET.requestFocus();
        isScannerOpenTrue = false;
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);


    }

    public void openScannerForUpdateData(View view) {
        binding.edittextLL.setVisibility(View.GONE);
        binding.sannnerLL.setVisibility(View.VISIBLE);
        binding.barCodeFromSCET.requestFocus();
        isScannerOpenTrue = true;
        disableFocus();
        hideKeyboard(this);
    }

    @Override
    public void onBackPressed() {
        updateProduct();
        super.onBackPressed();

    }

    public void clearProductImage(View view) {
        binding.imageView.setVisibility(View.GONE);
        binding.pictureLL.setVisibility(View.VISIBLE);
        binding.clearCV.setVisibility(View.GONE);
    }

    public void openEditCamera(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 0);
    }

    public void openEditGallery(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }
}