package com.rapples.arafat.toolbox2.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.rapples.arafat.toolbox2.Database.Acquisition_DB;
import com.rapples.arafat.toolbox2.Database.CustomFunction_DB;
import com.rapples.arafat.toolbox2.Database.MasterExecutor;
import com.rapples.arafat.toolbox2.R;
import com.rapples.arafat.toolbox2.databinding.ActivityCustomFunctionBinding;
import com.rapples.arafat.toolbox2.model.CustomFunction;
import com.rapples.arafat.toolbox2.model.CustomFunctionProduct;
import com.rapples.arafat.toolbox2.util.SharedPref;
import com.rapples.arafat.toolbox2.view.adapter.CustomFileAdapter;
import com.rapples.arafat.toolbox2.view.adapter.CustomFunctionFileAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class CustomFunctionActivity extends AppCompatActivity {

    private ActivityCustomFunctionBinding binding;
    private CustomFunctionFileAdapter adapter;
    private List<CustomFunction> customFunctionList;
    private List<CustomFunctionProduct> customFunctionProductList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_custom_function);

        init();

        getIntentData();

        getFileFromRoom();


    }

    private void getFileFromRoom() {
        MasterExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                customFunctionList = CustomFunction_DB.getInstance(getApplicationContext()).CustomFunctionDao().loadAllData();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (customFunctionList != null) {
                            Collections.reverse(customFunctionList);
                        }

                        binding.customFunctionRecyclearView.setLayoutManager(new LinearLayoutManager(CustomFunctionActivity.this));
                        adapter = new CustomFunctionFileAdapter(
                                customFunctionList, CustomFunctionActivity.this);
                        binding.customFunctionRecyclearView.setAdapter(adapter);

                        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
                        itemTouchHelper.attachToRecyclerView(binding.customFunctionRecyclearView);

                    }
                });
            }
        });

    }

    private void init() {
        customFunctionList = new ArrayList<>();
        customFunctionProductList = new ArrayList<>();
    }

    private void getIntentData() {
        binding.titleTv.setText(getIntent().getStringExtra(SharedPref.CUSTOM_FUNCTION_NAME));
    }

    public void addCustomFunctionFile(View view) {
        startActivity(new Intent(CustomFunctionActivity.this, CustomFunctionFileNameActivity.class)
                .putExtra(SharedPref.CUSTOM_FUNCTION_NAME, getIntent().getStringExtra(SharedPref.CUSTOM_FUNCTION_NAME)));
    }

    public void onBackCustomFunction(View view) {
        onBackPressed();
    }

    public void onSettingsFromCustomFunction(View view) {
        startActivity(new Intent(CustomFunctionActivity.this, CustomFunctionSettingsActivity.class));
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
                    startActivity(new Intent(CustomFunctionActivity.this, CustomFunctionDetailsActivity.class)
                            .putExtra(SharedPref.FILE_NAME, customFunctionList.get(position).getFileName())
                            .putExtra(SharedPref.DATE, customFunctionList.get(position).getDate())
                            .putExtra(SharedPref.ID, String.valueOf(customFunctionList.get(position).id))
                            .putExtra(SharedPref.EDITABLE, "true"));


                    break;

                case ItemTouchHelper.LEFT:
                    deleteItem(position);
                    Toast.makeText(CustomFunctionActivity.this, "File deleted", Toast.LENGTH_SHORT).show();
                    break;
            }

        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(CustomFunctionActivity.this, c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(CustomFunctionActivity.this, R.color.green))
                    .addSwipeRightActionIcon(R.drawable.ic_baseline_edit_24)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(CustomFunctionActivity.this, R.color.red))
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
                    .setActionIconTint(ContextCompat.getColor(recyclerView.getContext(), android.R.color.white))
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    private void deleteItem(final int position) {

        deleteProduct(customFunctionList.get(position).getFileName());
        MasterExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                CustomFunction_DB.getInstance(getApplicationContext()).CustomFunctionDao().deleteCustomFunctionData(customFunctionList.get(position));
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
                customFunctionProductList = CustomFunction_DB.getInstance(CustomFunctionActivity.this).CustomFunctionProductDao().loadAllproduct(fileName);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MasterExecutor.getInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i < customFunctionProductList.size(); i++) {
                                    CustomFunction_DB.getInstance(CustomFunctionActivity.this).CustomFunctionProductDao().deleteProduct(customFunctionProductList.get(i));
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