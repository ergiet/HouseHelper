package com.example.househelper;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.househelper.databinding.ActivityListTasksBinding;
import com.google.firebase.firestore.FirebaseFirestore;

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
}
