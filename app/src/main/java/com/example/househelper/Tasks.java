package com.example.househelper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.example.househelper.databinding.ActivityTasksBinding;
import com.google.android.material.button.MaterialButton;

public class Tasks extends Drawer {

    ActivityTasksBinding activityTasksBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityTasksBinding = ActivityTasksBinding.inflate(getLayoutInflater());
        setContentView(activityTasksBinding.getRoot());
        allocateActivityTitle(getString(R.string.tasks));
    }
}