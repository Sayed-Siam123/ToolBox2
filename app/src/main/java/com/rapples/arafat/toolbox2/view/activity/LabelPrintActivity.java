package com.rapples.arafat.toolbox2.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.rapples.arafat.toolbox2.Database.MasterData_DB;
import com.rapples.arafat.toolbox2.Database.MasterExecutor;
import com.rapples.arafat.toolbox2.R;
import com.rapples.arafat.toolbox2.databinding.ActivityLabelPrintBinding;
import com.rapples.arafat.toolbox2.model.Masterdata;
import com.rapples.arafat.toolbox2.view.adapter.CustomMasterDataAdapter;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class LabelPrintActivity extends AppCompatActivity {

    private final boolean isScannerOpenTrue = true;

    private ActivityLabelPrintBinding binding;
    private final boolean flag = false;
    private boolean ScannerOpen = true;
    private List<Masterdata> masterDataList;
    private int quantity = 1;
    private TextView productDesc,price;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_label_print);
        binding.underline.setVisibility(View.INVISIBLE);

        init();
        setDataIntoMaster();
    }


    public void openKeyboard(View view) {
        binding.scanBarcodeLL.setVisibility(View.GONE);
        binding.editbarcodeLL.setVisibility(View.VISIBLE);
        binding.underline.setVisibility(View.GONE);
        ScannerOpen = false;
        binding.masterbarCodeFromET.requestFocus();
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        checkDatainMasterList();
    }

    public void openScanner(View view) {
        binding.scanBarcodeLL.setVisibility(View.VISIBLE);
        binding.editbarcodeLL.setVisibility(View.GONE);
        binding.underline.setVisibility(View.GONE);
        ScannerOpen = true;
        disableFocus();
        hideKeyboard(this);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void disableFocus() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public void backLabel(View view) {
        onBackPressed();
        hideKeyboard(this);
    }

    private void setDataIntoMaster() {

        MasterExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                masterDataList = MasterData_DB.getInstance(getApplicationContext()).MasterdataDao().loadAllData();
                Log.d("TAG", "run: " + masterDataList.size());
            }
        });
    }


    private void init() {
        masterDataList = new ArrayList<>();
        binding.labelQnty.setVisibility(View.INVISIBLE);
        binding.imageViewbarcode.setVisibility(View.GONE);
        binding.afterInputLL.setVisibility(View.GONE);
        binding.fabButton1.setVisibility(View.GONE);
        productDesc = findViewById(R.id.productDescTV);
        price = findViewById(R.id.productPriceTV);
        image = findViewById(R.id.barcodeIV);
    }


    public void checkDatainMasterList() {
        if (ScannerOpen) {
            Log.d("TAG", "checkDatainMasterList: ");
        } else {
            Log.d("TAG", "checkDatainMasterList: ");
            inputMasterTypeBarcode();
        }
    }


    private void inputMasterTypeBarcode() {

        binding.masterbarCodeFromET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Log.d("TAG", "onEditorAction: " + binding.masterbarCodeFromET.getText().toString());
                    for (int i = 0; i < masterDataList.size(); i++) {
                        Log.d("TAG", "onTextChanged: " + masterDataList.get(i).getBarcode());

                        if (masterDataList.get(i).getBarcode().equals(binding.masterbarCodeFromET.getText().toString())) {
                            Log.d("TAG", "onEditorAction: exist");
                            binding.afterInputLL.setVisibility(View.VISIBLE);
                            binding.editbarcodeLL.setVisibility(View.GONE);
                            binding.scanMastercodeTV.setVisibility(View.GONE);
                            binding.underline3.setVisibility(View.GONE);
                            binding.underline.setVisibility(View.VISIBLE);
                            binding.fabButton1.setVisibility(View.VISIBLE);
                            binding.quantityPrint.setVisibility(View.VISIBLE);
                            binding.quantityTV.setText(String.valueOf(quantity));
                            binding.barcodeET1.setText(masterDataList.get(i).getBarcode());
                            binding.underline2.setVisibility(View.VISIBLE);
                            binding.barcodeET1.requestFocus();
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                            binding.descTV.setText(masterDataList.get(i).getDescription());
                            binding.priceTV.setText(masterDataList.get(i).getPrice());
                            getBarcode(masterDataList.get(i));

                        } else {
                            Log.d("TAG", "onEditorAction: not exist");
                        }
                    }
                    handled = true;
                }
                return handled;
            }
        });

    }

    public void getBarcode(Masterdata data) {
        Bitmap bitmap = null;

        try {
            bitmap = encodeAsBitmap(data.getBarcode(), BarcodeFormat.CODE_128, 600, 300);
            //binding.imageViewbarcode.setImageBitmap(bitmap);
            binding.labelQnty.setVisibility(View.VISIBLE);
            finalLayout(bitmap, data);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    public void fabButtonClicktoPrint(View view) {
        Log.d("TAG", "fabButtonClicktoPrint: Print! Print!! Print!!! " + quantity);

    }

    public void decrementQuantity(View view) {
        if (quantity != 1) {
            quantity--;
            binding.quantityTV.setText(String.valueOf(quantity));
        }
    }

    public void incrementQuantity(View view) {
        if (quantity >= 1 && quantity != 10) {
            quantity++;
            binding.quantityTV.setText(String.valueOf(quantity));
        }
    }

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height) throws WriterException {
        String contentsToEncode = contents;
        if (contentsToEncode == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contentsToEncode);
        if (encoding != null) {
            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contentsToEncode, format, img_width, img_height, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }

    public void finalLayout(Bitmap barcodeImage, Masterdata data) {
        productDesc.setText(data.getDescription());
        if(data.getPrice().equals("") || data.getPrice().equals("null")){
            price.setText("");
        }
        else{
            price.setText(data.getPrice());
        }
        image.setImageBitmap(barcodeImage);
        Bitmap bitmap = Bitmap.createBitmap(binding.labelQnty.getWidth()+100,binding.labelQnty.getHeight()+50,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        binding.labelQnty.draw(canvas);
        binding.imageViewbarcode.setVisibility(View.VISIBLE);
        binding.imageViewbarcode.setImageBitmap(bitmap);
        binding.labelQnty.setVisibility(View.INVISIBLE);

    }

}