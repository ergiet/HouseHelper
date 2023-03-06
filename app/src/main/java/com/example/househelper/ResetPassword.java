package com.example.househelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Patterns;
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

public class ResetPassword extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        mAuth = FirebaseAuth.getInstance();

        MaterialButton resetbtn=(MaterialButton) findViewById(R.id.reset_house_password_btn);

        resetbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                TextView tvemail =(TextView) findViewById(R.id.email);
                String mail = tvemail.getText().toString().toLowerCase();
                Toast.makeText(getApplicationContext(), mail, Toast.LENGTH_SHORT).show();

                if (!mail.isEmpty()) {
                    if (Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                        mAuth.sendPasswordResetEmail(mail)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), "wiadomość została wysłana", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent( ResetPassword.this, Login.class);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(getApplicationContext(), "nie ma uzytkownika z takim mailem", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else Toast.makeText(getApplicationContext(), R.string.incorrect_e_mail, Toast.LENGTH_SHORT).show();
                } else Toast.makeText(getApplicationContext(), "Prosze wpisac maila", Toast.LENGTH_SHORT).show();
            }
        });
    }
}