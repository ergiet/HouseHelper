package com.example.househelper;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.househelper.databinding.ActivityAddCarBinding;
import com.example.househelper.databinding.ActivityAddTaskBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddTask extends Drawer implements AdapterView.OnItemSelectedListener{

    ActivityAddTaskBinding activityAddTaskBinding;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String task_interval_type;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAddTaskBinding = ActivityAddTaskBinding.inflate(getLayoutInflater());
        setContentView(activityAddTaskBinding.getRoot());
        allocateActivityTitle("");

        Spinner spinner_type = findViewById(R.id.task_interval_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.task_interval_type_display, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_type.setAdapter(adapter);
        spinner_type.setOnItemSelectedListener(this);

        String[] task_interval_type_display = getResources().getStringArray(R.array.task_interval_type_display);
        String[] task_interval_type_value = getResources().getStringArray(R.array.task_interval_type_value);
        HashMap<String, String> interval_type = new HashMap<String, String>();
        for (int i = 0; i < task_interval_type_display.length; i++) {
            interval_type.put(task_interval_type_display[i], task_interval_type_value[i]);
        }

        MaterialButton task_interval_time = (MaterialButton) findViewById(R.id.task_interval_time);
        MaterialButton task_time = (MaterialButton) findViewById(R.id.task_time);

        task_interval_time.setOnClickListener(new AddTask.DatePickerOnClickListener());
        task_time.setOnClickListener(new AddTask.DatePickerOnClickListener());

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        task_interval_time.setText(cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR));
        task_time.setText(cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR));

        boolean[] isPrivate = {false};
        CheckBox private_checkbox = findViewById(R.id.task_private_checkbox);
        private_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isPrivate[0] = isChecked;
            }
        });

        boolean[] isRecursive = {false};
        CheckBox recursive_checkbox = findViewById(R.id.task_recursive_checkbox);
        LinearLayout task_interval =(LinearLayout) findViewById(R.id.task_interval);
        task_interval.setVisibility(View.GONE);
        recursive_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isRecursive[0] = isChecked;
                if(isChecked){
                    task_time.setVisibility(View.GONE);
                    task_interval.setVisibility(View.VISIBLE);
                }else{
                    task_interval.setVisibility(View.GONE);
                    task_time.setVisibility(View.VISIBLE);
                }
            }
        });

        SharedPreferences preferences = getSharedPreferences("user_data", MODE_PRIVATE);
        String userid_value = preferences.getString("userid", "");

        TextView tvtask =(TextView) findViewById(R.id.task_task);
        TextView tvinterval_time =(TextView) findViewById(R.id.task_interval_time);
        TextView tvtask_time =(TextView) findViewById(R.id.task_time);


        MaterialButton addTask = (MaterialButton) findViewById(R.id.add_task_btn);
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String task, interval_time_text, task_time_text;
                task = tvtask.getText().toString();
                if(isRecursive[0] == true){
                    task_interval_type = interval_type.get(task_interval_type).toString();
                }else{
                    task_interval_type = "none";
                }
                Date currentDate = new Date(System.currentTimeMillis());
                Timestamp interval_time = new Timestamp(currentDate);
                Timestamp task_time = new Timestamp(currentDate);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

                try {
                    interval_time_text = tvinterval_time.getText().toString();
                    Date interval_time_date = simpleDateFormat.parse(interval_time_text);
                    interval_time = new Timestamp(interval_time_date);
                } catch (ParseException e) {
                    Toast.makeText(getApplicationContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                }

                try {
                    task_time_text = tvtask_time.getText().toString();
                    Date task_time_date = simpleDateFormat.parse(task_time_text);
                    task_time = new Timestamp(task_time_date);
                } catch (ParseException e) {
                    Toast.makeText(getApplicationContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                }

                if(!task.equals("")) {
                    //Start ProgressBar first (Set visibility VISIBLE)
                    Map<String, Object> taskObject = new HashMap<>();
                    taskObject.put("task", task);
                    taskObject.put("is_private", isPrivate[0]);
                    taskObject.put("is_recursive", isRecursive[0]);
                    taskObject.put("user", userid_value);
                    if(isRecursive[0] == true){
                        Map<String, Object> prepared_interval = new HashMap<>();
                        prepared_interval.put("time", interval_time);
                        prepared_interval.put("type", task_interval_type);
                        taskObject.put("interval", prepared_interval);
                    }else if(isRecursive[0] == false){
                        Map<String, Object> prepared_interval = new HashMap<>();
                        prepared_interval.put("time", task_time);
                        prepared_interval.put("type", task_interval_type);
                        taskObject.put("interval", prepared_interval);
                    }

                    db.collection("tasks")
                            .add(taskObject)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(getApplicationContext(), "New task added", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Something failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                }else {
                    Toast.makeText(getApplicationContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    class DatePickerOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AddTask.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                            Calendar selectedDate = Calendar.getInstance();
                            selectedDate.set(year, month, day);
                            ((MaterialButton) view).setText(day + "/" + (month + 1) + "/" + year);
                        }
                    },
                    year,
                    month,
                    day
            );
            datePickerDialog.show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        task_interval_type = adapterView.getItemAtPosition(i).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
