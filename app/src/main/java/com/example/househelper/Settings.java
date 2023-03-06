package com.example.househelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.View;
import android.widget.TextView;

import com.example.househelper.databinding.ActivityBalanceBinding;
import com.example.househelper.databinding.ActivitySettingsBinding;
import com.google.android.material.button.MaterialButton;
import com.vishnusivadas.advanced_httpurlconnection.FetchData;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

public class Settings extends Drawer{

    ActivitySettingsBinding activitySettingsBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySettingsBinding = activitySettingsBinding.inflate(getLayoutInflater());
        setContentView(activitySettingsBinding.getRoot());
        allocateActivityTitle(getString(R.string.settings));
//        addPreferencesFromResource(R.xml.settings);
    }
}