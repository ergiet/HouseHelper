package com.example.househelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Display;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.vishnusivadas.advanced_httpurlconnection.FetchData;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.checkerframework.checker.nullness.qual.NonNull;

public class Login extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        TextView tvusername =(TextView) findViewById(R.id.username);
        TextView tvpassword =(TextView) findViewById(R.id.password);

        TextView register =(TextView) findViewById(R.id.register);
        TextView forgotpass =(TextView) findViewById(R.id.forgotpass);

        MaterialButton loginbtn =(MaterialButton) findViewById(R.id.loginbtn);

        //otwiera rejestracje
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRegister();
            }
        });
        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgotPassword();
            }
        });

        SharedPreferences preferences = getSharedPreferences("user_data", MODE_PRIVATE);
        String remember_value = preferences.getString("remember", "");
        if(remember_value.equals("true")){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }else if(remember_value.equals("false")){
            Toast.makeText(Login.this, "Please login", Toast.LENGTH_SHORT).show();
        }

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String username, password;
                username = tvusername.getText().toString().toLowerCase();
                password = tvpassword.getText().toString();
                if(!username.equals("") && !password.equals("")) {
                    //Start ProgressBar first (Set visibility VISIBLE)
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            db.collection("users")
                                    .whereEqualTo("username", username)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                                String email = "";
                                                String userid = "";
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    email = document.getString("email");
                                                    userid = document.getId();
//                                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                                }
                                                SharedPreferences preferences = getSharedPreferences("user_data", MODE_PRIVATE);
                                                SharedPreferences.Editor editor = preferences.edit();
                                                editor.putString("remember", "true");
                                                editor.putString("username", username);
                                                editor.putString("userid", userid);
                                                editor.apply();
                                                mAuth.signInWithEmailAndPassword(email, password)
                                                        .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(getApplicationContext(), "Successfully logged in", Toast.LENGTH_SHORT).show();
                                                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                } else {
                                                                    Toast.makeText(getApplicationContext(), "User authentication failed", Toast.LENGTH_SHORT).show();

                                                                }
                                                            }
                                                        });
                                            } else {
                                                Toast.makeText(getApplicationContext(), "No such user", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                        }
                    });
                }else {
                    Toast.makeText(getApplicationContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void openRegister() {
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
    }
    public void forgotPassword() {
        Intent intent = new Intent(this, ResetPassword.class);
        startActivity(intent);
    }
}