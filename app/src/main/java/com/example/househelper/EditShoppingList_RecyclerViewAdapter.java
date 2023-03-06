package com.example.househelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EditShoppingList_RecyclerViewAdapter extends RecyclerView.Adapter<EditShoppingList_RecyclerViewAdapter.MyViewHolder>{

    Context context;
    ArrayList<ProductModel> productModels;

    public EditShoppingList_RecyclerViewAdapter(Context context, ArrayList<ProductModel> productModel){
        this.context = context;
        this.productModels = productModel;
    }

    @NonNull
    @Override
    public EditShoppingList_RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.product_list_row, parent, false);
        return new EditShoppingList_RecyclerViewAdapter.MyViewHolder(view);
    }


    public interface OnItemClickListener {
        void onItemClick(ProductModel item);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull EditShoppingList_RecyclerViewAdapter.MyViewHolder holder, int position) {
        ProductModel productModel = productModels.get(position);

        holder.row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(productModel);
                productModel.setIs_bought(!productModel.getIs_bought());
                holder.is_bought.setChecked(!holder.is_bought.isChecked());
            }
        });

        holder.product.setText(productModel.getProduct());
        holder.is_bought.setChecked(productModel.getIs_bought());
    }

    @Override
    public int getItemCount() {
        return productModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView product;
        CheckBox is_bought;
        CardView row;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            product = itemView.findViewById(R.id.product);
            is_bought = itemView.findViewById(R.id.is_bought);
            row = itemView.findViewById(R.id.product_list_row);
        }
    }
}
