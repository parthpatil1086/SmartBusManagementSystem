package com.example.smartbussystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MyBookingActivity
        extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView tvPassengerName;

    FirebaseAuth auth;
    FirebaseFirestore db;

    List<BookingModel> bookingList =
            new ArrayList<>();

    BookingAdapter adapter;

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {
        AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO
        );
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_my_booking
        );

        auth =
                FirebaseAuth.getInstance();

        db =
                FirebaseFirestore
                        .getInstance();

        if(auth.getCurrentUser()
                == null){

            startActivity(
                    new Intent(
                            this,
                            LoginActivity.class
                    )
            );

            finish();
            return;
        }

        tvPassengerName =
                findViewById(
                        R.id.tvPassengerName
                );

        recyclerView =
                findViewById(
                        R.id.recyclerBookings
                );

        recyclerView
                .setLayoutManager(
                        new LinearLayoutManager(
                                this
                        )
                );

        adapter =
                new BookingAdapter(
                        bookingList
                );

        recyclerView.setAdapter(
                adapter
        );

        loadPassenger();
        loadBookings();
    }

    private void loadPassenger() {

        String uid =
                auth.getCurrentUser()
                        .getUid();

        db.collection("passengers")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {

                    if(doc.exists()) {

                        String name =
                                doc.getString(
                                        "name"
                                );

                        tvPassengerName
                                .setText(
                                        "Hello, "
                                                + name
                                );
                    }

                });
    }

    private void loadBookings() {

        String uid =
                auth.getCurrentUser()
                        .getUid();

        db.collection("tickets")
                .whereEqualTo(
                        "uid",
                        uid
                )
                .get()
                .addOnSuccessListener(query -> {

                    bookingList.clear();

                    for(var doc : query){

                        BookingModel booking =
                                doc.toObject(
                                        BookingModel.class
                                );

                        bookingList.add(
                                booking
                        );
                    }

                    adapter.notifyDataSetChanged();

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            e.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();

                });
    }
}