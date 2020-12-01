package com.rapples.arafat.toolbox2.view.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rapples.arafat.toolbox2.Database.Acquisition_DB;
import com.rapples.arafat.toolbox2.Database.MasterExecutor;
import com.rapples.arafat.toolbox2.R;
import com.rapples.arafat.toolbox2.model.Product;
import com.rapples.arafat.toolbox2.util.SharedPref;
import com.rapples.arafat.toolbox2.view.activity.DataAcquisitionActivity;

import java.util.List;

public class CustomDataAcquisitionAdapter extends RecyclerView.Adapter<CustomDataAcquisitionAdapter.ViewHolder> {

    List<Product> productList;
    Context context;
    SharedPreferences sharedPreferences;
    boolean quantityStatus;

    public CustomDataAcquisitionAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
        sharedPreferences = context.getSharedPreferences(SharedPref.SETTING_PREFERENCE,Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_data_acquisition_product_design,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        quantityStatus = sharedPreferences.getBoolean(SharedPref.QUANTITY_FIELD,false);

        Product product = productList.get(position);
        Log.d("sajib", "onBindViewHolder: "+product.getQuantity()+" sd"+product.getBarcode());
        holder.barcode.setText(product.getBarcode());
        holder.description.setText(product.getDescription());
        holder.quantityTv.setText(product.getQuantity());
        if(quantityStatus){

            holder.quantityLL.setVisibility(View.VISIBLE);
        }else{
            holder.quantityLL.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView barcode,description, quantityTv;
        private LinearLayout quantityLL;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            barcode = itemView.findViewById(R.id.barcodeTv);
            description = itemView.findViewById(R.id.descriptionTv);
            quantityTv = itemView.findViewById(R.id.quantityTv);
            quantityLL = itemView.findViewById(R.id.quantityLiL);
        }
    }
}
