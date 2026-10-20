package com.example.smartbussystem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity
        extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;
    TextView tvRegister;

    FirebaseAuth auth;
    FirebaseFirestore db;

    SharedPreferences preferences;

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO
        );

        setContentView(
                R.layout.activity_login
        );

        auth =
                FirebaseAuth.getInstance();

        db =
                FirebaseFirestore.getInstance();

        preferences =
                getSharedPreferences(
                        "SmartBus",
                        MODE_PRIVATE
                );

        etEmail =
                findViewById(
                        R.id.etEmail
                );

        etPassword =
                findViewById(
                        R.id.etPassword
                );

        btnLogin =
                findViewById(
                        R.id.btnLogin
                );

        tvRegister =
                findViewById(
                        R.id.tvRegister
                );

        checkExistingLogin();

        btnLogin.setOnClickListener(v -> {

            String email =
                    etEmail.getText()
                            .toString()
                            .trim();

            String password =
                    etPassword.getText()
                            .toString()
                            .trim();

            if(email.isEmpty()
                    || password.isEmpty()) {

                Toast.makeText(
                        this,
                        "Enter all fields",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            checkAdminLogin(
                    email,
                    password
            );

        });

        tvRegister.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            LoginActivity.this,
                            RegisterActivity.class
                    )
            );

        });

    }

    private void checkAdminLogin(
            String email,
            String password
    ) {

        db.collection("admin")
                .whereEqualTo(
                        "username",
                        email
                )
                .whereEqualTo(
                        "password",
                        password
                )
                .get()
                .addOnSuccessListener(snapshot -> {

                    if(!snapshot.isEmpty()) {

                        preferences.edit()
                                .putString(
                                        "role",
                                        "admin"
                                )
                                .apply();

                        Toast.makeText(
                                this,
                                "Admin Login Success",
                                Toast.LENGTH_SHORT
                        ).show();

                        startActivity(
                                new Intent(
                                        LoginActivity.this,
                                        admindashboard.class
                                )
                        );

                        finish();

                    } else {

                        checkValidatorLogin(
                                email,
                                password
                        );
                    }

                });
    }

    private void checkValidatorLogin(
            String email,
            String password
    ) {

        db.collection("validator")
                .whereEqualTo(
                        "username",
                        email
                )
                .whereEqualTo(
                        "password",
                        password
                )
                .get()
                .addOnSuccessListener(snapshot -> {

                    if(!snapshot.isEmpty()) {

                        preferences.edit()
                                .putString("role", "validator")
                                .putString("username", email)
                                .apply();

                        Toast.makeText(
                                this,
                                "Validator Login Success",
                                Toast.LENGTH_SHORT
                        ).show();

                        startActivity(
                                new Intent(
                                        LoginActivity.this,
                                        ValidateTicketActivity.class
                                )
                        );

                        finish();

                    } else {

                        passengerLogin(
                                email,
                                password
                        );
                    }

                });
    }

    private void passengerLogin(
            String email,
            String password
    ) {

        auth.signInWithEmailAndPassword(
                        email,
                        password
                )
                .addOnCompleteListener(task -> {

                    if(task.isSuccessful()) {

                        preferences.edit()
                                .putString(
                                        "role",
                                        "passenger"
                                )
                                .apply();

                        Toast.makeText(
                                this,
                                "Passenger Login Success",
                                Toast.LENGTH_SHORT
                        ).show();

                        startActivity(
                                new Intent(
                                        LoginActivity.this,
                                        MainActivity.class
                                )
                        );

                        finish();

                    } else {

                        Toast.makeText(
                                this,
                                "Invalid Credentials",
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                });
    }

    private void checkExistingLogin() {

        String role =
                preferences.getString(
                        "role",
                        ""
                );

        if(role.equals("admin"))
        {
            startActivity(
                    new Intent(
                            LoginActivity.this,
                            admindashboard.class
                    )
            );

            finish();
        }

        else if(role.equals("validator"))
        {
            startActivity(
                    new Intent(
                            LoginActivity.this,
                            ValidateTicketActivity.class
                    )
            );

            finish();
        }

        else if(
                role.equals("passenger")
                        &&
                        auth.getCurrentUser() != null
        )
        {
            startActivity(
                    new Intent(
                            LoginActivity.this,
                            MainActivity.class
                    )
            );

            finish();
        }
    }

}