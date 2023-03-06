package com.example.househelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ListCars_RecyclerViewAdapter extends RecyclerView.Adapter<ListCars_RecyclerViewAdapter.MyViewHolder> {

    Context context;
    ArrayList<CarModel> carModels;

    public ListCars_RecyclerViewAdapter(Context context, ArrayList<CarModel> carModels){
        this.context = context;
        this.carModels = carModels;
    }

    @NonNull
    @Override
    public ListCars_RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.car_list_row, parent, false);
        return new ListCars_RecyclerViewAdapter.MyViewHolder(view);
    }

    public interface OnItemClickListener {
        void onItemClick(CarModel item);
    }

    private ListCars_RecyclerViewAdapter.OnItemClickListener listener;

    public void setOnItemClickListener(ListCars_RecyclerViewAdapter.OnItemClickListener listener) {

        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull ListCars_RecyclerViewAdapter.MyViewHolder holder, int position) {
        CarModel carModel = carModels.get(position);

        holder.row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(carModel);
            }
        });

        Timestamp first_reg_timestamp = carModels.get(position).getFirstRegistration();
        long first_reg_seconds = first_reg_timestamp.getSeconds();
        Date first_reg_date = new Date(first_reg_seconds * 1000);

        Timestamp insurance_timestamp = carModels.get(position).getInsurance();
        long insurance_seconds = insurance_timestamp.getSeconds();
        Date insurance_date = new Date(insurance_seconds * 1000);

        Timestamp mot_timestamp = carModels.get(position).getMot();
        long mot_seconds = mot_timestamp.getSeconds();
        Date mot_date = new Date(mot_seconds * 1000);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        holder.make_model.setText(carModel.getMake() + " " + carModel.getModel());
        holder.plate.setText(carModel.getPlate());
        holder.vin.setText(carModel.getVin());
        holder.firstRegistration.setText(dateFormat.format(first_reg_date));
        holder.insurance.setText(dateFormat.format(insurance_date));
        holder.mot.setText(dateFormat.format(mot_date));
    }

    @Override
    public int getItemCount() {
        return carModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView make_model;
        TextView vin;
        TextView plate;
        TextView firstRegistration;
        TextView mot;
        TextView insurance;
        CardView row;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            make_model = itemView.findViewById(R.id.car_make_model);
            vin = itemView.findViewById(R.id.car_vin);
            plate = itemView.findViewById(R.id.car_plate);
            firstRegistration = itemView.findViewById(R.id.car_first_registration_date);
            mot = itemView.findViewById(R.id.car_mot_date);
            insurance = itemView.findViewById(R.id.car_insurance_date);
            row = itemView.findViewById(R.id.car_list_row);
        }
    }
}
