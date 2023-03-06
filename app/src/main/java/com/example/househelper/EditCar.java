package com.example.househelper;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.househelper.databinding.ActivityEditCarBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class EditCar extends Drawer {

    ActivityEditCarBinding activityEditCarBinding;

    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityEditCarBinding = ActivityEditCarBinding.inflate(getLayoutInflater());
        setContentView(activityEditCarBinding.getRoot());
        allocateActivityTitle("");

        TextView tvcar_make_model =(TextView) findViewById(R.id.car_make_model);
        TextView tvcar_plate =(TextView) findViewById(R.id.car_plate);
        TextView tvcar_vin =(TextView) findViewById(R.id.car_vin);
        TextView tvcar_insurance =(TextView) findViewById(R.id.car_insurance_date);
        TextView tvcar_mot =(TextView) findViewById(R.id.car_mot_date);

//        setupProductListModels();
        //Toast.makeText(getApplicationContext(), "Car ID: " + getIntent().getStringExtra("car_id"), Toast.LENGTH_SHORT).show();

        db.collection("cars").document(getIntent().getStringExtra("car_id"))
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String make = document.getString("make");
                                String model = document.getString("model");
                                String plate = document.getString("plate");
                                String vin = document.getString("vin");
                                Timestamp insurance_timestamp = document.getTimestamp("insurance");
                                long insurance_seconds = insurance_timestamp.getSeconds();
                                Date insurance_date = new Date(insurance_seconds * 1000);
                                Timestamp mot_timestamp = document.getTimestamp("mot");
                                long mot_seconds = mot_timestamp.getSeconds();
                                Date mot_date = new Date(mot_seconds * 1000);

                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

                                tvcar_make_model.setText(make + " " + model);
                                tvcar_plate.setText(plate);
                                tvcar_vin.setText(vin);
                                tvcar_insurance.setText(dateFormat.format(insurance_date));
                                tvcar_mot.setText(dateFormat.format(mot_date));
                            } else {
                                Toast.makeText(getApplicationContext(), "Document doesn't exist", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.document_retrieve_failed, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        MaterialButton car_remove_btn =(MaterialButton) findViewById(R.id.car_remove_btn);
        MaterialButton update_insurance_btn =(MaterialButton) findViewById(R.id.update_insurance_btn);
        MaterialButton update_mot_btn =(MaterialButton) findViewById(R.id.update_mot_btn);

        update_insurance_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR)+1;
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        EditCar.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                Calendar selectedDate = Calendar.getInstance();
                                selectedDate.set(year, month, day);

                                db.collection("cars").document(getIntent().getStringExtra("car_id"))
                                        .update("insurance", selectedDate.getTime())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                tvcar_insurance.setText(day + "-" + (month + 1) + "-" + year);
                                                Toast.makeText(getApplicationContext(), "Insurance expiration date updated", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        },
                        year,
                        month,
                        day
                );
                datePickerDialog.show();
            }
        });

        update_mot_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR)+1;
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        EditCar.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                Calendar selectedDate = Calendar.getInstance();
                                selectedDate.set(year, month, day);

                                db.collection("cars").document(getIntent().getStringExtra("car_id"))
                                        .update("mot", selectedDate.getTime())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                tvcar_mot.setText(day + "-" + (month + 1) + "-" + year);
                                                Toast.makeText(getApplicationContext(), "MOT expiration date updated", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        },
                        year,
                        month,
                        day
                );
                datePickerDialog.show();
            }
        });

        car_remove_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an AlertDialog to confirm the deletion
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage("Are you sure you want to remove the car?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked "Yes", so remove the car
                                removeCar(getIntent().getStringExtra("car_id"));
                                Toast.makeText(getApplicationContext(), "Car is getting removed", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog, so do nothing
                                Toast.makeText(getApplicationContext(), "Car is not gonna be removed", Toast.LENGTH_SHORT).show();
                            }
                        });
                builder.show();
            }
        });
    }
    private void removeCar(String carId) {
        db.collection("cars").document(carId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent intent = new Intent(getApplicationContext(), ListCars.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }
}
