package com.example.househelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.househelper.databinding.ActivityHouseSettingsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

public class HouseSettings extends Drawer {

    ActivityHouseSettingsBinding activityHouseSettingsBinding;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String householdid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityHouseSettingsBinding = activityHouseSettingsBinding.inflate(getLayoutInflater());
        setContentView(activityHouseSettingsBinding.getRoot());
        allocateActivityTitle(getString(R.string.house_settings));

        TextView tvhousehold_info_welcome =(TextView) findViewById(R.id.household_info_welcome);
        TextView tvhousehold_info_id =(TextView) findViewById(R.id.household_info_id);
        TextView tvhousehold_info_data =(TextView) findViewById(R.id.household_info_data);

        SharedPreferences preferences = getSharedPreferences("user_data", MODE_PRIVATE);
        String userid_value = preferences.getString("userid", "");


        db.collection("users").document(userid_value)
        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                if (document.getData().containsKey("household")) {
                                    tvhousehold_info_welcome.setText(R.string.household_info_welcome);
                                    householdid = document.getString("household");
                                    tvhousehold_info_id.setText(getString(R.string.household_info_id) + " " + householdid.substring(0, 6));
                                    db.collection("households").document(householdid)
                                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document.exists()) {
                                                            String city = document.getString("city");
                                                            String street = document.getString("street");
                                                            String house_number = document.getString("house_number");
                                                            String apartment_number = document.getString("apartment_number");

                                                            tvhousehold_info_data.setText(getString(R.string.household_info_data) + " " + city + " " + street + " " + house_number + "/" + apartment_number);
                                                        } else {
                                                            Toast.makeText(getApplicationContext(), R.string.no_household_document, Toast.LENGTH_SHORT).show();
                                                        }
                                                    } else {
                                                        Toast.makeText(getApplicationContext(), R.string.document_retrieve_failed, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    tvhousehold_info_welcome.setText(R.string.household_info_welcome_no_household);
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), R.string.no_household_document, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.document_retrieve_failed, Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        MaterialButton create_house_settings_btn =(MaterialButton) findViewById(R.id.create_house_settings_btn);
        MaterialButton join_house_settings_btn =(MaterialButton) findViewById(R.id.join_house_settings_btn);
        MaterialButton reset_house_password_btn =(MaterialButton) findViewById(R.id.reset_house_password_btn);
        MaterialButton leave_house_btn =(MaterialButton) findViewById(R.id.leave_house_btn);
        leave_house_btn.setVisibility(MaterialButton.INVISIBLE);
        reset_house_password_btn.setVisibility(MaterialButton.INVISIBLE);
        create_house_settings_btn.setVisibility(MaterialButton.INVISIBLE);
        join_house_settings_btn.setVisibility(MaterialButton.INVISIBLE);

        db.collection("users")
                .document(userid_value)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.getString("household") != null) {
                            reset_house_password_btn.setVisibility(MaterialButton.VISIBLE);
                            leave_house_btn.setVisibility(MaterialButton.VISIBLE);
                        } else {
                            create_house_settings_btn.setVisibility(MaterialButton.VISIBLE);
                            join_house_settings_btn.setVisibility(MaterialButton.VISIBLE);
                        }
                    }
                });

        create_house_settings_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCreateHouseMenu();
            }
        });

        join_house_settings_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openJoinHouseMenu();
            }
        });

        reset_house_password_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword(householdid);
            }
        });

        leave_house_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leaveHousehold(userid_value);
            }
        });
    }
    public void openCreateHouseMenu() {
        Intent intent = new Intent(this, CreateHouse.class);
        startActivity(intent);
        finish();
    }
    public void openJoinHouseMenu() {
        Intent intent = new Intent(this, JoinHouse.class);
        startActivity(intent);
        finish();
    }
    public void resetPassword(String householdid) {
        TextView tvhousehold_info_password =(TextView) findViewById(R.id.household_info_password);
        int generated_password = (int)Math.floor(Math.random()*(999999-100000+1)+100000);

        db.collection("households")
                .whereEqualTo("password", generated_password)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        if (querySnapshot.isEmpty()) {
                            // If no documents are found, update the password
                            db.collection("households").document(householdid)
                                    .update("password", generated_password)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            tvhousehold_info_password.setText(getString(R.string.household_password) + " " + generated_password);
                                            Toast.makeText(getApplicationContext(), R.string.password_updated, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            // If at least one document is found, reset the password
                            resetPassword(householdid);
                        }
                    }
                });
    }
    public void leaveHousehold(String userid) {
        DocumentReference userRef = db.collection("users").document("userId");

        db.collection("users").document(userid)
                .update("household", FieldValue.delete())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), R.string.household_left, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), HouseSettings.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }
}