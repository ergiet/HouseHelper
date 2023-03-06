package com.example.househelper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.househelper.databinding.ActivityListCarsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Calendar;

public class ListCars extends Drawer {

    ActivityListCarsBinding activityListCarsBinding;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ArrayList<CarModel> carModels = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityListCarsBinding = ActivityListCarsBinding.inflate(getLayoutInflater());
        setContentView(activityListCarsBinding.getRoot());
        allocateActivityTitle("");

        SharedPreferences preferences = getSharedPreferences("user_data", MODE_PRIVATE);
        String username_value = preferences.getString("username", "");

        setupCarListModels();
    }

    private void retrieveHouseholdUsers(String userid, final ListCars.Callback callback) {
        final ArrayList<String> arrayUsers = new ArrayList<>();
        db.collection("users").document(userid)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            String householdid = document.getString("household").toString();
                            db.collection("users")
                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    if(!(document.getString("household") == null)) {
                                                        if (householdid.equals(document.getString("household").toString())) {
                                                            arrayUsers.add(document.getId());
                                                        }
                                                    }
                                                }
                                                callback.onResult(arrayUsers);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void getUsername(String arrayUser, final ListCars.CallbackUsername callback) {
        db.collection("users").document(arrayUser)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            String username = task.getResult().getString("username");
                            callback.onResult(username);
                        }
                    }
                });
    }



    interface Callback {
        void onResult(ArrayList<String> arrayUsers);
    }

    interface CallbackUsername {
        void onResult(String username);
    }

    private void setupCarListModels(){
        SharedPreferences preferences = getSharedPreferences("user_data", MODE_PRIVATE);
        String userid_value = preferences.getString("userid", "");

        retrieveHouseholdUsers(userid_value, new ListCars.Callback() {
            @Override
            public void onResult(ArrayList<String> arrayUsers) {
                db.collection("cars")
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String user = document.getString("user");
                                        for (String arrayUser : arrayUsers) {
                                            if (arrayUser.equals(user)) {
                                                getUsername(user, new ListCars.CallbackUsername() {
                                                    @Override
                                                    public void onResult(String username) {

                                                        if(document.getBoolean("is_private") && document.getString("user").equals(userid_value)){
                                                            carModels.add(new CarModel(document.getString("make"), document.getString("model"), document.getString("vin"), document.getString("plate"), document.getTimestamp("first_registration"),
                                                                    document.getTimestamp("mot"), document.getTimestamp("insurance"), document.getBoolean("is_private"), document.getString("user")));
                                                        }else if(!document.getBoolean("is_private")){
                                                            carModels.add(new CarModel(document.getString("make"), document.getString("model"), document.getString("vin"), document.getString("plate"), document.getTimestamp("first_registration"),
                                                                    document.getTimestamp("mot"), document.getTimestamp("insurance"), document.getBoolean("is_private"), document.getString("user")));
                                                        }

                                                        RecyclerView recyclerView = findViewById(R.id.cars_list);

                                                        ListCars_RecyclerViewAdapter adapterlist = new ListCars_RecyclerViewAdapter(ListCars.this, carModels);
                                                        adapterlist.setOnItemClickListener(new ListCars_RecyclerViewAdapter.OnItemClickListener() {
                                                            @Override
                                                            public void onItemClick(CarModel item) {
                                                                Intent intent = new Intent(getApplicationContext(), EditCar.class);
                                                                intent.putExtra("car_id", document.getId());
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        });
                                                        recyclerView.setAdapter(adapterlist);
                                                        recyclerView.setLayoutManager(new LinearLayoutManager(ListCars.this));
                                                    }
                                                });
                                            }
                                        }
                                    }
                                }
                            }
                        });
            }
        });
    }

}