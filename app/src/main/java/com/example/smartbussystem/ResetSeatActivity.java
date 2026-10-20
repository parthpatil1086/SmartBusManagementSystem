package com.example.smartbussystem;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ResetSeatActivity
        extends AppCompatActivity {

    RecyclerView recyclerRoutes;
    RecyclerView recyclerSeats;

    TextView tvSelectedRoute;

    FirebaseFirestore db;

    ArrayList<RouteModel> routeList;
    ArrayList<SeatModel> seatList;

    RouteAdapter routeAdapter;
    SeatAdapter seatAdapter;

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO
        );

        setContentView(
                R.layout.activity_reset_seat
        );

        recyclerRoutes =
                findViewById(
                        R.id.recyclerRoutes
                );

        recyclerSeats =
                findViewById(
                        R.id.recyclerSeats
                );

        tvSelectedRoute =
                findViewById(
                        R.id.tvSelectedRoute
                );

        db =
                FirebaseFirestore
                        .getInstance();

        routeList =
                new ArrayList<>();

        seatList =
                new ArrayList<>();

        setupRoutes();

        routeAdapter =
                new RouteAdapter(
                        this,
                        routeList,
                        route -> {

                            tvSelectedRoute.setText(
                                    route.getRouteName()
                            );

                            loadSeats(
                                    route.getRouteId()
                            );

                        }
                );

        recyclerRoutes.setLayoutManager(
                new LinearLayoutManager(
                        this,
                        LinearLayoutManager.HORIZONTAL,
                        false
                )
        );

        recyclerRoutes.setAdapter(
                routeAdapter
        );

        seatAdapter =
                new SeatAdapter(
                        this,
                        seatList
                );

        recyclerSeats.setLayoutManager(
                new LinearLayoutManager(
                        this
                )
        );

        recyclerSeats.setAdapter(
                seatAdapter
        );
    }

    private void setupRoutes() {

        routeList.add(
                new RouteModel(
                        "1",
                        "Lonavla → Nigdi"
                )
        );

        routeList.add(
                new RouteModel(
                        "2",
                        "Vadgaon → Nigdi"
                )
        );

        routeList.add(
                new RouteModel(
                        "3",
                        "Shivajinagar → Hadapsar"
                )
        );
    }

    private void loadSeats(
            String routeId
    ) {

        db.collection("seats")
                .get()
                .addOnSuccessListener(
                        queryDocumentSnapshots -> {

                            seatList.clear();

                            for(var doc :
                                    queryDocumentSnapshots
                                            .getDocuments()) {

                                String docId =
                                        doc.getId();

                                if(docId.startsWith(
                                        routeId + "_"
                                )) {

                                    String date =
                                            docId.substring(
                                                    docId.indexOf("_") + 1
                                            );

                                    Long totalSeats =
                                            doc.getLong(
                                                    "totalSeats"
                                            );

                                    Long bookedSeats =
                                            doc.getLong(
                                                    "bookedSeats"
                                            );

                                    SeatModel model =
                                            new SeatModel(
                                                    routeId,
                                                    date,
                                                    totalSeats == null ? 60 : totalSeats,
                                                    bookedSeats == null ? 0 : bookedSeats
                                            );

                                    seatList.add(
                                            model
                                    );
                                }
                            }

                            seatAdapter
                                    .notifyDataSetChanged();

                        });
    }
}