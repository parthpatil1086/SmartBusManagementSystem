package com.example.smartbussystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText etName, etPhone, etEmail, etPassword;
    Button btnRegister;
    TextView tvLogin;

    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO
        );
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Bind Views
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        // Register Button Click
        btnRegister.setOnClickListener(v -> {

            String name =
                    etName.getText().toString().trim();

            String phone =
                    etPhone.getText().toString().trim();

            String email =
                    etEmail.getText().toString().trim();

            String password =
                    etPassword.getText().toString().trim();

            if(name.isEmpty() ||
                    phone.isEmpty() ||
                    email.isEmpty() ||
                    password.isEmpty())
            {
                Toast.makeText(
                        RegisterActivity.this,
                        "Please fill all fields",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            // Firebase Register
            auth.createUserWithEmailAndPassword(
                    email,
                    password
            ).addOnCompleteListener(task -> {

                if(task.isSuccessful())
                {
                    String uid =
                            auth.getCurrentUser().getUid();

                    HashMap<String,Object> user =
                            new HashMap<>();

                    user.put("uid", uid);
                    user.put("name", name);
                    user.put("phone", phone);
                    user.put("email", email);

                    // Save in Firestore
                    db.collection("passengers")
                            .document(uid)
                            .set(user)
                            .addOnSuccessListener(unused -> {

                                Toast.makeText(
                                        RegisterActivity.this,
                                        "Registered Successfully",
                                        Toast.LENGTH_SHORT
                                ).show();

                                startActivity(
                                        new Intent(
                                                RegisterActivity.this,
                                                LoginActivity.class
                                        )
                                );

                                finish();

                            });

                }
                else
                {
                    Toast.makeText(
                            RegisterActivity.this,
                            task.getException().getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                }

            });

        });

        // Go to Login
        tvLogin.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            RegisterActivity.this,
                            LoginActivity.class
                    )
            );

        });

    }
}