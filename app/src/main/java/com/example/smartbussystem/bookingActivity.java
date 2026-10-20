package com.example.smartbussystem;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class bookingActivity extends AppCompatActivity {

    TextView tvUserName,
            tvSeats,
            tvPassengerCount,
            tvPrice;

    Spinner spinnerRoute,
            spinnerFrom,
            spinnerTo;

    Button btnDate,
            btnTime,
            btnMinus,
            btnPlus,
            btnCalculateFare,
            btnConfirmBooking;

    FirebaseAuth auth;
    FirebaseFirestore db;

    int passengerCount = 1;
    int totalFare = 0;
    String selectedDate = "";
    String selectedTime = "";

    String selectedRouteName = "";
    String selectedBusNumber = "";

    int pricePerStop = 0;

    ArrayList<String> routes =
            new ArrayList<>();

    ArrayList<String> busNumbers =
            new ArrayList<>();

    ArrayList<Integer> routePrices =
            new ArrayList<>();

    ArrayList<Integer> availableSeats =
            new ArrayList<>();

    ArrayList<String> routeIds =
            new ArrayList<>();

    HashMap<String,
            ArrayList<String>> routeStops =
            new HashMap<>();

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO
        );

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_booking
        );

        auth =
                FirebaseAuth.getInstance();

        db =
                FirebaseFirestore.getInstance();

        if(auth.getCurrentUser() == null){

            Toast.makeText(
                    this,
                    "Please Login First",
                    Toast.LENGTH_SHORT
            ).show();

            startActivity(
                    new Intent(
                            bookingActivity.this,
                            LoginActivity.class
                    )
            );

            finish();

            return;
        }

        initViews();

        loadPassengerName();

        setupRouteSpinner();

        setupDatePicker();

        setupTimePicker();

        setupPassengerCounter();

        setupFareCalculation();

        setupBooking();
    }

    private void initViews() {

        tvUserName =
                findViewById(
                        R.id.tvUserName
                );

        tvSeats =
                findViewById(
                        R.id.tvSeats
                );

        tvPassengerCount =
                findViewById(
                        R.id.tvPassengerCount
                );

        tvPrice =
                findViewById(
                        R.id.tvPrice
                );

        spinnerRoute =
                findViewById(
                        R.id.spinnerRoute
                );

        spinnerFrom =
                findViewById(
                        R.id.spinnerFrom
                );

        spinnerTo =
                findViewById(
                        R.id.spinnerTo
                );

        btnDate =
                findViewById(
                        R.id.btnDate
                );

        btnTime =
                findViewById(
                        R.id.btnTime
                );

        btnMinus =
                findViewById(
                        R.id.btnMinus
                );

        btnPlus =
                findViewById(
                        R.id.btnPlus
                );

        btnCalculateFare =
                findViewById(
                        R.id.btnCalculateFare
                );

        btnConfirmBooking =
                findViewById(
                        R.id.btnConfirmBooking
                );
    }

    private void loadPassengerName() {

        if(auth.getCurrentUser() == null){

            Toast.makeText(
                    this,
                    "Please Login First",
                    Toast.LENGTH_SHORT
            ).show();

            startActivity(
                    new Intent(
                            bookingActivity.this,
                            LoginActivity.class
                    )
            );

            finish();

            return;
        }

        String uid =
                auth.getCurrentUser()
                        .getUid();

        db.collection("passengers")
                .document(uid)
                .get()
                .addOnSuccessListener(document -> {

                    if(document.exists()){

                        String name =
                                document.getString(
                                        "name"
                                );

                        if(name != null){

                            tvUserName.setText(
                                    name
                            );
                        }
                    }
                });
    }

    private void setupRouteSpinner() {

        db.collection("routes")
                .get()
                .addOnSuccessListener(
                        queryDocumentSnapshots -> {

                            if(queryDocumentSnapshots.isEmpty()){

                                Toast.makeText(
                                        bookingActivity.this,
                                        "No Routes Available",
                                        Toast.LENGTH_LONG
                                ).show();

                                return;
                            }

                            routes.clear();

                            busNumbers.clear();

                            routePrices.clear();

                            availableSeats.clear();

                            routeStops.clear();

                            routeIds.clear();

                            for(QueryDocumentSnapshot doc
                                    : queryDocumentSnapshots){

                                routeIds.add(
                                        doc.getId()
                                );

                                String routeName =
                                        doc.getString(
                                                "routeName"
                                        );

                                String busNumber =
                                        doc.getString(
                                                "busNumber"
                                        );

                                Long fare =
                                        doc.getLong(
                                                "farePerStop"
                                        );

                                Long seats =
                                        doc.getLong(
                                                "availableSeats"
                                        );

                                if(routeName == null){

                                    routeName =
                                            "Unknown Route";
                                }

                                if(busNumber == null){

                                    busNumber =
                                            "N/A";
                                }

                                routes.add(
                                        routeName
                                );

                                busNumbers.add(
                                        busNumber
                                );

                                routePrices.add(
                                        fare != null
                                                ? fare.intValue()
                                                : 0
                                );

                                availableSeats.add(
                                        seats != null
                                                ? seats.intValue()
                                                : 0
                                );

                                String source =
                                        doc.getString(
                                                "source"
                                        );

                                String destination =
                                        doc.getString(
                                                "destination"
                                        );

                                ArrayList<String> stops =
                                        new ArrayList<>();

                                if(source != null){

                                    stops.add(
                                            source
                                    );
                                }

                                if(destination != null){

                                    stops.add(
                                            destination
                                    );
                                }

                                routeStops.put(
                                        routeName,
                                        stops
                                );
                            }

                            ArrayAdapter<String> adapter =
                                    new ArrayAdapter<>(
                                            bookingActivity.this,
                                            android.R.layout.simple_spinner_dropdown_item,
                                            routes
                                    );

                            spinnerRoute.setAdapter(
                                    adapter
                            );

                            spinnerRoute
                                    .setOnItemSelectedListener(
                                            new android.widget.AdapterView.OnItemSelectedListener() {

                                                @Override
                                                public void onItemSelected(
                                                        android.widget.AdapterView<?> parent,
                                                        android.view.View view,
                                                        int position,
                                                        long id
                                                ) {

                                                    selectedRouteName =
                                                            routes.get(
                                                                    position
                                                            );

                                                    selectedBusNumber =
                                                            busNumbers.get(
                                                                    position
                                                            );

                                                    pricePerStop =
                                                            routePrices.get(
                                                                    position
                                                            );

                                                    loadStops(
                                                            selectedRouteName
                                                    );

                                                    loadSeats(
                                                            position
                                                    );
                                                }

                                                @Override
                                                public void onNothingSelected(
                                                        android.widget.AdapterView<?> parent
                                                ) {

                                                }
                                            }
                                    );
                        }
                )
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            bookingActivity.this,
                            e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void loadStops(
            String routeName
    ) {

        ArrayList<String> stops =
                routeStops.get(
                        routeName
                );

        if(stops == null
                || stops.isEmpty()){

            Toast.makeText(
                    this,
                    "No Stops Available",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        ArrayAdapter<String> stopAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        stops
                );

        spinnerFrom.setAdapter(
                stopAdapter
        );

        spinnerTo.setAdapter(
                stopAdapter
        );
    }

    private void setupDatePicker() {

        btnDate.setOnClickListener(v -> {

            Calendar calendar =
                    Calendar.getInstance();

            DatePickerDialog picker =
                    new DatePickerDialog(
                            this,
                            (view,
                             year,
                             month,
                             dayOfMonth) -> {

                                selectedDate =
                                        year + "-" +
                                                String.format(
                                                        "%02d",
                                                        month + 1
                                                ) +
                                                "-" +
                                                String.format(
                                                        "%02d",
                                                        dayOfMonth
                                                );

                                btnDate.setText(
                                        selectedDate
                                );

                            },
                            calendar.get(
                                    Calendar.YEAR
                            ),
                            calendar.get(
                                    Calendar.MONTH
                            ),
                            calendar.get(
                                    Calendar.DAY_OF_MONTH
                            )
                    );

            picker.show();
        });
    }

    private void setupTimePicker() {

        btnTime.setOnClickListener(v -> {

            Calendar calendar =
                    Calendar.getInstance();

            TimePickerDialog dialog =
                    new TimePickerDialog(
                            this,
                            (view,
                             hour,
                             minute) -> {

                                selectedTime =
                                        String.format(
                                                "%02d:%02d",
                                                hour,
                                                minute
                                        );

                                btnTime.setText(
                                        selectedTime
                                );
                            },
                            calendar.get(
                                    Calendar.HOUR_OF_DAY
                            ),
                            calendar.get(
                                    Calendar.MINUTE
                            ),
                            true
                    );

            dialog.show();
        });
    }

    private void setupPassengerCounter() {

        btnPlus.setOnClickListener(v -> {

            passengerCount++;

            tvPassengerCount.setText(
                    String.valueOf(
                            passengerCount
                    )
            );
        });

        btnMinus.setOnClickListener(v -> {

            if(passengerCount > 1){

                passengerCount--;

                tvPassengerCount.setText(
                        String.valueOf(
                                passengerCount
                        )
                );
            }
        });
    }

    private void setupFareCalculation() {

        btnCalculateFare
                .setOnClickListener(v -> {

                    if(spinnerFrom.getCount() == 0
                            || spinnerTo.getCount() == 0){

                        Toast.makeText(
                                this,
                                "No Stops Available",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    int from =
                            spinnerFrom
                                    .getSelectedItemPosition();

                    int to =
                            spinnerTo
                                    .getSelectedItemPosition();

                    if(from >= to){

                        Toast.makeText(
                                this,
                                "Invalid Stops",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    int stopDifference =
                            to - from;

                    totalFare =
                            stopDifference
                                    * pricePerStop
                                    * passengerCount;

                    tvPrice.setText(
                            "Total Price: ₹"
                                    + totalFare
                    );
                });
    }

    private void setupBooking() {

        btnConfirmBooking
                .setOnClickListener(v -> {

                    if(totalFare <= 0){

                        Toast.makeText(
                                this,
                                "Calculate Fare First",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    if(selectedDate.isEmpty()
                            || selectedTime.isEmpty()){

                        Toast.makeText(
                                this,
                                "Select Date & Time",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    saveBooking();
                });
    }

    private void saveBooking() {

        if(auth.getCurrentUser() == null){

            Toast.makeText(
                    this,
                    "Login Required",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if(spinnerFrom.getSelectedItem() == null
                || spinnerTo.getSelectedItem() == null){

            Toast.makeText(
                    this,
                    "No Route Available",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String uid =
                auth.getCurrentUser()
                        .getUid();

        String ticketId =
                "TKT-"
                        + System.currentTimeMillis();

        String passengerName =
                tvUserName
                        .getText()
                        .toString();

        String from =
                spinnerFrom
                        .getSelectedItem()
                        .toString();

        String to =
                spinnerTo
                        .getSelectedItem()
                        .toString();

        HashMap<String,Object> booking =
                new HashMap<>();

        booking.put(
                "ticketId",
                ticketId
        );

        booking.put(
                "uid",
                uid
        );

        booking.put(
                "passengerName",
                passengerName
        );

        booking.put(
                "route",
                selectedRouteName
        );

        booking.put(
                "from",
                from
        );

        booking.put(
                "to",
                to
        );

        booking.put(
                "time",
                selectedTime
        );

        booking.put(
                "date",
                selectedDate
        );

        booking.put(
                "busNumber",
                selectedBusNumber
        );

        booking.put(
                "passengers",
                passengerCount
        );

        booking.put(
                "total",
                totalFare
        );

        booking.put(
                "status",
                "active"
        );

        booking.put(
                "createdAt",
                Timestamp.now()
        );

        db.collection("tickets")
                .document(ticketId)
                .set(booking)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            this,
                            "Booking Successful",
                            Toast.LENGTH_LONG
                    ).show();

                    resetForm();

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                });
    }

    private void loadSeats(
            int routePosition
    ) {

        if(routePosition >=
                availableSeats.size()) {

            tvSeats.setText(
                    "Available Seats: 0"
            );

            return;
        }

        int seats =
                availableSeats.get(
                        routePosition
                );

        tvSeats.setText(
                "Available Seats: "
                        + seats
        );
    }

    private void resetForm() {

        passengerCount = 1;

        totalFare = 0;

        tvPassengerCount.setText(
                "1"
        );

        tvPrice.setText(
                "Total Price: ₹0"
        );

        btnDate.setText(
                "Select Date"
        );

        btnTime.setText(
                "Select Time"
        );
    }

}