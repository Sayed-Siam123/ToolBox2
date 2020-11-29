package com.rapples.arafat.toolbox2.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.rapples.arafat.toolbox2.Database.Acquisition_DB;
import com.rapples.arafat.toolbox2.Database.MasterData_DB;
import com.rapples.arafat.toolbox2.Database.MasterExecutor;
import com.rapples.arafat.toolbox2.R;
import com.rapples.arafat.toolbox2.databinding.ActivityDataAcqusitionFileNameBinding;
import com.rapples.arafat.toolbox2.model.DataAcquisition;
import com.rapples.arafat.toolbox2.model.Masterdata;
import com.rapples.arafat.toolbox2.util.SharedPref;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DataAcqusitionFileNameActivity extends AppCompatActivity {

    private ActivityDataAcqusitionFileNameBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_data_acqusition_file_name);

        focusFileName();

        submitFileName();

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
        final DataAcquisition dataAcquisition = new DataAcquisition(filename, currentDateandTime);

        MasterExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Acquisition_DB.getInstance(getApplicationContext()).AcquisitionDao().insertAcquisitionData(dataAcquisition);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(DataAcqusitionFileNameActivity.this, AddDataAcquisitionActivity.class)
                                .putExtra(SharedPref.FILE_NAME, filename)
                        .putExtra(SharedPref.IS_ADDED,false));
                        finish();
                    }
                });
            }
        });


    }

    private void focusFileName() {
        binding.fileNameEt.requestFocus();
        InputMethodManager imm = (InputMethodManager) getApplication().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public void onSettingsFromDataAcquisition(View view) {
        startActivity(new Intent(DataAcqusitionFileNameActivity.this, ApplicationSettingsActivity.class));
    }

    public void onBackDataAcquisitionFileName(View view) {
        onBackPressed();
    }
}