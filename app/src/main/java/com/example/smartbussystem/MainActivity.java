package com.example.smartbussystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private CardView cardTracking;
    private CardView cardLivepassenger;

    private ImageView profileIcon;
    private TextView txtUsername;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO
        );

        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Check Login
        if (auth.getCurrentUser() == null) {

            Intent intent =
                    new Intent(
                            MainActivity.this,
                            LoginActivity.class
                    );

            startActivity(intent);

            finish();
            return;
        }

        // Initialize Views
        txtUsername = findViewById(R.id.txtUsername);

        cardTracking = findViewById(R.id.cardTracking);
        cardLivepassenger = findViewById(R.id.cardLivepassenger);

        profileIcon = findViewById(R.id.profileIcon);

        BottomNavigationView bottomNav =
                findViewById(R.id.bottomNav);

        // Load Passenger Name
        FirebaseUser currentUser =
                auth.getCurrentUser();

        if (currentUser != null) {

            String uid =
                    currentUser.getUid();

            db.collection("passengers")
                    .document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {

                        if (documentSnapshot.exists()) {

                            String name =
                                    documentSnapshot.getString("name");

                            if (name != null &&
                                    !name.isEmpty()) {

                                txtUsername.setText(name);

                            } else {

                                txtUsername.setText("Passenger");
                            }

                        } else {

                            txtUsername.setText("Passenger");
                        }

                    })
                    .addOnFailureListener(e -> {

                        txtUsername.setText("Passenger");

                    });
        }

        // Live Tracking
        cardTracking.setOnClickListener(v -> {

             startActivity(new Intent(
             MainActivity.this,
                     BusMapActivity.class));

        });

        // Schedule
        cardLivepassenger.setOnClickListener(v -> {

            // Future
             startActivity(new Intent(
             MainActivity.this,
             LivePassengerCountActivity.class));

        });

        // Profile Screen
        profileIcon.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            MainActivity.this,
                            ProfileActivity.class
                    );

            startActivity(intent);

        });

        // Bottom Navigation
        bottomNav.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_book) {

                Intent intent =
                        new Intent(
                                MainActivity.this,
                                bookingActivity.class
                        );

                startActivity(intent);

                return true;
            }

            if (id == R.id.nav_bookings) {

                Intent intent =
                        new Intent(
                                MainActivity.this,
                                MyBookingActivity.class
                        );

                startActivity(intent);

                return true;
            }

            return false;
        });
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        finishAffinity();

    }
}