package com.rapples.arafat.toolbox2.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rapples.arafat.toolbox2.R;
import com.rapples.arafat.toolbox2.model.Product;

import java.util.List;

public class LastDataAcquisitionAdapter extends RecyclerView.Adapter<LastDataAcquisitionAdapter.ViewHolder> {

    List<Product> productList;
    Context context;

    public LastDataAcquisitionAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_data_acquisition_product_design,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Product product = productList.get(position);
        holder.barcode.setText(product.getBarcode());
        holder.description.setText(product.getDescription());
        holder.barcodeTitle.setText("Last barcode");



    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView barcode,description, barcodeTitle, descriptionTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            barcode = itemView.findViewById(R.id.barcodeTv);
            description = itemView.findViewById(R.id.descriptionTv);
            barcodeTitle = itemView.findViewById(R.id.barcodeTitleTv);

        }
    }
}
