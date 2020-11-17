package com.rapples.arafat.toolbox2.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.rapples.arafat.toolbox2.R;
import com.rapples.arafat.toolbox2.databinding.ActivityMasterDataBinding;
import com.rapples.arafat.toolbox2.model.Masterdata;
import com.rapples.arafat.toolbox2.view.adapter.CustomMasterDataAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MasterData extends AppCompatActivity {

    private ActivityMasterDataBinding binding;
    List<Masterdata> masterDataList;
    CustomMasterDataAdapter adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_master_data);

        init();

        setDataIntoMaster();


    }

    private void setDataIntoMaster() {

        Masterdata[] myListData = new Masterdata[] {
                new Masterdata("Email", "141012345" ,android.R.drawable.ic_dialog_email),
                new Masterdata("Info", "141012345" ,android.R.drawable.ic_dialog_info),
                new Masterdata("Delete", "141012345" ,android.R.drawable.ic_delete),
                new Masterdata("Dialer", "141012345" ,android.R.drawable.ic_dialog_dialer),
                new Masterdata("Alert", "141012345" ,android.R.drawable.ic_dialog_alert),
                new Masterdata("Map", "141012345" ,android.R.drawable.ic_dialog_map),
                new Masterdata("Email", "141012345" ,android.R.drawable.ic_dialog_email),
                new Masterdata("Info", "141012345" ,android.R.drawable.ic_dialog_info),
                new Masterdata("Delete", "141012345" ,android.R.drawable.ic_delete),
                new Masterdata("Dialer", "141012345" ,android.R.drawable.ic_dialog_dialer),
                new Masterdata("Alert", "141012345" ,android.R.drawable.ic_dialog_alert),
                new Masterdata("Map", "141012345" ,android.R.drawable.ic_dialog_map),
        };

        recyclerView = (RecyclerView) findViewById(R.id.masterDataRecyclerview);
        CustomMasterDataAdapter adapter = new CustomMasterDataAdapter(Arrays.asList(myListData),this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
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

                    Snackbar.make(recyclerView, "Edit "+position, Snackbar.LENGTH_LONG)
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    Log.d("TAG", "onClick: Edited row position "+position);

                                }
                            }).show();
                    break;

                    case ItemTouchHelper.LEFT:
                    Snackbar.make(recyclerView,  "Delete "+position, Snackbar.LENGTH_LONG)
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Log.d("TAG", "onClick: Deleted row position "+position);
                                }
                            }).show();

                    break;
            }

        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(MasterData.this, c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(MasterData.this, R.color.green))
                    .addSwipeRightActionIcon(R.drawable.ic_baseline_edit_24)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(MasterData.this, R.color.red))
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
                    .setActionIconTint(ContextCompat.getColor(recyclerView.getContext(), android.R.color.white))
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    private void init() {
        binding.masterDataRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        masterDataList = new ArrayList<>();
    }
}