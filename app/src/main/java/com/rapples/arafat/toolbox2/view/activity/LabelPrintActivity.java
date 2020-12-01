package com.rapples.arafat.toolbox2.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.rapples.arafat.toolbox2.Database.MasterData_DB;
import com.rapples.arafat.toolbox2.Database.MasterExecutor;
import com.rapples.arafat.toolbox2.R;
import com.rapples.arafat.toolbox2.databinding.ActivityLabelPrintBinding;
import com.rapples.arafat.toolbox2.model.Masterdata;
import com.rapples.arafat.toolbox2.view.adapter.CustomMasterDataAdapter;

import java.util.ArrayList;
import java.util.List;

public class LabelPrintActivity extends AppCompatActivity {

    private boolean isScannerOpenTrue = true;

    private ActivityLabelPrintBinding binding;
    private boolean flag = false;
    private boolean ScannerOpen = true;
    private List<Masterdata> masterDataList;
    private int quantity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_label_print);
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
                Log.d("TAG", "run: "+masterDataList.size());
            }
        });
    }


    private void init() {
        masterDataList = new ArrayList<>();
    }


    public void checkDatainMasterList(){
        if(ScannerOpen){
            Log.d("TAG", "checkDatainMasterList: ");
        }

        else{
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
                    Log.d("TAG", "onEditorAction: "+ binding.masterbarCodeFromET.getText().toString());
                    for (int i = 0; i < masterDataList.size(); i++) {
                        Log.d("TAG", "onTextChanged: " + masterDataList.get(i).getBarcode());

                        if(masterDataList.get(i).getBarcode().equals(binding.masterbarCodeFromET.getText().toString())){
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

                        }

                        else{
                            Log.d("TAG", "onEditorAction: not exist");
                        }
                    }
                    handled = true;
                }
                return handled;
            }
        });

    }

    public void fabButtonClicktoPrint(View view) {
        Log.d("TAG", "fabButtonClicktoPrint: Print! Print!! Print!!! "+quantity);

    }

    public void decrementQuantity(View view) {
        if(quantity != 1){
            quantity--;
            binding.quantityTV.setText(String.valueOf(quantity));
        }
    }

    public void incrementQuantity(View view) {
        if(quantity >= 1 && quantity != 10){
            quantity++;
            binding.quantityTV.setText(String.valueOf(quantity));
        }
    }
}