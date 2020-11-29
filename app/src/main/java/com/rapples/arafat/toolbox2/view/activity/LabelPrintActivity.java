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
import android.view.View;
import android.view.inputmethod.InputMethodManager;

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


        }
    }



}