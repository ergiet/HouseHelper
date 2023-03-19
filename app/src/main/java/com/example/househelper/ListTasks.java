package com.example.househelper;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.househelper.databinding.ActivityListTasksBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;

public class ListTasks extends Drawer {
    ActivityListTasksBinding activityListTasksBinding;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    //ArrayList<CarModel> carModels = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityListTasksBinding = ActivityListTasksBinding.inflate(getLayoutInflater());
        setContentView(activityListTasksBinding.getRoot());
        allocateActivityTitle("");

        SharedPreferences preferences = getSharedPreferences("user_data", MODE_PRIVATE);
        String username_value = preferences.getString("username", "");

        //setupCarListModels();
    }

    private void retrieveHouseholdUsers(String userid, final ListTasks.Callback callback) {
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

    private void getUsername(String arrayUser, final ListTasks.CallbackUsername callback) {
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



}
