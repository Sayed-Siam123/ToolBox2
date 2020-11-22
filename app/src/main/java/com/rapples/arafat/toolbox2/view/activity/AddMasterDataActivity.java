package com.rapples.arafat.toolbox2.view.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rapples.arafat.toolbox2.R;
import com.rapples.arafat.toolbox2.databinding.ActivityAddMasterdataBinding;

import java.io.FileNotFoundException;

public class AddMasterDataActivity extends AppCompatActivity implements View.OnClickListener {

    FloatingActionButton submit;
    ImageView cameraPicker,galleryPicker;
    private ActivityAddMasterdataBinding binding;
    private int PROFILE_IMAGE_REQ_CODE = 101;
    private int GALLERY_IMAGE_REQ_CODE = 102;
    private int CAMERA_IMAGE_REQ_CODE = 103;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_masterdata);
        submit = binding.fabAdd;
        cameraPicker = binding.cameraPicker;
        galleryPicker = binding.galleryPicker;
        submit.setOnClickListener(this);
        cameraPicker.setOnClickListener(this);
        galleryPicker.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.fabAdd){
            Log.d("TAG", "onClick: Data Submitted");
        }

        else if(v.getId() == R.id.cameraPicker){
            Log.d("TAG", "onClick: Camera");

            ImagePicker.Companion.with(this).cameraOnly().start();
//            Intent in = new Intent(Intent.ACTION_CAMERA_BUTTON);
//            in.setType("camera/*");
//            startActivityForResult(in, CAMERA_IMAGE_REQ_CODE);// start
        }

        else if(v.getId() == R.id.galleryPicker){
            Log.d("TAG", "onClick: Gallery");

        }
    }

    protected void onActivityResult(int requestcode, int resultcode, Intent imagereturnintent) {
        super.onActivityResult(requestcode, resultcode, imagereturnintent);
        if(requestcode == Activity.RESULT_OK){
            Log.d("TAG", "onActivityResult: gotit");
        }
    }

    public void onBack(View view) {
        onBackPressed();
    }
}