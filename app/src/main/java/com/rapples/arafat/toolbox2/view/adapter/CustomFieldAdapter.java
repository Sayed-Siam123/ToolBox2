package com.rapples.arafat.toolbox2.view.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rapples.arafat.toolbox2.OnFieldChangeListener;
import com.rapples.arafat.toolbox2.R;
import com.rapples.arafat.toolbox2.model.Field;

import java.util.ArrayList;
import java.util.List;

public class CustomFieldAdapter extends RecyclerView.Adapter<CustomFieldAdapter.ViewHolder> {

    List<Field> fieldList;
    List<Field> fieldDataList = new ArrayList<>();
    Context context;
    ArrayAdapter<String> adapter;
    OnFieldChangeListener onFieldChangeListener;

    public CustomFieldAdapter(List<Field> fieldList, Context context,OnFieldChangeListener onFieldChangeListener) {
        this.fieldList = fieldList;
        this.context = context;
        this.onFieldChangeListener = onFieldChangeListener;
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
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {


        Field field = fieldList.get(position);
        fieldDataList.add(field);

        holder.fieldName.setText(field.getFieldName());
        holder.fieldType.setAdapter(adapter);

        holder.fieldType.getSelectedItem().toString();
        holder.fieldCount.setText("Field "+String.valueOf(position+1));

        holder.fieldName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                fieldDataList.get(position).setFieldName(holder.fieldName.getText().toString());
                fieldDataList.get(position).setFieldType(holder.fieldType.getSelectedItem().toString());

                onFieldChangeListener.updateFieldList(fieldDataList);
            }
        });

        holder.setIsRecyclable(false);


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
