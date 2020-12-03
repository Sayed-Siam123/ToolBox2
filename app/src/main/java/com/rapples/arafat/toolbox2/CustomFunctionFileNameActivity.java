package com.rapples.arafat.toolbox2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.rapples.arafat.toolbox2.Database.Acquisition_DB;
import com.rapples.arafat.toolbox2.Database.CustomFunction_DB;
import com.rapples.arafat.toolbox2.Database.MasterExecutor;
import com.rapples.arafat.toolbox2.databinding.ActivityCustomFunctionFileNameBinding;
import com.rapples.arafat.toolbox2.model.CustomFunction;
import com.rapples.arafat.toolbox2.model.DataAcquisition;
import com.rapples.arafat.toolbox2.util.SharedPref;
import com.rapples.arafat.toolbox2.view.activity.AddCustomFunctionDataAcquisition;
import com.rapples.arafat.toolbox2.view.activity.AddDataAcquisitionActivity;
import com.rapples.arafat.toolbox2.view.activity.CustomFunctionSettingsActivity;
import com.rapples.arafat.toolbox2.view.activity.DataAcqusitionFileNameActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomFunctionFileNameActivity extends AppCompatActivity {

    private ActivityCustomFunctionFileNameBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_custom_function_file_name);


        setTitle();

        focusFileName();

        submitFileName();
    }

    private void setTitle() {
        binding.titleTv.setText(getIntent().getStringExtra(SharedPref.CUSTOM_FUNCTION_NAME));
    }

    private void focusFileName() {
        binding.fileNameEt.requestFocus();
        InputMethodManager imm = (InputMethodManager) getApplication().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    private void submitFileName() {
        binding.fileNameEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (!binding.fileNameEt.getText().toString().isEmpty()) {

                        saveFileNameRoomDb(binding.fileNameEt.getText().toString());

                    }
                    handled = true;
                }
                return handled;
            }
        });
    }

    private void saveFileNameRoomDb(final String filename) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String currentDateandTime = sdf.format(new Date());
        final CustomFunction customFunction = new CustomFunction(filename, currentDateandTime);

        MasterExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                CustomFunction_DB.getInstance(getApplicationContext()).CustomFunctionDao().insertCustomFunctionData(customFunction);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(CustomFunctionFileNameActivity.this, AddCustomFunctionDataAcquisition.class)
                                .putExtra(SharedPref.FILE_NAME, filename)
                                .putExtra(SharedPref.IS_ADDED, false));
                        finish();
                    }
                });
            }
        });


    }

    public void onBackCustomFunctionFileName(View view) {
        onBackPressed();
    }

    public void onSettingsFromCustomFunctionFileName(View view) {
        startActivity(new Intent(CustomFunctionFileNameActivity.this, CustomFunctionSettingsActivity.class));
    }
}