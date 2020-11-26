package com.rapples.arafat.toolbox2.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.rapples.arafat.toolbox2.Database.Acquisition_DB;
import com.rapples.arafat.toolbox2.Database.MasterData_DB;
import com.rapples.arafat.toolbox2.Database.MasterExecutor;
import com.rapples.arafat.toolbox2.R;
import com.rapples.arafat.toolbox2.databinding.ActivityDataAcquisitionBinding;
import com.rapples.arafat.toolbox2.model.DataAcquisition;
import com.rapples.arafat.toolbox2.view.adapter.CustomFileAdapter;
import com.rapples.arafat.toolbox2.view.adapter.CustomMasterDataAdapter;

import java.util.List;

public class DataAcquisitionActivity extends AppCompatActivity {

    private List<DataAcquisition> dataAcquisitionList;
    private ActivityDataAcquisitionBinding binding;
    private CustomFileAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_data_acquisition);

    }

    @Override
    protected void onResume() {
        getFileFromRoom();
        super.onResume();
    }

    private void getFileFromRoom() {
        MasterExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                dataAcquisitionList = Acquisition_DB.getInstance(getApplicationContext()).AcquisitionDao().loadAllData();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.dataAcusitionRecyclearView.setLayoutManager(new LinearLayoutManager(DataAcquisitionActivity.this));
                        adapter = new CustomFileAdapter(dataAcquisitionList,DataAcquisitionActivity.this);
                        binding.dataAcusitionRecyclearView.setAdapter(adapter);


                    }
                });
            }
        });

    }

    public void onBackDataAcquisition(View view) {
        onBackPressed();
    }

    public void onSettingsFromBarcodeAcquisition(View view) {
        startActivity(new Intent(DataAcquisitionActivity.this,ApplicationSettingsActivity.class));
    }

    public void addDList(View view) {
        startActivity(new Intent(DataAcquisitionActivity.this,DataAcqusitionFileNameActivity.class));
    }
}