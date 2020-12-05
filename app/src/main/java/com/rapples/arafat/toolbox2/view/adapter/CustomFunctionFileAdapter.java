package com.rapples.arafat.toolbox2.view.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.rapples.arafat.toolbox2.Database.Acquisition_DB;
import com.rapples.arafat.toolbox2.Database.CustomFunction_DB;
import com.rapples.arafat.toolbox2.Database.MasterExecutor;
import com.rapples.arafat.toolbox2.R;
import com.rapples.arafat.toolbox2.model.CustomFunction;
import com.rapples.arafat.toolbox2.model.CustomFunctionProduct;
import com.rapples.arafat.toolbox2.model.DataAcquisition;
import com.rapples.arafat.toolbox2.model.Product;
import com.rapples.arafat.toolbox2.util.SharedPref;
import com.rapples.arafat.toolbox2.view.activity.CustomFunctionDetailsActivity;
import com.rapples.arafat.toolbox2.view.activity.DataAcquisitionDetailsActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class CustomFunctionFileAdapter extends RecyclerView.Adapter<CustomFunctionFileAdapter.ViewHolder> {

    private List<CustomFunction> customFunctionList;
    private List<CustomFunctionProduct> customFunctionProductList;
    private Context context;

    public CustomFunctionFileAdapter(List<CustomFunction> customFunctionList, Context context) {
        this.customFunctionList = customFunctionList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_data_acquisition_design, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final CustomFunction customFunction = customFunctionList.get(position);

        String title ="";
        holder.fileNameTv.setText(customFunction.getFileName());
        holder.dateTv.setText(customFunction.getDate());
        String[] words = customFunction.getFileName().split("\\W+");
        for(int i=0;i<words.length;i++){
            title += words[i].toUpperCase().charAt(0);
        }
        holder.iconTv.setText(title);
        setValueCount(holder, customFunction.getFileName());

        int[] colorList = new int[]{R.color.blue, R.color.purple, R.color.green,R.color.orange,R.color.red,R.color.darkblue,
                R.color.darkgreen,R.color.darkpurple,R.color.orange,};

        int randomNum = ThreadLocalRandom.current().nextInt(0, 8);

        holder.iconTv.setBackgroundTintList(context.getResources().getColorStateList(colorList[randomNum]));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, CustomFunctionDetailsActivity.class)
                        .putExtra(SharedPref.FILE_NAME, customFunction.getFileName())
                        .putExtra(SharedPref.DATE, customFunction.getDate())
                        .putExtra(SharedPref.ID,String.valueOf(customFunction.id))
                        .putExtra(SharedPref.EDITABLE,"false"));


            }
        });
    }

    private void setValueCount(final ViewHolder holder, final String fileName) {
        customFunctionProductList = new ArrayList<>();
        customFunctionProductList.clear();
        MasterExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                customFunctionProductList = CustomFunction_DB.getInstance(context).CustomFunctionProductDao().loadAllproduct(fileName);
                holder.totalCountTv.setText(customFunctionProductList.size() + " Values");
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
        return customFunctionList.size();
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
