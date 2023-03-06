package com.example.househelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.househelper.databinding.ActivityMainBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;

public class Drawer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;

    @Override
    public void setContentView(View view){
        drawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_drawer_base, null);
        FrameLayout container = drawerLayout.findViewById(R.id.activityContainer);
        container.addView(view);
        super.setContentView(drawerLayout);

        Toolbar toolbar = drawerLayout.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView=drawerLayout.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        switch(item.getItemId()){
            case R.id.nav_tasks:
                startActivity(new Intent(this, Tasks.class));
                overridePendingTransition(0,0);
                break;
            case R.id.nav_add_task:
                startActivity(new Intent(this, AddTask.class));
                overridePendingTransition(0,0);
                break;
            case R.id.nav_list_tasks:
                startActivity(new Intent(this, ListTasks.class));
                overridePendingTransition(0,0);
                break;
            case R.id.nav_balance:
                startActivity(new Intent(this, Balance.class));
                overridePendingTransition(0,0);
                break;
            case R.id.nav_balance_history:
                startActivity(new Intent(this, BalanceList.class));
                overridePendingTransition(0,0);
                break;
            case R.id.nav_add_shopping_list:
                startActivity(new Intent(this, AddShoppingList.class));
                overridePendingTransition(0,0);
                break;
            case R.id.nav_list_shopping_lists:
                startActivity(new Intent(this, ListShoppingLists.class));
                overridePendingTransition(0,0);
                break;
            case R.id.nav_add_car:
                startActivity(new Intent(this, AddCar.class));
                overridePendingTransition(0,0);
                break;
            case R.id.list_cars:
                startActivity(new Intent(this, ListCars.class));
                overridePendingTransition(0,0);
                break;
            case R.id.nav_settings:
                startActivity(new Intent(this, Settings.class));
                overridePendingTransition(0,0);
                break;
            case R.id.household_settings:
                startActivity(new Intent(this, HouseSettings.class));
                overridePendingTransition(0,0);
                break;
            case R.id.logout:
                SharedPreferences preferences = getSharedPreferences("user_data", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("remember", "false");
                editor.apply();

                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
                break;
        }
        return false;
    }

    protected void allocateActivityTitle(String titleString){
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(titleString);
        }
    }
}