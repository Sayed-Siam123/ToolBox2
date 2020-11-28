package com.rapples.arafat.toolbox2.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.rapples.arafat.toolbox2.Database.Acquisition_DB;
import com.rapples.arafat.toolbox2.Database.MasterExecutor;
import com.rapples.arafat.toolbox2.R;
import com.rapples.arafat.toolbox2.databinding.ActivityDataAcquisitionDetailsBinding;
import com.rapples.arafat.toolbox2.model.DataAcquisition;
import com.rapples.arafat.toolbox2.model.Product;
import com.rapples.arafat.toolbox2.util.SharedPref;
import com.rapples.arafat.toolbox2.view.adapter.CustomDataAcquisitionAdapter;
import com.rapples.arafat.toolbox2.view.adapter.CustomDataAcquisitionEditAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class DataAcquisitionDetailsActivity extends AppCompatActivity {

    private ActivityDataAcquisitionDetailsBinding binding;
    private String fileName;
    private String date;
    private int id;
    private String editable;
    private List<Product> productList;
    private CustomDataAcquisitionAdapter adapter;
    private CustomDataAcquisitionEditAdapter editAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_data_acquisition_details);

        init();

        getFileName();

        getProductList();


    }

    private void init() {
        productList = new ArrayList<>();
    }

    private void getProductList() {

        MasterExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                productList = Acquisition_DB.getInstance(DataAcquisitionDetailsActivity.this).ProductDao().loadAllproduct(fileName);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.valueCountTv.setText(productList.size() + " Values");
                        for (int i = 0; i < productList.size(); i++) {
                        }
                        if (productList.size() > 0) {
                            Collections.reverse(productList);
                        }
                        binding.dataAcquisitionDetailsRecyclerView.setLayoutManager(new LinearLayoutManager(DataAcquisitionDetailsActivity.this));
                        if (editable.equals("true")) {
                            editAdapter = new CustomDataAcquisitionEditAdapter(productList, DataAcquisitionDetailsActivity.this);
                            binding.dataAcquisitionDetailsRecyclerView.setAdapter(editAdapter);

                        } else {
                            adapter = new CustomDataAcquisitionAdapter(productList, DataAcquisitionDetailsActivity.this);
                            binding.dataAcquisitionDetailsRecyclerView.setAdapter(adapter);

                        }


                        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
                        itemTouchHelper.attachToRecyclerView(binding.dataAcquisitionDetailsRecyclerView);


                    }
                });
            }
        });

    }

    private void getFileName() {
        fileName = getIntent().getStringExtra(SharedPref.FILE_NAME);
        date = getIntent().getStringExtra(SharedPref.DATE);
        id = Integer.parseInt(getIntent().getStringExtra(SharedPref.ID));
        editable = getIntent().getStringExtra(SharedPref.EDITABLE);


        binding.fileNameTv.setText(fileName);
        binding.dateTv.setText(date);

    }

    public void onBackDataAcquisitionDetails(View view) {
        onBackPressed();
    }

    public void onSettingsFromDataAcquisitionDetails(View view) {
        startActivity(new Intent(DataAcquisitionDetailsActivity.this, ApplicationSettingsActivity.class));
    }

    public void DeleteFile(View view) {
        Snackbar.make(binding.dataAcquisitionDetailsRecyclerView, "Do you want to delete this product?", Snackbar.LENGTH_LONG)
                .setAction("Yes", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteProduct(fileName);
                        final DataAcquisition dataAcquisition = new DataAcquisition(id, fileName, date);
                        MasterExecutor.getInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                Acquisition_DB.getInstance(getApplicationContext()).AcquisitionDao().deleteAcquisitionData(dataAcquisition);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        onBackPressed();
                                    }
                                });
                            }
                        });
                    }
                }).setActionTextColor(getResources().getColor(R.color.white)).show();

    }

    private void deleteProduct(final String fileName) {

        MasterExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                productList = Acquisition_DB.getInstance(DataAcquisitionDetailsActivity.this).ProductDao().loadAllproduct(fileName);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MasterExecutor.getInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i < productList.size(); i++) {
                                    Acquisition_DB.getInstance(DataAcquisitionDetailsActivity.this).ProductDao().deleteProduct(productList.get(i));
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


    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return true;
        }


        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            final int position = viewHolder.getAdapterPosition();

            switch (direction) {
                case ItemTouchHelper.RIGHT:


                    break;

                case ItemTouchHelper.LEFT:
                    deleteProductItem(productList.get(position));
                    break;
            }

        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(DataAcquisitionDetailsActivity.this, c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(DataAcquisitionDetailsActivity.this, R.color.red))
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
                    .setActionIconTint(ContextCompat.getColor(recyclerView.getContext(), android.R.color.white))
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    private void deleteProductItem(final Product product) {
        MasterExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Acquisition_DB.getInstance(DataAcquisitionDetailsActivity.this).ProductDao().deleteProduct(product);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getProductList();
                    }
                });
            }
        });
    }
}