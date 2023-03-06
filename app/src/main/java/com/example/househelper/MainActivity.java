package com.example.househelper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.househelper.databinding.ActivityMainBinding;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends Drawer {

    ActivityMainBinding activityMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());
        allocateActivityTitle("");

        SharedPreferences preferences = getSharedPreferences("user_data", MODE_PRIVATE);
        String username_value = preferences.getString("username", "");
        TextView tvwelcome =(TextView) findViewById(R.id.welcome);
        tvwelcome.setText(getString(R.string.hello) + " " + username_value);
    }
}