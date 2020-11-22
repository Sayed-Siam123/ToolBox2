package com.rapples.arafat.toolbox2.view.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.rapples.arafat.toolbox2.R;
import com.rapples.arafat.toolbox2.model.Masterdata;

import java.util.List;

public class CustomMasterDataAdapter extends RecyclerView.Adapter<CustomMasterDataAdapter.ViewHolder> {
    List<Masterdata> masterDataList;
    Context context;

    public CustomMasterDataAdapter(List<Masterdata> masterDataList, Context context) {
        this.masterDataList = masterDataList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_masterdata_item_design, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Masterdata masterData = masterDataList.get(position);


        holder.productDescriptionTv.setText(masterData.getDescription());
        holder.barCodeTv.setText(masterData.getBarcode());
        if(masterData.getPrice() != null){
            holder.priceTv.setText(masterData.getPrice());
        }

        if (masterData.getImage() != null) {
            holder.imageView.setImageBitmap(decodeBase64(masterData.getImage()));
        }

        holder.LinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Clicked " + masterData.getDescription(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return masterDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView productDescriptionTv;
        private TextView barCodeTv;
        private TextView priceTv;
        public ImageView imageView;
        public LinearLayout LinearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productDescriptionTv = itemView.findViewById(R.id.prod_desc);
            barCodeTv = itemView.findViewById(R.id.barcodeText);
            priceTv = itemView.findViewById(R.id.priceText);
            imageView = itemView.findViewById(R.id.iconImage);
            LinearLayout = itemView.findViewById(R.id.linearLayout);

        }
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}
