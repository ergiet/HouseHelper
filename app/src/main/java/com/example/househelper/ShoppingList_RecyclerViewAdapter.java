package com.example.househelper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

public class ShoppingList_RecyclerViewAdapter extends RecyclerView.Adapter<ShoppingList_RecyclerViewAdapter.MyViewHolder> {

    Context context;
    ArrayList<ShoppingListModel> shoppingListModels;

    public ShoppingList_RecyclerViewAdapter(Context context, ArrayList<ShoppingListModel> shoppingListModels){
        this.context = context;
        this.shoppingListModels = shoppingListModels;
    }

    @NonNull
    @Override
    public ShoppingList_RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.shopping_list_row, parent, false);
        return new ShoppingList_RecyclerViewAdapter.MyViewHolder(view);
    }

    public interface OnItemClickListener {
        void onItemClick(ShoppingListModel item);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {

        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingList_RecyclerViewAdapter.MyViewHolder holder, int position) {
        ShoppingListModel shoppingListModel = shoppingListModels.get(position);

        holder.row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(shoppingListModel);
            }
        });

        holder.username.setText(shoppingListModel.getUsername());
        holder.name.setText(shoppingListModel.getName());
        Timestamp timestamp = shoppingListModels.get(position).getDate();
        long seconds = timestamp.getSeconds();
        Date date = new Date(seconds * 1000);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        holder.date.setText(dateFormat.format(date));
        holder.isPrivate.setText(shoppingListModel.getIs_private().toString());
    }

    @Override
    public int getItemCount() {
        return shoppingListModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView username;
        TextView name;
        TextView date;
        TextView isPrivate;

        CardView row;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.shopping_list_username);
            name = itemView.findViewById(R.id.shopping_list_name);
            date = itemView.findViewById(R.id.shopping_list_date);
            isPrivate = itemView.findViewById(R.id.shopping_list_is_private);
            row = itemView.findViewById(R.id.shopping_list_row);
        }
    }
}