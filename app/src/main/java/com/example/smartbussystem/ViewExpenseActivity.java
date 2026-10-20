package com.example.smartbussystem;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ViewExpenseActivity
        extends AppCompatActivity {

    RecyclerView recyclerExpense;

    TextView tvTotalProfit;

    FirebaseFirestore db;

    ArrayList<ExpenseModel> expenseList;

    ExpenseAdapter adapter;

    int overallProfit = 0;

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
                R.layout.activity_view_expense
        );

        db =
                FirebaseFirestore
                        .getInstance();

        recyclerExpense =
                findViewById(
                        R.id.recyclerExpense
                );

        tvTotalProfit =
                findViewById(
                        R.id.tvTotalProfit
                );

        expenseList =
                new ArrayList<>();

        adapter =
                new ExpenseAdapter(
                        this,
                        expenseList
                );

        recyclerExpense
                .setLayoutManager(
                        new LinearLayoutManager(
                                this
                        )
                );

        recyclerExpense
                .setAdapter(
                        adapter
                );

        loadExpenses();
    }

    private void loadExpenses() {

        overallProfit = 0;

        expenseList.clear();

        db.collection("expenses")
                .orderBy(
                        "createdAt",
                        Query.Direction.DESCENDING
                )
                .get()
                .addOnSuccessListener(query -> {

                    for(QueryDocumentSnapshot doc
                            : query) {

                        String expenseId =
                                doc.getString(
                                        "expenseId"
                                );

                        String routeName =
                                doc.getString(
                                        "routeName"
                                );

                        String busNumber =
                                doc.getString(
                                        "busNumber"
                                );

                        String expenseDate =
                                doc.getString(
                                        "expenseDate"
                                );

                        Long revenue =
                                doc.getLong(
                                        "revenue"
                                );

                        Long totalExpense =
                                doc.getLong(
                                        "totalExpense"
                                );

                        Long netProfit =
                                doc.getLong(
                                        "netProfit"
                                );

                        int profit =
                                netProfit != null
                                        ? netProfit.intValue()
                                        : 0;

                        overallProfit +=
                                profit;

                        expenseList.add(
                                new ExpenseModel(
                                        expenseId,
                                        routeName,
                                        busNumber,
                                        expenseDate,
                                        revenue != null
                                                ? revenue.intValue()
                                                : 0,
                                        totalExpense != null
                                                ? totalExpense.intValue()
                                                : 0,
                                        profit
                                )
                        );
                    }

                    adapter.notifyDataSetChanged();

                    tvTotalProfit.setText(
                            "Overall Profit : ₹"
                                    + overallProfit
                    );
                });
    }
}