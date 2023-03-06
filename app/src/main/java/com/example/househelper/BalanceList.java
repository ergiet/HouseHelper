package com.example.househelper;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.househelper.databinding.BalanceListBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class BalanceList extends Drawer {

    BalanceListBinding balanceListBinding;
    ArrayList<BalanceListModel> balanceListModels = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        balanceListBinding = BalanceListBinding.inflate(getLayoutInflater());
        setContentView(balanceListBinding.getRoot());
        allocateActivityTitle("");

        MaterialButton startDateButton = (MaterialButton) findViewById(R.id.start_date_button);
        MaterialButton endDateButton = (MaterialButton) findViewById(R.id.end_date_button);
        startDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        BalanceList.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                Calendar selectedDate = Calendar.getInstance();
                                selectedDate.set(year, month, day);

                                Calendar endDate = Calendar.getInstance();
                                String endDateText = endDateButton.getText().toString();
                                if (!endDateText.isEmpty()) {
                                    String[] dateParts = endDateText.split("/");
                                    endDate.set(Integer.parseInt(dateParts[2]), Integer.parseInt(dateParts[1]) - 1, Integer.parseInt(dateParts[0]));

                                    if (selectedDate.before(endDate) || selectedDate.equals(endDate)) {
                                        // set the selected date to the startDateButton text
                                        startDateButton.setText(day + "/" + (month + 1) + "/" + year);
                                        String startDateText = startDateButton.getText().toString();

                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                        try {
                                            Date endDateObject = endDate.getTime();
                                            Timestamp endTimestamp = new Timestamp(endDateObject);

                                            Date startDate = simpleDateFormat.parse(startDateText);
                                            Timestamp startTimestamp = new Timestamp(startDate);
                                            setupBalanceListModels(startTimestamp, endTimestamp);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        Toast.makeText(BalanceList.this, R.string.start_date_before, Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    startDateButton.setText(day + "/" + (month + 1) + "/" + year);
                                }
                            }
                        },
                        year,
                        month,
                        day
                );
                datePickerDialog.show();
            }
        });

        endDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        BalanceList.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                Calendar selectedDate = Calendar.getInstance();
                                selectedDate.set(year, month, day);

                                Calendar startDate = Calendar.getInstance();
                                String[] startDateParts = startDateButton.getText().toString().split("/");
                                startDate.set(Integer.parseInt(startDateParts[2]),
                                        Integer.parseInt(startDateParts[1]) - 1,
                                        Integer.parseInt(startDateParts[0]));

                                if (selectedDate.before(startDate)) {
                                    // display a message indicating that the end date must be after the start date
                                    Toast.makeText(BalanceList.this, R.string.end_date_after, Toast.LENGTH_SHORT).show();
                                } else {
                                    endDateButton.setText(day + "/" + (month + 1) + "/" + year);

                                    String endDateText = endDateButton.getText().toString();

                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                    try {
                                        Date startDateObject = startDate.getTime();
                                        Timestamp startTimestamp = new Timestamp(startDateObject);

                                        Date endDate = simpleDateFormat.parse(endDateText);
                                        Timestamp endTimestamp = new Timestamp(endDate);
                                        setupBalanceListModels(startTimestamp, endTimestamp);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        },
                        year,
                        month,
                        day
                );
                datePickerDialog.show();
            }
        });

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        startDateButton.setText(cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR));
        Timestamp startTimestamp = new Timestamp(cal.getTime());

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        endDateButton.setText(cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR));
        Timestamp endTimestamp = new Timestamp(cal.getTime());

        setupBalanceListModels(startTimestamp, endTimestamp);

        SharedPreferences preferences = getSharedPreferences("user_data", MODE_PRIVATE);
        String username_value = preferences.getString("username", "");
    }
    private void retrieveHouseholdUsers(String userid, final BalanceList.Callback callback) {
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

    private void getUsername(String arrayUser, final BalanceList.CallbackUsername callback) {
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
    private void setupBalanceListModels(final Timestamp startTimestamp, final Timestamp endTimestamp){
        balanceListModels.clear();
        SharedPreferences preferences = getSharedPreferences("user_data", MODE_PRIVATE);
        String userid_value = preferences.getString("userid", "");

        retrieveHouseholdUsers(userid_value, new BalanceList.Callback() {
            @Override
            public void onResult(ArrayList<String> arrayUsers) {
                db.collection("budget_changes")
                        .orderBy("date")
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String user = document.getString("user");
                                        for (String arrayUser : arrayUsers) {
                                            if (arrayUser.equals(user)) {
                                                getUsername(user, new BalanceList.CallbackUsername() {
                                                    @Override
                                                    public void onResult(String username) {
                                                        Timestamp time = document.getTimestamp("date");
                                                        Calendar cal = Calendar.getInstance();
                                                        cal.setTime(time.toDate());

                                                        Calendar current = Calendar.getInstance();
                                                        if (time.toDate().compareTo(startTimestamp.toDate()) >= 0 && time.toDate().compareTo(endTimestamp.toDate()) <= 0) {

                                                            balanceListModels.add(new BalanceListModel(username, document.getString("description"), document.getDouble("amount"), document.getTimestamp("date")));
                                                        }

                                                        RecyclerView recyclerView = findViewById(R.id.balance_list);

                                                        Balance_RecyclerViewAdapter adapterlist = new Balance_RecyclerViewAdapter(BalanceList.this, balanceListModels);
                                                        recyclerView.setAdapter(adapterlist);
                                                        recyclerView.setLayoutManager(new LinearLayoutManager(BalanceList.this));
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