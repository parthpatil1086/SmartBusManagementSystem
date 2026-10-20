package com.example.smartbussystem;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class AddExpenseActivity
        extends AppCompatActivity {

    Spinner spinnerRoute;

    TextView tvBusNumber;
    TextView tvRevenue;
    TextView tvTotalExpense;
    TextView tvNetProfit;
    TextView tvSelectedDate;

    EditText etFuelExpense;
    EditText etTollExpense;
    EditText etDriverExpense;
    EditText etCleanerExpense;
    EditText etMaintenanceExpense;
    EditText etOtherExpense;

    Button btnSelectDate;
    Button btnCalculate;
    Button btnSaveExpense;

    FirebaseFirestore db;

    ArrayList<String> routes =
            new ArrayList<>();

    ArrayList<String> busNumbers =
            new ArrayList<>();

    ArrayList<String> routeIds =
            new ArrayList<>();

    String selectedRoute = "";
    String selectedBusNumber = "";
    String selectedDate = "";

    int revenue = 0;
    int totalExpense = 0;
    int netProfit = 0;

    boolean isCalculated = false;

    private boolean validateExpenses() {

        return getValue(etFuelExpense) > 0
                || getValue(etTollExpense) > 0
                || getValue(etDriverExpense) > 0
                || getValue(etCleanerExpense) > 0
                || getValue(etMaintenanceExpense) > 0
                || getValue(etOtherExpense) > 0;
    }

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
                R.layout.activity_add_expense
        );

        db =
                FirebaseFirestore
                        .getInstance();

        initViews();

        loadRoutes();

        setupDatePicker();

        setupCalculate();

        setupSave();
    }

    private void initViews() {

        spinnerRoute =
                findViewById(
                        R.id.spinnerRoute
                );

        tvBusNumber =
                findViewById(
                        R.id.tvBusNumber
                );

        tvRevenue =
                findViewById(
                        R.id.tvRevenue
                );

        tvTotalExpense =
                findViewById(
                        R.id.tvTotalExpense
                );

        tvNetProfit =
                findViewById(
                        R.id.tvNetProfit
                );

        tvSelectedDate =
                findViewById(
                        R.id.tvSelectedDate
                );

        etFuelExpense =
                findViewById(
                        R.id.etFuelExpense
                );

        etTollExpense =
                findViewById(
                        R.id.etTollExpense
                );

        etDriverExpense =
                findViewById(
                        R.id.etDriverExpense
                );

        etCleanerExpense =
                findViewById(
                        R.id.etCleanerExpense
                );

        etMaintenanceExpense =
                findViewById(
                        R.id.etMaintenanceExpense
                );

        etOtherExpense =
                findViewById(
                        R.id.etOtherExpense
                );

        btnSelectDate =
                findViewById(
                        R.id.btnSelectDate
                );

        btnCalculate =
                findViewById(
                        R.id.btnCalculate
                );

        btnSaveExpense =
                findViewById(
                        R.id.btnSaveExpense
                );
    }

    private void loadRoutes() {

        db.collection("routes")
                .get()
                .addOnSuccessListener(query -> {

                    routes.clear();
                    busNumbers.clear();
                    routeIds.clear();

                    for (QueryDocumentSnapshot doc : query) {

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

                        if (routeName != null) {

                            routes.add(
                                    routeName
                            );
                        }

                        if (busNumber != null) {

                            busNumbers.add(
                                    busNumber
                            );
                        }
                    }

                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<>(
                                    AddExpenseActivity.this,
                                    android.R.layout.simple_spinner_dropdown_item,
                                    routes
                            );

                    spinnerRoute.setAdapter(
                            adapter
                    );

                    spinnerRoute
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

                                            selectedRoute =
                                                    routes.get(
                                                            position
                                                    );
                                            isCalculated = false;

                                            selectedBusNumber =
                                                    busNumbers.get(
                                                            position
                                                    );

                                            tvBusNumber.setText(
                                                    "Bus Number : "
                                                            + selectedBusNumber
                                            );
                                        }

                                        @Override
                                        public void onNothingSelected(
                                                android.widget.AdapterView<?> parent
                                        ) {

                                        }
                                    });
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                });
    }

    private void setupDatePicker() {

        btnSelectDate
                .setOnClickListener(v -> {

                    Calendar calendar =
                            Calendar.getInstance();

                    DatePickerDialog dialog =
                            new DatePickerDialog(
                                    this,
                                    (view,
                                     year,
                                     month,
                                     dayOfMonth) -> {

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
                                                        dayOfMonth
                                                );
                                        isCalculated = false;

                                        tvSelectedDate
                                                .setText(
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

                });
    }

    private int getValue(
            EditText editText
    ) {

        String value =
                editText.getText()
                        .toString()
                        .trim();

        if(value.isEmpty()) {

            return 0;
        }

        return Integer.parseInt(
                value
        );
    }

    private void calculateRevenue() {

        revenue = 0;

        db.collection("tickets")
                .whereEqualTo(
                        "route",
                        selectedRoute
                )
                .whereEqualTo(
                        "date",
                        selectedDate
                )
                .get()
                .addOnSuccessListener(query -> {

                    for(QueryDocumentSnapshot doc
                            : query) {

                        Long total =
                                doc.getLong(
                                        "total"
                                );

                        if(total != null) {

                            revenue +=
                                    total.intValue();
                        }
                    }
                    if(revenue <= 0){

                        Toast.makeText(
                                this,
                                "No bookings found for selected route and date",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    calculateProfit();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                });
    }

    private void calculateProfit() {

        isCalculated = true;

        int fuel =
                getValue(
                        etFuelExpense
                );

        int toll =
                getValue(
                        etTollExpense
                );

        int driver =
                getValue(
                        etDriverExpense
                );

        int cleaner =
                getValue(
                        etCleanerExpense
                );

        int maintenance =
                getValue(
                        etMaintenanceExpense
                );

        int other =
                getValue(
                        etOtherExpense
                );

        totalExpense =
                fuel
                        + toll
                        + driver
                        + cleaner
                        + maintenance
                        + other;

        netProfit =
                revenue
                        - totalExpense;

        tvRevenue.setText(
                "Revenue : ₹"
                        + revenue
        );

        tvTotalExpense.setText(
                "Total Expense : ₹"
                        + totalExpense
        );

        tvNetProfit.setText(
                "Net Profit : ₹"
                        + netProfit
        );
    }

    private void setupCalculate() {

        btnCalculate
                .setOnClickListener(v -> {

                    if(selectedDate.isEmpty()) {

                        Toast.makeText(
                                this,
                                "Please Select Date",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    if(selectedRoute.isEmpty()) {

                        Toast.makeText(
                                this,
                                "Please Select Route",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    calculateRevenue();

                });
    }

    private void setupSave() {

        btnSaveExpense.setOnClickListener(v -> {

            if(selectedDate.isEmpty()) {

                Toast.makeText(
                        this,
                        "Please Select Date",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if(selectedRoute.isEmpty()) {

                Toast.makeText(
                        this,
                        "Please Select Route",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if(!isCalculated){

                Toast.makeText(
                        this,
                        "Please Calculate Profit First",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if(!validateExpenses()){

                Toast.makeText(
                        this,
                        "Enter At Least One Expense",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            checkDuplicateExpense();

        });

    }
    private void checkDuplicateExpense() {

        db.collection("expenses")
                .whereEqualTo(
                        "routeName",
                        selectedRoute
                )
                .whereEqualTo(
                        "expenseDate",
                        selectedDate
                )
                .get()
                .addOnSuccessListener(query -> {

                    if(!query.isEmpty()) {

                        Toast.makeText(
                                this,
                                "Expense already exists for this route and date",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    saveExpense();

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                });

    }

    private void saveExpense() {

        String expenseId =
                selectedRoute.replace(" ", "_")
                        + "_"
                        + selectedDate;

        HashMap<String,Object>
                expense =
                new HashMap<>();

        expense.put(
                "expenseId",
                expenseId
        );

        expense.put(
                "routeName",
                selectedRoute
        );

        expense.put(
                "busNumber",
                selectedBusNumber
        );

        expense.put(
                "expenseDate",
                selectedDate
        );

        expense.put(
                "revenue",
                revenue
        );

        expense.put(
                "fuelExpense",
                getValue(
                        etFuelExpense
                )
        );

        expense.put(
                "tollExpense",
                getValue(
                        etTollExpense
                )
        );

        expense.put(
                "driverExpense",
                getValue(
                        etDriverExpense
                )
        );

        expense.put(
                "cleanerExpense",
                getValue(
                        etCleanerExpense
                )
        );

        expense.put(
                "maintenanceExpense",
                getValue(
                        etMaintenanceExpense
                )
        );

        expense.put(
                "otherExpense",
                getValue(
                        etOtherExpense
                )
        );

        expense.put(
                "totalExpense",
                totalExpense
        );

        expense.put(
                "netProfit",
                netProfit
        );

        expense.put(
                "createdAt",
                Timestamp.now()
        );

        calculateProfit();

        db.collection("expenses")
                .document(expenseId)
                .set(expense)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            this,
                            "Expense Saved Successfully",
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

    private void resetForm() {

        etFuelExpense.setText("");
        etTollExpense.setText("");
        etDriverExpense.setText("");
        etCleanerExpense.setText("");
        etMaintenanceExpense.setText("");
        etOtherExpense.setText("");
        selectedDate = "";

        btnSelectDate.setText("Select Date");

        revenue = 0;
        totalExpense = 0;
        netProfit = 0;
        isCalculated = false;

        tvRevenue.setText(
                "Revenue : ₹0"
        );

        tvTotalExpense.setText(
                "Total Expense : ₹0"
        );

        tvNetProfit.setText(
                "Net Profit : ₹0"
        );
    }

}