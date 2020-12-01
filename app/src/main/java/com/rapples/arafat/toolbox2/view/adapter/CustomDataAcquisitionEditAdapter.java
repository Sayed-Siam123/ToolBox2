package com.rapples.arafat.toolbox2.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rapples.arafat.toolbox2.Database.Acquisition_DB;
import com.rapples.arafat.toolbox2.Database.MasterExecutor;
import com.rapples.arafat.toolbox2.R;
import com.rapples.arafat.toolbox2.model.Product;
import com.rapples.arafat.toolbox2.util.SharedPref;

import java.util.List;

public class CustomDataAcquisitionEditAdapter extends RecyclerView.Adapter<CustomDataAcquisitionEditAdapter.ViewHolder> {

    List<Product> productList;
    Context context;
    String fileName;
    private SharedPreferences sharedPreferences;
    private boolean quantity;

    public CustomDataAcquisitionEditAdapter(List<Product> productList, Context context,String fileName) {
        this.productList = productList;
        this.context = context;
        this.fileName = fileName;
        sharedPreferences = context.getSharedPreferences(SharedPref.SETTING_PREFERENCE, Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_data_acquisition_edit_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        quantity = sharedPreferences.getBoolean(SharedPref.QUANTITY_FIELD, false);

        final Product product = productList.get(position);
        holder.barcode.setText(product.getBarcode());
        holder.description.setText(product.getDescription());

        if (quantity) {
            holder.quantityLayout.setVisibility(View.VISIBLE);
        } else {
            holder.quantityLayout.setVisibility(View.GONE);
        }
        holder.quantity.setText(product.getQuantity());

        Log.d("sajib", "onBindViewHolder: "+product.getQuantity());


        holder.increaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.quantity.setText(Integer.parseInt(holder.quantity.getText().toString()) + 1 + "");

                final Product item = new Product(product.getId(),fileName, product.getBarcode(), product.getDescription(),Integer.parseInt(holder.quantity.getText().toString()) + 1 + "");

                MasterExecutor.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        Acquisition_DB.getInstance(context.getApplicationContext()).ProductDao().updateProduct(item);
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });
                    }
                });
            }
        });

        holder.decreaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(holder.quantity.getText().toString()) > 1) {
                    holder.quantity.setText(Integer.parseInt(holder.quantity.getText().toString()) - 1 + "");
                    final Product item = new Product(product.getId(),fileName, product.getBarcode(), product.getDescription(),Integer.parseInt(holder.quantity.getText().toString()) - 1 + "");

                    MasterExecutor.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            Acquisition_DB.getInstance(context.getApplicationContext()).ProductDao().updateProduct(item);
                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                }
                            });
                        }
                    });

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
        private LinearLayout quantityLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            barcode = itemView.findViewById(R.id.barcodeTv);
            description = itemView.findViewById(R.id.descriptionTv);
            quantity = itemView.findViewById(R.id.quantityTv);
            increaseBtn = itemView.findViewById(R.id.incrementRl);
            decreaseBtn = itemView.findViewById(R.id.decrementRL);
            quantityLayout = itemView.findViewById(R.id.quantityLL);
        }
    }
}
