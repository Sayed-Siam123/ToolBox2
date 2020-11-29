package com.rapples.arafat.toolbox2.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rapples.arafat.toolbox2.R;
import com.rapples.arafat.toolbox2.model.Product;

import java.util.List;

public class CustomDataAcquisitionEditAdapter extends RecyclerView.Adapter<CustomDataAcquisitionEditAdapter.ViewHolder> {

    List<Product> productList;
    Context context;

    public CustomDataAcquisitionEditAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_data_acquisition_edit_design,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        Product product = productList.get(position);
        holder.barcode.setText(product.getBarcode());
        holder.description.setText(product.getDescription());
        holder.quantity.setText("1");

        holder.increaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.quantity.setText(Integer.parseInt(holder.quantity.getText().toString())+1 +"");
            }
        });

        holder.decreaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Integer.parseInt(holder.quantity.getText().toString()) >1){
                    holder.quantity.setText(Integer.parseInt(holder.quantity.getText().toString())-1 +"");
                }
            }
        });



    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView barcode;
        private TextView description;
        private TextView quantity;
        private RelativeLayout increaseBtn, decreaseBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            barcode = itemView.findViewById(R.id.barcodeTv);
            description = itemView.findViewById(R.id.descriptionTv);
            quantity = itemView.findViewById(R.id.quantityTv);
            increaseBtn = itemView.findViewById(R.id.incrementRl);
            decreaseBtn = itemView.findViewById(R.id.decrementRL);
        }
    }
}
