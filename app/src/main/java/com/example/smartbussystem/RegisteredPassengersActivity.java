package com.example.smartbussystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class RegisteredPassengersActivity
        extends AppCompatActivity {

    RecyclerView recyclerView;
    Button btnBack;
    FirebaseFirestore db;
    ArrayList<RegisteredPassengerModel> list;
    RegisteredPassengerAdapter adapter;

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_registered_passengers
        );

        recyclerView =
                findViewById(
                        R.id.recyclerPassengers
                );

        btnBack =
                findViewById(
                        R.id.btnBack
                );

        db =
                FirebaseFirestore
                        .getInstance();

        list =
                new ArrayList<>();

        adapter =
                new RegisteredPassengerAdapter(
                        list
                );

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerView.setAdapter(adapter);

        loadPassengers();

        btnBack.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            this,
                            admindashboard.class
                    )
            );

            finish();

        });
    }

    private void loadPassengers() {

        db.collection("passengers")
                .get()
                .addOnSuccessListener(
                        queryDocumentSnapshots -> {

                            list.clear();

                            for(var doc :
                                    queryDocumentSnapshots
                                            .getDocuments())
                            {

                                RegisteredPassengerModel p =
                                        new RegisteredPassengerModel(

                                                doc.getString(
                                                        "name"
                                                ),

                                                doc.getString(
                                                        "email"
                                                ),

                                                doc.getString(
                                                        "phone"
                                                )
                                        );

                                list.add(p);
                            }
                            adapter.notifyDataSetChanged();
                        });
    }
}