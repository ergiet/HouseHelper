package com.example.househelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.househelper.databinding.ActivityCreateHouseBinding;
import com.example.househelper.databinding.ActivityJoinHouseBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

public class CreateHouse extends Drawer {

    ActivityCreateHouseBinding activityCreateHouseBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCreateHouseBinding = activityCreateHouseBinding.inflate(getLayoutInflater());
        setContentView(activityCreateHouseBinding.getRoot());
        allocateActivityTitle(getString(R.string.create_house));

        TextView tvcity =(TextView) findViewById(R.id.city);
        TextView tvstreet =(TextView) findViewById(R.id.street);
        TextView tvhouse_number =(TextView) findViewById(R.id.house_number);
        TextView tvapartment_number =(TextView) findViewById(R.id.apartment_number);
        TextView tvhouse_password =(TextView) findViewById(R.id.house_password);

        SharedPreferences preferences = getSharedPreferences("user_data", MODE_PRIVATE);
        String userid_value = preferences.getString("userid", "");

        MaterialButton create_house_btn =(MaterialButton) findViewById(R.id.create_house_btn);

        create_house_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String city, street, house_number, apartment_number, house_password;
                city = tvcity.getText().toString();
                street = tvstreet.getText().toString();
                house_number = tvhouse_number.getText().toString();
                apartment_number = tvapartment_number.getText().toString();
                house_password = tvhouse_password.getText().toString();

                if(!city.equals("") && !street.equals("") && !house_number.equals("") && !apartment_number.equals("") && !house_password.equals("")) {
                    //Start ProgressBar first (Set visibility VISIBLE)
                    Map<String, Object> household = new HashMap<>();
                    household.put("city", city);
                    household.put("street", street);
                    household.put("house_number", house_number);
                    household.put("apartment_number", apartment_number);
                    household.put("password", house_password);

                    db.collection("households")
                            .add(household)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(getApplicationContext(), "Household added", Toast.LENGTH_SHORT).show();
                                    //jesli dom zostal dodany zmien household uzytkownika
                                    db.collection("users").document(userid_value)
                                            .update("household", documentReference.getId());
                                    Intent intent = new Intent(getApplicationContext(), HouseSettings.class);
                                    startActivity(intent);
                                    finish();
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
}