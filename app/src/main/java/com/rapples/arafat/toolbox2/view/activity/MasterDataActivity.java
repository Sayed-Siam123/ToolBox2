package com.rapples.arafat.toolbox2.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.rapples.arafat.toolbox2.Database.MasterData_DB;
import com.rapples.arafat.toolbox2.Database.MasterExecutor;
import com.rapples.arafat.toolbox2.R;
import com.rapples.arafat.toolbox2.databinding.ActivityMasterDataBinding;
import com.rapples.arafat.toolbox2.model.Masterdata;
import com.rapples.arafat.toolbox2.view.adapter.CustomMasterDataAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MasterDataActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityMasterDataBinding binding;
    private List<Masterdata> masterDataList;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private  boolean isScannerOpenTrue = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_master_data);
        fab = findViewById(R.id.fab);

        fab.setOnClickListener(this);


        init();

        setDataIntoMaster();


    }

    @Override
    protected void onResume() {
        super.onResume();
        setDataIntoMaster();
    }

    private void setDataIntoMaster() {

        MasterExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                masterDataList  =  MasterData_DB.getInstance(getApplicationContext()).MasterdataDao().loadAllData();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView = (RecyclerView) findViewById(R.id.masterDataRecyclerview);
                        CustomMasterDataAdapter adapter = new CustomMasterDataAdapter(masterDataList,MasterDataActivity.this);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(MasterDataActivity.this));
                        recyclerView.setAdapter(adapter);

                        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
                        itemTouchHelper.attachToRecyclerView(recyclerView);


                    }
                });
            }
        });


    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            final int position = viewHolder.getAdapterPosition();

            switch (direction) {
                case ItemTouchHelper.RIGHT:
                    startActivity(new Intent(MasterDataActivity.this,EditMasterdataActivity.class)
                            .putExtra("barcode",masterDataList.get(position).getBarcode())
                            .putExtra("description",masterDataList.get(position).getDescription())
                            .putExtra("price",masterDataList.get(position).getPrice())
                            .putExtra("image",masterDataList.get(position).getImage())
                            .putExtra("id",String.valueOf(masterDataList.get(position).getId()))

                    );


                    break;

                    case ItemTouchHelper.LEFT:
                    Snackbar.make(recyclerView,  "Delete product", Snackbar.LENGTH_LONG)
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            }).show();

                    deleateElement(position);

                    break;
            }

        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(MasterDataActivity.this, c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(MasterDataActivity.this, R.color.green))
                    .addSwipeRightActionIcon(R.drawable.ic_baseline_edit_24)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(MasterDataActivity.this, R.color.red))
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
                    .setActionIconTint(ContextCompat.getColor(recyclerView.getContext(), android.R.color.white))
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    private void deleateElement(final int position) {

        MasterExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                MasterData_DB.getInstance(getApplicationContext()).MasterdataDao().delete(masterDataList.get(position));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       setDataIntoMaster();
                    }
                });
            }
        });

    }

    private void init() {
        binding.masterDataRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        masterDataList = new ArrayList<>();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.fab){
            Log.d("TAG", "onClick: ADD");
            startActivity(new Intent(MasterDataActivity.this, AddMasterDataActivity.class));
        }

    }

    public void onBack(View view) {
        onBackPressed();
    }

    public void openSearchModule(View view) {
        binding.searchToolBar.setVisibility(View.VISIBLE);
        binding.defaultToolBar.setVisibility(View.GONE);
    }

    public void openEditScanner(View view) {
        binding.edittextLL.setVisibility(View.GONE);
        binding.sannnerLL.setVisibility(View.VISIBLE);
        binding.barCodeFromSCET.requestFocus();
        isScannerOpenTrue = true;
        disableFocus();
        hideKeyboard(this);
    }

    public void openEditableKeyboard(View view) {
        binding.edittextLL.setVisibility(View.VISIBLE);
        binding.sannnerLL.setVisibility(View.GONE);
        binding.barCodeET.requestFocus();
        isScannerOpenTrue = false;
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
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
}