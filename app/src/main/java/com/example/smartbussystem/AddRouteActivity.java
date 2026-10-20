package com.example.smartbussystem;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class AddRouteActivity extends AppCompatActivity {

    EditText etRouteName;
    EditText etBusNumber;
    EditText etSource;
    EditText etDestination;
    EditText etFarePerStop;
    EditText etSeats;
    EditText etDepartureTime;
    EditText etDriverName;
    EditText etDriverMobile;

    Button btnSaveRoute;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate
                .setDefaultNightMode(
                        AppCompatDelegate
                                .MODE_NIGHT_NO
                );

        setContentView(R.layout.activity_add_route);

        db = FirebaseFirestore.getInstance();

        etRouteName = findViewById(R.id.etRouteName);
        etBusNumber = findViewById(R.id.etBusNumber);
        etSource = findViewById(R.id.etSource);
        etDestination = findViewById(R.id.etDestination);
        etFarePerStop = findViewById(R.id.etFarePerStop);
        etSeats = findViewById(R.id.etSeats);
        etDepartureTime = findViewById(R.id.etDepartureTime);
        etDriverName = findViewById(R.id.etDriverName);
        etDriverMobile = findViewById(R.id.etDriverMobile);

        btnSaveRoute = findViewById(R.id.btnSaveRoute);

        btnSaveRoute.setOnClickListener(v -> saveRoute());
    }

    private void saveRoute() {

        String routeName =
                etRouteName.getText().toString().trim();

        String busNumber =
                etBusNumber.getText().toString().trim();

        String source =
                etSource.getText().toString().trim();

        String destination =
                etDestination.getText().toString().trim();

        String departureTime =
                etDepartureTime.getText().toString().trim();

        String driverName =
                etDriverName.getText().toString().trim();

        String driverMobile =
                etDriverMobile.getText().toString().trim();

        String fare =
                etFarePerStop.getText().toString().trim();

        String seats =
                etSeats.getText().toString().trim();

        if(routeName.isEmpty()
                || busNumber.isEmpty()
                || source.isEmpty()
                || destination.isEmpty()
                || fare.isEmpty()
                || seats.isEmpty()) {

            Toast.makeText(
                    this,
                    "Fill all fields",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        HashMap<String,Object> route =
                new HashMap<>();

        route.put("routeName", routeName);
        route.put("busNumber", busNumber);
        route.put("source", source);
        route.put("destination", destination);
        route.put("farePerStop",
                Integer.parseInt(fare));

        route.put("totalSeats",
                Integer.parseInt(seats));

        route.put("availableSeats",
                Integer.parseInt(seats));

        route.put("departureTime",
                departureTime);

        route.put("driverName",
                driverName);

        route.put("driverMobile",
                driverMobile);

        db.collection("routes")
                .add(route)
                .addOnSuccessListener(documentReference -> {

                    Toast.makeText(
                            this,
                            "Route Added Successfully",
                            Toast.LENGTH_LONG
                    ).show();

                    finish();

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                });
    }
}