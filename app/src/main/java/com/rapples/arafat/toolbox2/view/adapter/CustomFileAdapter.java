package com.rapples.arafat.toolbox2.view.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rapples.arafat.toolbox2.Database.Acquisition_DB;
import com.rapples.arafat.toolbox2.Database.MasterExecutor;
import com.rapples.arafat.toolbox2.R;
import com.rapples.arafat.toolbox2.model.DataAcquisition;
import com.rapples.arafat.toolbox2.model.Product;
import com.rapples.arafat.toolbox2.util.SharedPref;
import com.rapples.arafat.toolbox2.view.activity.DataAcquisitionDetailsActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CustomFileAdapter extends RecyclerView.Adapter<CustomFileAdapter.ViewHolder> {

    private List<DataAcquisition> dataAcquisitionList;
    private List<Product> productList;
    private Context context;

    public CustomFileAdapter(List<DataAcquisition> dataAcquisitionList, Context context) {
        this.dataAcquisitionList = dataAcquisitionList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_data_acquisition_design, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final DataAcquisition dataAcquisition = dataAcquisitionList.get(position);

        holder.fileNameTv.setText(dataAcquisition.getFileName());
        holder.dateTv.setText(dataAcquisition.getDate());
        holder.iconTv.setText("" + dataAcquisition.getFileName().charAt(0) + "F");
        setValueCount(holder, dataAcquisition.getFileName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, DataAcquisitionDetailsActivity.class)
                        .putExtra(SharedPref.FILE_NAME, dataAcquisition.getFileName())
                        .putExtra(SharedPref.DATE, dataAcquisition.getDate())
                        .putExtra(SharedPref.ID,String.valueOf(dataAcquisition.id))   );

            }
        });
    }

    private void setValueCount(final ViewHolder holder, final String fileName) {
        productList = new ArrayList<>();
        productList.clear();
        MasterExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                productList = Acquisition_DB.getInstance(context).ProductDao().loadAllproduct(fileName);
                holder.totalCountTv.setText(productList.size() + " Values");
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataAcquisitionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView fileNameTv, totalCountTv, dateTv, iconTv;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            fileNameTv = itemView.findViewById(R.id.fileNameTv);
            totalCountTv = itemView.findViewById(R.id.valueCountTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            iconTv = itemView.findViewById(R.id.iconTV);

        }
    }
}
