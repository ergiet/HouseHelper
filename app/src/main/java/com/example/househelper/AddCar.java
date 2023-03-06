package com.example.househelper;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.househelper.databinding.ActivityAddCarBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddCar extends Drawer{

    ActivityAddCarBinding activityAddCarBinding;

    ArrayList<ShoppingListModel> shoppingListModels = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAddCarBinding = ActivityAddCarBinding.inflate(getLayoutInflater());
        setContentView(activityAddCarBinding.getRoot());
        allocateActivityTitle("");

        MaterialButton firstRegistrationButton = (MaterialButton) findViewById(R.id.car_first_registration);
        MaterialButton motButton = (MaterialButton) findViewById(R.id.car_mot);
        MaterialButton insuranceButton = (MaterialButton) findViewById(R.id.car_insurance);

        firstRegistrationButton.setOnClickListener(new DatePickerOnClickListener());
        motButton.setOnClickListener(new DatePickerOnClickListener());
        insuranceButton.setOnClickListener(new DatePickerOnClickListener());

        //setupShoppingListModels(startTimestamp, endTimestamp);

        SharedPreferences preferences = getSharedPreferences("user_data", MODE_PRIVATE);
        String userid_value = preferences.getString("userid", "");

        TextView tvmake =(TextView) findViewById(R.id.car_make);
        TextView tvmodel =(TextView) findViewById(R.id.car_model);
        TextView tvvin =(TextView) findViewById(R.id.car_vin);
        TextView tvplate =(TextView) findViewById(R.id.car_plate);
        TextView tvfirst_registration =(TextView) findViewById(R.id.car_first_registration);
        TextView tvmot =(TextView) findViewById(R.id.car_mot);
        TextView tvinsurance =(TextView) findViewById(R.id.car_insurance);
        boolean[] isPrivate = {false};
        CheckBox checkBox = findViewById(R.id.private_car_checkbox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isPrivate[0] = isChecked;
            }
        });

        MaterialButton addCar = (MaterialButton) findViewById(R.id.add_car_btn);
        addCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String make, model, vin, plate, first_registration_text, mot_text, insurance_text;
                make = tvmake.getText().toString();
                model = tvmodel.getText().toString();
                vin = tvvin.getText().toString();
                plate = tvplate.getText().toString();
                Date currentDate = new Date(System.currentTimeMillis());
                Timestamp insurance_timestamp = new Timestamp(currentDate);
                Timestamp mot_timestamp = new Timestamp(currentDate);
                Timestamp first_registration_timestamp = new Timestamp(currentDate);


                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

                try {
                    first_registration_text = tvfirst_registration.getText().toString();
                    Date first_registration_date = simpleDateFormat.parse(first_registration_text);
                    first_registration_timestamp = new Timestamp(first_registration_date);

                    mot_text = tvmot.getText().toString();
                    Date mot_date = simpleDateFormat.parse(mot_text);
                    mot_timestamp = new Timestamp(mot_date);

                    insurance_text = tvinsurance.getText().toString();
                    Date insurance_date = simpleDateFormat.parse(insurance_text);
                    insurance_timestamp = new Timestamp(insurance_date);
                } catch (ParseException e) {
                    Toast.makeText(getApplicationContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                }

                if(!make.equals("") && !model.equals("") && !vin.equals("") && !plate.equals("")) {
                    //Start ProgressBar first (Set visibility VISIBLE)
                    Map<String, Object> car = new HashMap<>();
                    car.put("make", make);
                    car.put("model", model);
                    car.put("vin", vin);
                    car.put("plate", plate);
                    car.put("first_registration", first_registration_timestamp);
                    car.put("mot", mot_timestamp);
                    car.put("insurance", insurance_timestamp);
                    car.put("is_private", isPrivate[0]);
                    car.put("user", userid_value);

                    db.collection("cars")
                            .add(car)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(getApplicationContext(), "Car added", Toast.LENGTH_SHORT).show();
                                    //jesli dom zostal dodany zmien household uzytkownika
//                                    Intent intent = new Intent(getApplicationContext(), HouseSettings.class);
//                                    startActivity(intent);
//                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Something failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                }else {
                    Toast.makeText(getApplicationContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    class DatePickerOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AddCar.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                            Calendar selectedDate = Calendar.getInstance();
                            selectedDate.set(year, month, day);
                            ((MaterialButton) view).setText(day + "/" + (month + 1) + "/" + year);
                        }
                    },
                    year,
                    month,
                    day
            );
            datePickerDialog.show();
        }
    }
}
