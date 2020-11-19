package com.rapples.arafat.toolbox2.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rapples.arafat.toolbox2.R;
import com.rapples.arafat.toolbox2.databinding.ActivityAddMasterDataBinding;

public class AddMasterDataActivity extends AppCompatActivity implements View.OnClickListener {

    FloatingActionButton submit;
    private ActivityAddMasterDataBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_master_data);
        submit = binding.fabAdd;
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.fabAdd){
            Log.d("TAG", "onClick: Data Submitted");
        }
    }

    public void onBack(View view) {
        onBackPressed();
    }
}