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

import com.example.househelper.databinding.ActivityJoinHouseBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.checkerframework.checker.nullness.qual.NonNull;

public class JoinHouse extends Drawer {

    ActivityJoinHouseBinding activityJoinHouseBinding;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityJoinHouseBinding = activityJoinHouseBinding.inflate(getLayoutInflater());
        setContentView(activityJoinHouseBinding.getRoot());
        allocateActivityTitle(getString(R.string.join_house));

        TextView tvhousehold_id =(TextView) findViewById(R.id.household_id);
        TextView tvhouse_password =(TextView) findViewById(R.id.house_password);

        SharedPreferences preferences = getSharedPreferences("user_data", MODE_PRIVATE);
        String userid_value = preferences.getString("userid", "");

        MaterialButton join_house_btn =(MaterialButton) findViewById(R.id.join_house_btn);

        join_house_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String householdid, house_password;
                householdid = tvhousehold_id.getText().toString();
                house_password = tvhouse_password.getText().toString();
                Long long_pass = Long.parseLong(house_password);

                if(!householdid.equals("") && !house_password.equals("")) {
                    if(!(householdid.length()<6)){
                        db.collection("households")
                                .whereEqualTo("password", long_pass)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            boolean matchFound = false;
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                if (document.getId().startsWith(householdid)) {
                                                    matchFound = true;
                                                    // update the user document with the household id
                                                    db.collection("users").document(userid_value)
                                                            .update("household", document.getId());
                                                    Toast.makeText(getApplicationContext(), "House joined successfully.", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(getApplicationContext(), HouseSettings.class);
                                                    startActivity(intent);
                                                    finish();
                                                    break;
                                                }
                                            }
                                            if (!matchFound) {
                                                Toast.makeText(getApplicationContext(), "No matching household found.", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Failed to retrieve households.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }else{
                        Toast.makeText(getApplicationContext(), "Provided household id is too short.", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getApplicationContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}