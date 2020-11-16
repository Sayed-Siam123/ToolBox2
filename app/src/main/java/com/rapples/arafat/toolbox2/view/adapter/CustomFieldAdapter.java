package com.rapples.arafat.toolbox2.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rapples.arafat.toolbox2.R;
import com.rapples.arafat.toolbox2.model.Field;

import java.util.List;

public class CustomFieldAdapter extends RecyclerView.Adapter<CustomFieldAdapter.ViewHolder> {

    List<Field> fieldList;
    Context context;
    ArrayAdapter<String> adapter;

    public CustomFieldAdapter(List<Field> fieldList, Context context) {
        this.fieldList = fieldList;
        this.context = context;
        String[] items = new String[]{"Barcode", "Numeric", "TextField"};
        adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, items);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_field_design,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Field field = fieldList.get(position);

        holder.fieldName.setText(field.getFieldName());
        holder.fieldType.setAdapter(adapter);
        holder.fieldCount.setText("Field "+String.valueOf(position+1));


    }

    @Override
    public int getItemCount() {
        return fieldList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private Spinner fieldType;
        private EditText fieldName;
        private TextView fieldCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fieldName = itemView.findViewById(R.id.fieldNameEt);
            fieldType = itemView.findViewById(R.id.fieldTypeDropdown);
            fieldCount = itemView.findViewById(R.id.fieldCountTv);

        }
    }
}
