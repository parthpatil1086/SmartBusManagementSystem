package com.example.smartbussystem;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class BusLocationActivity
        extends AppCompatActivity {

    RecyclerView recyclerView;

    ArrayList<BusLocationModel> list;

    BusLocationAdapter adapter;

    FirebaseFirestore db;

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_bus_location
        );

        recyclerView =
                findViewById(
                        R.id.recyclerBusLocation
                );

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        list =
                new ArrayList<>();

        adapter =
                new BusLocationAdapter(
                        this,
                        list
                );

        recyclerView.setAdapter(
                adapter
        );

        db =
                FirebaseFirestore.getInstance();

        loadLocations();
    }

    private void loadLocations()
    {
        db.collection("bus_locations")
                .addSnapshotListener(
                        (value,error) ->
                        {
                            list.clear();

                            if(value == null)
                            {
                                return;
                            }

                            value.getDocuments()
                                    .forEach(document -> {

                                        String busId =
                                                document.getId();

                                        Double lat =
                                                document.getDouble(
                                                        "latitude"
                                                );

                                        Double lng =
                                                document.getDouble(
                                                        "longitude"
                                                );

                                        Long timestamp =
                                                document.getLong(
                                                        "timestamp"
                                                );

                                        list.add(
                                                new BusLocationModel(
                                                        busId,
                                                        lat,
                                                        lng,
                                                        timestamp
                                                )
                                        );
                                    });

                            adapter.notifyDataSetChanged();
                        });
    }
}