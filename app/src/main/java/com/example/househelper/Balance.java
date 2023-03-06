package com.example.househelper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.househelper.databinding.ActivityBalanceBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Balance extends Drawer implements AdapterView.OnItemSelectedListener {

//    ArrayList<BalanceListModel> balanceListModels = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ActivityBalanceBinding activityBalanceBinding;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        super.onCreate(savedInstanceState);
        activityBalanceBinding = ActivityBalanceBinding.inflate(getLayoutInflater());
        setContentView(activityBalanceBinding.getRoot());
        allocateActivityTitle(getString(R.string.balance));

        Spinner spinner_type = findViewById(R.id.balance_change_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.balance_change_type_display, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_type.setAdapter(adapter);
        spinner_type.setOnItemSelectedListener(this);

        SharedPreferences preferences = getSharedPreferences("user_data", MODE_PRIVATE);
        String userid_value = preferences.getString("userid", "");

        TextView tvamount =(TextView) findViewById(R.id.balance_change_amount);
        TextView tvdescription =(TextView) findViewById(R.id.balance_change_description);

        String[] balance_change_type_display = getResources().getStringArray(R.array.balance_change_type_display);
        String[] balance_change_type_value = getResources().getStringArray(R.array.balance_change_type_value);
        HashMap<String, String> check_type = new HashMap<String, String>();
        for (int i = 0; i < balance_change_type_display.length; i++) {
            check_type.put(balance_change_type_display[i], balance_change_type_value[i]);
        }

//        setupBalanceListModels();



        MaterialButton balance_change_btn =(MaterialButton) findViewById(R.id.balance_change_btn);
        balance_change_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String description;
                Double amount;
                String input = tvamount.getText().toString();
                description = tvdescription.getText().toString();
                type = check_type.get(type).toString();

                if(!input.equals("") && !description.equals("") && !type.equals("")) {
                    String pattern = "^\\d+(\\.\\d{1,2})?$";
                    input = input.replace(",", ".");
                    if (!input.matches(pattern)) {
                        Toast.makeText(getApplicationContext(), "Wprowadzona kwota jest nieprawidłowa.", Toast.LENGTH_SHORT).show();
                    } else {
                        amount = Double.parseDouble(input);
                        if(type.equals("expense")) amount = amount * -1;
                        Map<String, Object> budget_changes = new HashMap<>();
                        budget_changes.put("amount", amount);
                        budget_changes.put("user", userid_value);
                        budget_changes.put("description", description);
                        budget_changes.put("date", FieldValue.serverTimestamp());
                        db.collection("budget_changes")
                            .add(budget_changes)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(getApplicationContext(), "Budget change applied.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), BalanceList.class);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Something failed.", Toast.LENGTH_SHORT).show();
                                }
                            });
                    }
                }else {
                    Toast.makeText(getApplicationContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        type = adapterView.getItemAtPosition(i).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}