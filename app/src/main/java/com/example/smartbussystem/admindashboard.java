package com.example.smartbussystem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class admindashboard
        extends AppCompatActivity {

    TextView tvPassengers,
            tvRevenue;

    Button btnViewPassengers,
            btnRegisteredPassengers,
            btnResetSeats,
            btnProfitPerRoute,
            btnViewRoute,
            btnAddRoute,
    btnLogout;
    Button btnAddExpenses,btnViewExpenses;

    FirebaseFirestore db;

    int totalRevenue = 0;

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        AppCompatDelegate
                .setDefaultNightMode(
                        AppCompatDelegate
                                .MODE_NIGHT_NO
                );

        setContentView(
                R.layout.activity_admindashboard
        );

        SharedPreferences preferences =
                getSharedPreferences(
                        "SmartBus",
                        MODE_PRIVATE
                );

        String role =
                preferences.getString(
                        "role",
                        ""
                );

        if(!role.equals("admin"))
        {
            Toast.makeText(
                    this,
                    "Admin Login Required",
                    Toast.LENGTH_SHORT
            ).show();

            Intent intent =
                    new Intent(
                            admindashboard.this,
                            LoginActivity.class
                    );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                            |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);

            finish();
            return;
        }

        db =
                FirebaseFirestore
                        .getInstance();

        tvPassengers =
                findViewById(
                        R.id.tvPassengers
                );

        tvRevenue =
                findViewById(
                        R.id.tvRevenue
                );

        btnViewPassengers =
                findViewById(
                        R.id.btnViewPassengers
                );

        btnRegisteredPassengers =
                findViewById(
                        R.id.btnRegisteredPassengers
                );

        btnResetSeats =
                findViewById(
                        R.id.btnResetSeats
                );


        btnProfitPerRoute =
                findViewById(
                        R.id.btnProfitPerRoute
                );

        btnViewRoute =
                findViewById(
                        R.id.btnViewRoute
                );

        btnLogout =
                findViewById(
                        R.id.btnLogout
                );
        btnAddRoute =
                findViewById(R.id.btnAddRoute);

        btnAddExpenses =
                findViewById(
                        R.id.btnAddExpenses
                );

        btnViewExpenses =
                findViewById(
                        R.id.btnViewExpenses
                );

        // ==========================
        // Load Dashboard Data
        // ==========================

        loadPassengerCount();
        loadRevenue();

        // ==========================
        // Navigation Buttons
        // ==========================

        btnViewPassengers
                .setOnClickListener(v -> {

                    startActivity(
                            new Intent(
                                    admindashboard.this,
                                    PassengerListActivity.class
                            )
                    );

                });

        btnRegisteredPassengers
                .setOnClickListener(v -> {

                    startActivity(
                            new Intent(
                                    admindashboard.this,
                                    RegisteredPassengersActivity.class
                            )
                    );

                });

        btnResetSeats
                .setOnClickListener(v -> {

                     startActivity(
                          new Intent(
                                  admindashboard.this,
                                  ResetSeatActivity.class
                          )
                     );

                });

        btnProfitPerRoute
                .setOnClickListener(v -> {

                     startActivity(
                          new Intent(
                                  admindashboard.this,
                                  ProfitRouteActivity.class
                          )
                     );

                });

        btnAddRoute.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            admindashboard.this,
                            AddRouteActivity.class
                    )
            );

        });

        btnAddExpenses.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            admindashboard.this,
                            AddExpenseActivity.class
                    )
            );

        });

        btnViewExpenses.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            admindashboard.this,
                            ViewExpenseActivity.class
                    )
            );

        });

        // ==========================
        // View Routes
        // ==========================

//        btnViewRoute
//                .setOnClickListener(v -> {
//
//                    Toast.makeText(
//                            this,
//                            "View Routes Coming Soon",
//                            Toast.LENGTH_SHORT
//                    ).show();
//
//                    // startActivity(
//                    //      new Intent(
//                    //              admindashboard.this,
//                    //              ViewRoutesActivity.class
//                    //      )
//                    // );
//
//                });

        btnLogout.setOnClickListener(v -> {

            // Sign out firebase
            FirebaseAuth.getInstance()
                    .signOut();

            // Clear ONLY login session
            preferences.edit()
                    .remove("role")
                    .apply();

            Toast.makeText(
                    admindashboard.this,
                    "Admin Logged Out",
                    Toast.LENGTH_SHORT
            ).show();

            Intent intent =
                    new Intent(
                            admindashboard.this,
                            LoginActivity.class
                    );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                            |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);

            finish();

        });
    }

    private void loadPassengerCount() {

        db.collection("passengers")
                .get()
                .addOnSuccessListener(
                        queryDocumentSnapshots -> {

                            int count =
                                    queryDocumentSnapshots
                                            .size();

                            tvPassengers.setText(
                                    "👤 Total Registered Passengers: "
                                            + count
                            );

                        })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Failed to load passengers",
                            Toast.LENGTH_SHORT
                    ).show();

                });

    }

    private void loadRevenue() {

        db.collection("tickets")
                .get()
                .addOnSuccessListener(
                        queryDocumentSnapshots -> {

                            totalRevenue = 0;

                            for(var doc :
                                    queryDocumentSnapshots
                                            .getDocuments())
                            {

                                Long amount =
                                        doc.getLong(
                                                "total"
                                        );

                                if(amount != null)
                                {
                                    totalRevenue +=
                                            amount.intValue();
                                }
                            }

                            tvRevenue.setText(
                                    "💰 Total Revenue: ₹"
                                            + totalRevenue
                            );

                        })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Failed to load revenue",
                            Toast.LENGTH_SHORT
                    ).show();

                });
    }
}