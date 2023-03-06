package com.example.househelper;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.textservice.TextInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        TextView tvusername =(TextView) findViewById(R.id.username);
        TextView tvpassword =(TextView) findViewById(R.id.password);
        TextView tvemail =(TextView) findViewById(R.id.email);

        MaterialButton registerbtn =(MaterialButton) findViewById(R.id.registerbtn);

        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String username, password, email;
                username = tvusername.getText().toString().toLowerCase();
                password = tvpassword.getText().toString();
                email = tvemail.getText().toString();

                if(!username.equals("") && !password.equals("") && !email.equals("") && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Map<String, Object> user = new HashMap<>();
                    user.put("username", username);
                    user.put("password", password);
                    user.put("email", email);

                    db.collection("users")
                            .whereEqualTo("username", username)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            if(username.equals(document.getString("username"))) {
                                                Toast.makeText(getApplicationContext(), "Username already taken", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    } else {
                                        mAuth.createUserWithEmailAndPassword(email, password)
                                                .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                        if (task.isSuccessful()) {
//                                                            Toast.makeText(getApplicationContext(), "Authentication success", Toast.LENGTH_SHORT).show();
                                                            //FirebaseUser user = mAuth.getCurrentUser();
                                                            Map<String, Object> user = new HashMap<>();
                                                            user.put("email", email);
                                                            //user.put("household", null);
                                                            user.put("username", username);
                                                            db.collection("users")
                                                                    .add(user)
                                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                        @Override
                                                                        public void onSuccess(DocumentReference documentReference) {
                                                                            Toast.makeText(getApplicationContext(), "Registration successful", Toast.LENGTH_SHORT).show();
                                                                            Intent intent = new Intent(SignUp.this, Login.class);
                                                                            startActivity(intent);
                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Toast.makeText(getApplicationContext(), "Something failed", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                        } else {
                                                            Toast.makeText(getApplicationContext(), "Email is already taken", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                }
                            });

                }else {
                    if (!email.equals("") && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        Toast.makeText(getApplicationContext(), "Incorrect email", Toast.LENGTH_SHORT).show();
                    }
                    if(username.equals("") || password.equals("") || email.equals("")){
                    Toast.makeText(getApplicationContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}