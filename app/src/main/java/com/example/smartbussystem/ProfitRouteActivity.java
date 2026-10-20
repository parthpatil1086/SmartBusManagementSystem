package com.example.smartbussystem;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class ProfitRouteActivity
        extends AppCompatActivity {

    private TextView tvTodayProfit;
    private TextView tvSelectedDate;

    private Spinner spinnerFilter;

    private Button btnPickDate;

    private RecyclerView recyclerProfit;

    private FirebaseFirestore db;

    private ArrayList<ProfitRouteModel>
            profitList;

    private ProfitRouteAdapter adapter;

    private String selectedDate = "";

    private int overallProfit = 0;

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        AppCompatDelegate
                .setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_NO
                );

        setContentView(
                R.layout.activity_profitroute
        );

        db =
                FirebaseFirestore
                        .getInstance();

        initViews();

        setupRecyclerView();

        setupFilterSpinner();

        setupButtons();

        selectedDate =
                getTodayDate();

        tvSelectedDate.setText(
                selectedDate
        );

        loadProfitData(
                selectedDate
        );
    }

    private void initViews() {

        tvTodayProfit =
                findViewById(
                        R.id.tvTodayProfit
                );

        tvSelectedDate =
                findViewById(
                        R.id.tvSelectedDate
                );

        spinnerFilter =
                findViewById(
                        R.id.spinnerFilter
                );

        btnPickDate =
                findViewById(
                        R.id.btnPickDate
                );

        recyclerProfit =
                findViewById(
                        R.id.recyclerProfit
                );
    }

    private void setupRecyclerView() {

        profitList =
                new ArrayList<>();

        adapter =
                new ProfitRouteAdapter(
                        this,
                        profitList
                );

        recyclerProfit.setLayoutManager(
                new LinearLayoutManager(
                        this
                )
        );

        recyclerProfit.setAdapter(
                adapter
        );
    }

    private void setupButtons() {

        btnPickDate.setOnClickListener(v -> {

            openDatePicker();

        });
    }

    private void setupFilterSpinner() {

        String[] filters = {

                "Today",
                "Yesterday",
                "This Week",
                "This Month",
                "Custom Date"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout
                                .simple_spinner_dropdown_item,
                        filters
                );

        spinnerFilter.setAdapter(
                adapter
        );

        spinnerFilter
                .setOnItemSelectedListener(
                        new AdapterView
                                .OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(
                                    AdapterView<?> parent,
                                    View view,
                                    int position,
                                    long id
                            ) {

                                String filter =
                                        parent
                                                .getItemAtPosition(
                                                        position
                                                )
                                                .toString();

                                switch (filter) {

                                    case "Today":

                                        selectedDate =
                                                getTodayDate();

                                        tvSelectedDate
                                                .setText(
                                                        selectedDate
                                                );

                                        loadProfitData(
                                                selectedDate
                                        );

                                        break;

                                    case "Yesterday":

                                        selectedDate =
                                                getYesterdayDate();

                                        tvSelectedDate
                                                .setText(
                                                        selectedDate
                                                );

                                        loadProfitData(
                                                selectedDate
                                        );

                                        break;

                                    case "This Week":

                                        loadWeekProfit();

                                        break;

                                    case "This Month":

                                        loadMonthProfit();

                                        break;

                                    case "Custom Date":

                                        openDatePicker();

                                        break;
                                }
                            }

                            @Override
                            public void onNothingSelected(
                                    AdapterView<?> parent
                            ) {

                            }
                        }
                );
    }

    private void openDatePicker() {

        Calendar calendar =
                Calendar.getInstance();

        DatePickerDialog dialog =
                new DatePickerDialog(
                        this,
                        (view,
                         year,
                         month,
                         day) -> {

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

                            tvSelectedDate.setText(
                                    selectedDate
                            );

                            loadProfitData(
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

    private void loadProfitData(
            String date
    ) {

        profitList.clear();

        overallProfit = 0;

        HashMap<String, Integer>
                revenueMap =
                new HashMap<>();

        HashMap<String, Integer>
                expenseMap =
                new HashMap<>();

        db.collection("tickets")
                .whereEqualTo(
                        "date",
                        date
                )
                .get()
                .addOnSuccessListener(ticketQuery -> {

                    for (QueryDocumentSnapshot doc
                            : ticketQuery) {

                        String route =
                                doc.getString(
                                        "route"
                                );

                        Long total =
                                doc.getLong(
                                        "total"
                                );

                        int revenue =
                                total != null
                                        ? total.intValue()
                                        : 0;

                        if (route == null)
                            continue;

                        if (revenueMap
                                .containsKey(route)) {

                            revenueMap.put(
                                    route,
                                    revenueMap.get(route)
                                            + revenue
                            );

                        } else {

                            revenueMap.put(
                                    route,
                                    revenue
                            );
                        }
                    }

                    loadExpensesForDate(
                            revenueMap,
                            expenseMap,
                            date
                    );
                });
    }

    private void loadExpensesForDate(
            HashMap<String, Integer> revenueMap,
            HashMap<String, Integer> expenseMap,
            String date
    ) {

        db.collection("expenses")
                .whereEqualTo(
                        "expenseDate",
                        date
                )
                .get()
                .addOnSuccessListener(expenseQuery -> {

                    for (QueryDocumentSnapshot doc
                            : expenseQuery) {

                        String route =
                                doc.getString(
                                        "routeName"
                                );

                        Long totalExpense =
                                doc.getLong(
                                        "totalExpense"
                                );

                        int expense =
                                totalExpense != null
                                        ? totalExpense.intValue()
                                        : 0;

                        if (route == null)
                            continue;

                        expenseMap.put(
                                route,
                                expense
                        );
                    }

                    calculateFinalProfit(
                            revenueMap,
                            expenseMap
                    );
                });
    }

    private void calculateFinalProfit(
            HashMap<String, Integer> revenueMap,
            HashMap<String, Integer> expenseMap
    ) {

        profitList.clear();

        overallProfit = 0;

        for (String route
                : revenueMap.keySet()) {

            int revenue =
                    revenueMap.get(route);

            int expense =
                    expenseMap.containsKey(route)
                            ? expenseMap.get(route)
                            : 0;

            int profit =
                    revenue - expense;

            overallProfit +=
                    profit;

            profitList.add(
                    new ProfitRouteModel(
                            route,
                            profit
                    )
            );
        }

        adapter.notifyDataSetChanged();

        tvTodayProfit.setText(
                "₹" + overallProfit
        );
    }

    private void loadWeekProfit() {

        profitList.clear();

        overallProfit = 0;

        HashMap<String, Integer>
                revenueMap =
                new HashMap<>();

        HashMap<String, Integer>
                expenseMap =
                new HashMap<>();

        String weekStart =
                getWeekStartDate();

        String today =
                getTodayDate();

        db.collection("tickets")
                .get()
                .addOnSuccessListener(ticketQuery -> {

                    for (QueryDocumentSnapshot doc
                            : ticketQuery) {

                        String date =
                                doc.getString(
                                        "date"
                                );

                        if (date == null)
                            continue;

                        if (date.compareTo(
                                weekStart
                        ) < 0
                                ||
                                date.compareTo(
                                        today
                                ) > 0)
                            continue;

                        String route =
                                doc.getString(
                                        "route"
                                );

                        Long total =
                                doc.getLong(
                                        "total"
                                );

                        int revenue =
                                total != null
                                        ? total.intValue()
                                        : 0;

                        if (route == null)
                            continue;

                        if (revenueMap
                                .containsKey(route)) {

                            revenueMap.put(
                                    route,
                                    revenueMap.get(route)
                                            + revenue
                            );

                        } else {

                            revenueMap.put(
                                    route,
                                    revenue
                            );
                        }
                    }

                    loadWeekExpenses(
                            revenueMap,
                            expenseMap,
                            weekStart,
                            today
                    );
                });
    }

    private void loadWeekExpenses(
            HashMap<String, Integer> revenueMap,
            HashMap<String, Integer> expenseMap,
            String weekStart,
            String today
    ) {

        db.collection("expenses")
                .get()
                .addOnSuccessListener(expenseQuery -> {

                    for (QueryDocumentSnapshot doc
                            : expenseQuery) {

                        String date =
                                doc.getString(
                                        "expenseDate"
                                );

                        if (date == null)
                            continue;

                        if (date.compareTo(
                                weekStart
                        ) < 0
                                ||
                                date.compareTo(
                                        today
                                ) > 0)
                            continue;

                        String route =
                                doc.getString(
                                        "routeName"
                                );

                        Long totalExpense =
                                doc.getLong(
                                        "totalExpense"
                                );

                        int expense =
                                totalExpense != null
                                        ? totalExpense.intValue()
                                        : 0;

                        if (route == null)
                            continue;

                        expenseMap.put(
                                route,
                                expenseMap.getOrDefault(
                                        route,
                                        0
                                ) + expense
                        );
                    }

                    calculateFinalProfit(
                            revenueMap,
                            expenseMap
                    );
                });
    }

    private void loadMonthProfit() {

        profitList.clear();

        overallProfit = 0;

        HashMap<String, Integer>
                revenueMap =
                new HashMap<>();

        HashMap<String, Integer>
                expenseMap =
                new HashMap<>();

        String monthStart =
                getMonthStartDate();

        String today =
                getTodayDate();

        db.collection("tickets")
                .get()
                .addOnSuccessListener(ticketQuery -> {

                    for (QueryDocumentSnapshot doc
                            : ticketQuery) {

                        String date =
                                doc.getString(
                                        "date"
                                );

                        if (date == null)
                            continue;

                        if (date.compareTo(
                                monthStart
                        ) < 0
                                ||
                                date.compareTo(
                                        today
                                ) > 0)
                            continue;

                        String route =
                                doc.getString(
                                        "route"
                                );

                        Long total =
                                doc.getLong(
                                        "total"
                                );

                        int revenue =
                                total != null
                                        ? total.intValue()
                                        : 0;

                        if (route == null)
                            continue;

                        if (revenueMap
                                .containsKey(route)) {

                            revenueMap.put(
                                    route,
                                    revenueMap.get(route)
                                            + revenue
                            );

                        } else {

                            revenueMap.put(
                                    route,
                                    revenue
                            );
                        }
                    }

                    loadMonthExpenses(
                            revenueMap,
                            expenseMap,
                            monthStart,
                            today
                    );
                });
    }

    private void loadMonthExpenses(
            HashMap<String, Integer> revenueMap,
            HashMap<String, Integer> expenseMap,
            String monthStart,
            String today
    ) {

        db.collection("expenses")
                .get()
                .addOnSuccessListener(expenseQuery -> {

                    for (QueryDocumentSnapshot doc
                            : expenseQuery) {

                        String date =
                                doc.getString(
                                        "expenseDate"
                                );

                        if (date == null)
                            continue;

                        if (date.compareTo(
                                monthStart
                        ) < 0
                                ||
                                date.compareTo(
                                        today
                                ) > 0)
                            continue;

                        String route =
                                doc.getString(
                                        "routeName"
                                );

                        Long totalExpense =
                                doc.getLong(
                                        "totalExpense"
                                );

                        int expense =
                                totalExpense != null
                                        ? totalExpense.intValue()
                                        : 0;

                        if (route == null)
                            continue;

                        expenseMap.put(
                                route,
                                expenseMap.getOrDefault(
                                        route,
                                        0
                                ) + expense
                        );
                    }

                    calculateFinalProfit(
                            revenueMap,
                            expenseMap
                    );
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

    private String getWeekStartDate() {

        Calendar calendar =
                Calendar.getInstance();

        calendar.set(
                Calendar.DAY_OF_WEEK,
                calendar.getFirstDayOfWeek()
        );

        return new SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
        ).format(
                calendar.getTime()
        );
    }

    private String getMonthStartDate() {

        Calendar calendar =
                Calendar.getInstance();

        calendar.set(
                Calendar.DAY_OF_MONTH,
                1
        );

        return new SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
        ).format(
                calendar.getTime()
        );
    }
}