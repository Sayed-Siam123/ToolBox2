package com.rapples.arafat.toolbox2.view.adapter;

import android.content.Context;
import android.os.Build;
import android.util.Log;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_masterdata_item_design,parent,false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Masterdata masterData = masterDataList.get(position);

        Log.d("TAG", "onBindViewHolder: "+masterData.getImage());

        holder.titleTv.setText(masterData.getTitle());
        holder.subtitleTv.setText(masterData.getSubtitle());
        holder.imageView.setImageResource(masterData.getImage());

        holder.LinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Clicked "+ masterData.getTitle() , Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return masterDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView titleTv;
        private final TextView subtitleTv;
        public ImageView imageView;
        public LinearLayout LinearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTv = itemView.findViewById(R.id.title);
            subtitleTv = itemView.findViewById(R.id.barcodeText);
            imageView = itemView.findViewById(R.id.iconImage);
            LinearLayout = itemView.findViewById(R.id.linearLayout);

        }
    }
}
