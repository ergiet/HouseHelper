package com.example.househelper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

public class Balance_RecyclerViewAdapter extends RecyclerView.Adapter<Balance_RecyclerViewAdapter.MyViewHolder> {

    Context context;
    ArrayList<BalanceListModel> balanceListModels;

    public Balance_RecyclerViewAdapter(Context context, ArrayList<BalanceListModel> balanceListModels){
        this.context = context;
        this.balanceListModels = balanceListModels;

        // Sort the list in ascending order by date
        Collections.sort(balanceListModels, new Comparator<BalanceListModel>() {
            @Override
            public int compare(BalanceListModel model1, BalanceListModel model2) {
                return model1.getDate().compareTo(model2.getDate());
            }
        });

        // Add current balance field
        double currentBalance = 0;
        for (BalanceListModel model : balanceListModels) {
            currentBalance += model.getAmount();
            model.setCurrentBalance(currentBalance);
        }

        // Sort the list in descending order by date
        Collections.sort(balanceListModels, new Comparator<BalanceListModel>() {
            @Override
            public int compare(BalanceListModel model1, BalanceListModel model2) {
                return model2.getDate().compareTo(model1.getDate());
            }
        });
    }

    @NonNull
    @Override
    public Balance_RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.balance_list_row, parent, false);
        return new Balance_RecyclerViewAdapter.MyViewHolder(view);
    }

    double balance = 0;

    @Override
    public void onBindViewHolder(@NonNull Balance_RecyclerViewAdapter.MyViewHolder holder, int position) {
        Locale locale = Locale.getDefault();
        NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);
        Currency currency = Currency.getInstance(locale);

        double currentAmount = balanceListModels.get(position).getAmount();
        Drawable amountDrawable = null;
        if (currentAmount < 0) {
            holder.tvAmount.setTextColor(Color.RED);
            amountDrawable = ContextCompat.getDrawable(context, R.drawable.ic_baseline_income_24);
            holder.tvAmount.setCompoundDrawablesWithIntrinsicBounds(amountDrawable, null, null, null);

        } else {
            amountDrawable = ContextCompat.getDrawable(context, R.drawable.ic_baseline_expense_24);
            holder.tvAmount.setCompoundDrawablesWithIntrinsicBounds(amountDrawable, null, null, null);
        }

        holder.tvAmount.setCompoundDrawablesWithIntrinsicBounds(amountDrawable, null, null, null);
        holder.tvAmount.setText(numberFormat.format(currentAmount) + " " + currency.getCurrencyCode());
        holder.tvDescription.setText(balanceListModels.get(position).getDescription());
        holder.tvUsername.setText(balanceListModels.get(position).getUsername());
        Timestamp timestamp = balanceListModels.get(position).getDate();
        long seconds = timestamp.getSeconds();
        Date date = new Date(seconds * 1000);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        holder.tvDate.setText(dateFormat.format(date));

        //balance += currentAmount;
        holder.tvBalance.setText(numberFormat.format(balanceListModels.get(position).getCurrentBalance()) + " " + currency.getCurrencyCode());
    }

    @Override
    public int getItemCount() {
        return balanceListModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ConstraintLayout balance_layout;
        TextView tvAmount, tvDescription, tvUsername, tvDate, tvBalance;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            balance_layout = itemView.findViewById(R.id.balance_layout);
            tvAmount = itemView.findViewById(R.id.balance_text_amount);
            tvDescription = itemView.findViewById(R.id.balance_text_description);
            tvUsername = itemView.findViewById(R.id.balance_text_username);
            tvDate = itemView.findViewById(R.id.balance_text_date);
            tvBalance = itemView.findViewById(R.id.balance_text_balance);
        }
    }
}