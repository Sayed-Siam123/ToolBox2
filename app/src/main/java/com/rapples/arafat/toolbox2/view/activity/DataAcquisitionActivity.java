package com.rapples.arafat.toolbox2.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.rapples.arafat.toolbox2.Database.Acquisition_DB;
import com.rapples.arafat.toolbox2.Database.MasterData_DB;
import com.rapples.arafat.toolbox2.Database.MasterExecutor;
import com.rapples.arafat.toolbox2.R;
import com.rapples.arafat.toolbox2.databinding.ActivityDataAcquisitionBinding;
import com.rapples.arafat.toolbox2.model.DataAcquisition;
import com.rapples.arafat.toolbox2.model.Product;
import com.rapples.arafat.toolbox2.util.SharedPref;
import com.rapples.arafat.toolbox2.view.adapter.CustomFileAdapter;
import com.rapples.arafat.toolbox2.view.adapter.CustomMasterDataAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class DataAcquisitionActivity extends AppCompatActivity {

    private List<DataAcquisition> dataAcquisitionList;
    private ActivityDataAcquisitionBinding binding;
    private CustomFileAdapter adapter;
    List<Product> productList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_data_acquisition);

        init();
    }

    private void init() {
        productList = new ArrayList<>();
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

                        if (dataAcquisitionList != null) {
                            Collections.reverse(dataAcquisitionList);
                        }

                        binding.dataAcusitionRecyclearView.setLayoutManager(new LinearLayoutManager(DataAcquisitionActivity.this));
                        adapter = new CustomFileAdapter(dataAcquisitionList, DataAcquisitionActivity.this);
                        binding.dataAcusitionRecyclearView.setAdapter(adapter);

                        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
                        itemTouchHelper.attachToRecyclerView(binding.dataAcusitionRecyclearView);

                    }
                });
            }
        });

    }

    public void onBackDataAcquisition(View view) {
        onBackPressed();
    }

    public void onSettingsFromBarcodeAcquisition(View view) {
        startActivity(new Intent(DataAcquisitionActivity.this, ApplicationSettingsActivity.class));
    }

    public void addDList(View view) {
        startActivity(new Intent(DataAcquisitionActivity.this, DataAcqusitionFileNameActivity.class));
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return true;
        }


        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            final int position = viewHolder.getAdapterPosition();

            switch (direction) {
                case ItemTouchHelper.RIGHT:
                    startActivity(new Intent(DataAcquisitionActivity.this, DataAcquisitionDetailsActivity.class)
                            .putExtra(SharedPref.FILE_NAME, dataAcquisitionList.get(position).getFileName())
                            .putExtra(SharedPref.DATE, dataAcquisitionList.get(position).getDate())
                            .putExtra(SharedPref.ID,String.valueOf(dataAcquisitionList.get(position).id))
                            .putExtra(SharedPref.EDITABLE,"true"));


                    break;

                case ItemTouchHelper.LEFT:
                    getFileFromRoom();
                    Snackbar.make(binding.dataAcusitionRecyclearView, "Do you want to delete this product?", Snackbar.LENGTH_LONG)
                            .setAction("Yes", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    deleteItem(position);
                                }
                            }).setActionTextColor(getResources().getColor(R.color.white)).show();


                    break;
            }

        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(DataAcquisitionActivity.this, c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(DataAcquisitionActivity.this, R.color.green))
                    .addSwipeRightActionIcon(R.drawable.ic_baseline_edit_24)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(DataAcquisitionActivity.this, R.color.red))
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
                    .setActionIconTint(ContextCompat.getColor(recyclerView.getContext(), android.R.color.white))
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    private void deleteItem(final int position) {

        deleteProduct(dataAcquisitionList.get(position).getFileName());
        MasterExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Acquisition_DB.getInstance(getApplicationContext()).AcquisitionDao().deleteAcquisitionData(dataAcquisitionList.get(position));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getFileFromRoom();
                    }
                });
            }
        });
    }

    private void deleteProduct(final String fileName) {

        MasterExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                productList = Acquisition_DB.getInstance(DataAcquisitionActivity.this).ProductDao().loadAllproduct(fileName);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MasterExecutor.getInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i < productList.size(); i++) {
                                    Acquisition_DB.getInstance(DataAcquisitionActivity.this).ProductDao().deleteProduct(productList.get(i));
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                        }
                                    });
                                }
                            }
                        });


                    }
                });
            }
        });
    }

}