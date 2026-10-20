package com.example.smartbussystem;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class PassengerListActivity
        extends AppCompatActivity {

    RecyclerView recyclerView;

    Button btnBack,
            btnPickDate;

    Spinner spinnerDateFilter;

    TextView tvSelectedDate;

    FirebaseFirestore db;

    ArrayList<PassengerModel> list;

    PassengerAdapter adapter;

    String selectedDate = "";

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        AppCompatDelegate
                .setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_NO
                );

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_passenger_list
        );

        recyclerView =
                findViewById(
                        R.id.recyclerPassengers
                );

        btnBack =
                findViewById(
                        R.id.btnBack
                );

        btnPickDate =
                findViewById(
                        R.id.btnPickDate
                );

        spinnerDateFilter =
                findViewById(
                        R.id.spinnerDateFilter
                );

        tvSelectedDate =
                findViewById(
                        R.id.tvSelectedDate
                );

        db =
                FirebaseFirestore
                        .getInstance();

        list =
                new ArrayList<>();

        adapter =
                new PassengerAdapter(
                        this,
                        list
                );

        recyclerView
                .setLayoutManager(
                        new LinearLayoutManager(
                                this
                        )
                );

        recyclerView
                .setAdapter(
                        adapter
                );

        // Spinner Filters
        String[] filters = {
                "Today",
                "Yesterday",
                "Last 7 Days",
                "All Time",
                "Custom Date"
        };

        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout
                                .simple_spinner_dropdown_item,
                        filters
                );

        spinnerDateFilter
                .setAdapter(
                        spinnerAdapter
                );

        spinnerDateFilter
                .setOnItemSelectedListener(
                        new android.widget.AdapterView
                                .OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(
                                    android.widget.AdapterView<?> parent,
                                    android.view.View view,
                                    int position,
                                    long id
                            ) {

                                String selected =
                                        parent
                                                .getItemAtPosition(position)
                                                .toString();

                                switch (selected) {

                                    case "Today":

                                        selectedDate =
                                                getTodayDate();

                                        tvSelectedDate
                                                .setText(
                                                        "Showing: Today"
                                                );

                                        loadPassengers(
                                                selectedDate
                                        );

                                        break;

                                    case "Yesterday":

                                        selectedDate =
                                                getYesterdayDate();

                                        tvSelectedDate
                                                .setText(
                                                        "Showing: Yesterday"
                                                );

                                        loadPassengers(
                                                selectedDate
                                        );

                                        break;

                                    case "Last 7 Days":

                                        tvSelectedDate
                                                .setText(
                                                        "Showing: Last 7 Days"
                                                );

                                        loadLast7DaysPassengers();

                                        break;

                                    case "All Time":

                                        tvSelectedDate
                                                .setText(
                                                        "Showing: All Time"
                                                );

                                        loadAllPassengers();

                                        break;

                                    case "Custom Date":

                                        openDatePicker();

                                        break;
                                }
                            }

                            @Override
                            public void onNothingSelected(
                                    android.widget.AdapterView<?> parent
                            ) {

                            }
                        });

        btnPickDate
                .setOnClickListener(v -> {

                    openDatePicker();

                });

        // Default Today Data
        selectedDate =
                getTodayDate();

        loadPassengers(
                selectedDate
        );

        btnBack
                .setOnClickListener(v -> {

                    startActivity(
                            new Intent(
                                    PassengerListActivity.this,
                                    admindashboard.class
                            )
                    );

                    finish();

                });
    }

    private void openDatePicker() {

        Calendar calendar =
                Calendar.getInstance();

        DatePickerDialog dialog =
                new DatePickerDialog(
                        this,
                        (view, year, month, day) -> {

                            selectedDate =
                                    year + "-"
                                            + String.format(
                                            Locale.getDefault(),
                                            "%02d",
                                            month + 1
                                    )
                                            + "-"
                                            + String.format(
                                            Locale.getDefault(),
                                            "%02d",
                                            day
                                    );

                            tvSelectedDate
                                    .setText(
                                            "Showing: "
                                                    + selectedDate
                                    );

                            loadPassengers(
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

        dialog.show();
    }

    private void loadPassengers(
            String date
    ) {

        db.collection("tickets")
                .whereEqualTo(
                        "date",
                        date
                )
                .get()
                .addOnSuccessListener(
                        snapshots -> {

                            list.clear();

                            if(snapshots.isEmpty()) {

                                Toast.makeText(
                                        this,
                                        "No Passengers Found",
                                        Toast.LENGTH_SHORT
                                ).show();

                                adapter.notifyDataSetChanged();
                                return;
                            }

                            for(var doc :
                                    snapshots
                                            .getDocuments())
                            {

                                PassengerModel model =
                                        doc.toObject(
                                                PassengerModel.class
                                        );

                                list.add(
                                        model
                                );
                            }

                            adapter.notifyDataSetChanged();

                        });
    }

    private void loadLast7DaysPassengers() {

        list.clear();

        Calendar calendar =
                Calendar.getInstance();

        calendar.add(
                Calendar.DAY_OF_YEAR,
                -7
        );

        String last7DaysDate =
                new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                ).format(
                        calendar.getTime()
                );

        db.collection("tickets")
                .whereGreaterThanOrEqualTo(
                        "date",
                        last7DaysDate
                )
                .get()
                .addOnSuccessListener(
                        snapshots -> {

                            if(snapshots.isEmpty()) {

                                Toast.makeText(
                                        this,
                                        "No Data Found",
                                        Toast.LENGTH_SHORT
                                ).show();

                                adapter.notifyDataSetChanged();
                                return;
                            }

                            for(var doc :
                                    snapshots
                                            .getDocuments())
                            {

                                PassengerModel model =
                                        doc.toObject(
                                                PassengerModel.class
                                        );

                                list.add(
                                        model
                                );
                            }

                            adapter.notifyDataSetChanged();

                        });
    }

    private void loadAllPassengers() {

        list.clear();

        db.collection("tickets")
                .get()
                .addOnSuccessListener(
                        snapshots -> {

                            if(snapshots.isEmpty()) {

                                Toast.makeText(
                                        this,
                                        "No Data Found",
                                        Toast.LENGTH_SHORT
                                ).show();

                                adapter.notifyDataSetChanged();
                                return;
                            }

                            for(var doc :
                                    snapshots
                                            .getDocuments())
                            {

                                PassengerModel model =
                                        doc.toObject(
                                                PassengerModel.class
                                        );

                                list.add(
                                        model
                                );
                            }

                            adapter.notifyDataSetChanged();

                        });
    }

    private String getTodayDate() {

        return new SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
        ).format(
                Calendar.getInstance()
                        .getTime()
        );
    }

    private String getYesterdayDate() {

        Calendar calendar =
                Calendar.getInstance();

        calendar.add(
                Calendar.DAY_OF_YEAR,
                -1
        );

        return new SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
        ).format(
                calendar.getTime()
        );
    }
}