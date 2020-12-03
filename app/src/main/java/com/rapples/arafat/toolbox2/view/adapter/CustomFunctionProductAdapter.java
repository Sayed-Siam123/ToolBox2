package com.rapples.arafat.toolbox2.view.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rapples.arafat.toolbox2.R;
import com.rapples.arafat.toolbox2.model.CustomFunctionProduct;
import com.rapples.arafat.toolbox2.model.Field;
import com.rapples.arafat.toolbox2.util.SharedPref;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CustomFunctionProductAdapter extends RecyclerView.Adapter<CustomFunctionProductAdapter.ViewHolder> {

    private List<CustomFunctionProduct> customFunctionProductList;
    private Context context;
    private SharedPreferences sharedPreferences;
    private List<Field> fieldList = new ArrayList<>();
    private String fieldListInString;


    public CustomFunctionProductAdapter(List<CustomFunctionProduct> customFunctionProductList, Context context) {
        this.customFunctionProductList = customFunctionProductList;
        this.context = context;
        sharedPreferences = context.getSharedPreferences(SharedPref.SETTING_PREFERENCE, Context.MODE_PRIVATE);
        fieldListInString = sharedPreferences.getString(SharedPref.CUSTOM_FIELD_LIST, "");

        if (!fieldListInString.isEmpty()) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Field>>() {
            }.getType();
            fieldList = gson.fromJson(fieldListInString, type);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_custom_function_product_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        CustomFunctionProduct customFunctionProduct = customFunctionProductList.get(position);

        holder.field1LL.setVisibility(View.VISIBLE);
        holder.field1title.setText(fieldList.get(0).getFieldName());
        holder.field1value.setText(customFunctionProduct.getField1());

        if (fieldList.size() > 1) {
            holder.field2LL.setVisibility(View.VISIBLE);
            holder.field2title.setText(fieldList.get(1).getFieldName());
            holder.field2value.setText(customFunctionProduct.getField2());
        }

        if (fieldList.size() > 2) {
            holder.field3LL.setVisibility(View.VISIBLE);
            holder.field3title.setText(fieldList.get(2).getFieldName());
            holder.field3value.setText(customFunctionProduct.getField3());
        }
        if (fieldList.size() > 3) {
            holder.field4LL.setVisibility(View.VISIBLE);
            holder.field4title.setText(fieldList.get(3).getFieldName());
            holder.field4value.setText(customFunctionProduct.getField4());
        }
        if (fieldList.size() > 4) {
            holder.field5LL.setVisibility(View.VISIBLE);
            holder.field5title.setText(fieldList.get(4).getFieldName());
            holder.field5value.setText(customFunctionProduct.getField5());
        }


    }

    @Override
    public int getItemCount() {
        return customFunctionProductList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView field1title, field2title, field3title, field4title, field5title;
        private TextView field1value, field2value, field3value, field4value, field5value;
        private LinearLayout field1LL, field2LL, field3LL, field4LL, field5LL;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            field1title = itemView.findViewById(R.id.field1titleTv);
            field2title = itemView.findViewById(R.id.field2titleTv);
            field3title = itemView.findViewById(R.id.field3titleTv);
            field4title = itemView.findViewById(R.id.field4titleTv);
            field5title = itemView.findViewById(R.id.field5titleTv);

            field1value = itemView.findViewById(R.id.field1Value);
            field2value = itemView.findViewById(R.id.field2Value);
            field3value = itemView.findViewById(R.id.field3Value);
            field4value = itemView.findViewById(R.id.field4Value);
            field5value = itemView.findViewById(R.id.field5Value);

            field1LL = itemView.findViewById(R.id.field1Ll);
            field2LL = itemView.findViewById(R.id.field2Ll);
            field3LL = itemView.findViewById(R.id.field3Ll);
            field4LL = itemView.findViewById(R.id.field4Ll);
            field5LL = itemView.findViewById(R.id.field5Ll);


        }
    }
}
