package com.example.smartbussystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ExpenseAdapter extends
        RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {

    Context context;
    ArrayList<ExpenseModel> list;

    public ExpenseAdapter(
            Context context,
            ArrayList<ExpenseModel> list
    ) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater.from(context)
                        .inflate(
                                R.layout.item_expense,
                                parent,
                                false
                        );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        ExpenseModel model =
                list.get(position);

        holder.tvRoute.setText(
                "Route : "
                        + model.getRouteName()
        );

        holder.tvBus.setText(
                "Bus : "
                        + model.getBusNumber()
        );

        holder.tvDate.setText(
                "Date : "
                        + model.getExpenseDate()
        );

        holder.tvRevenue.setText(
                "Revenue : ₹"
                        + model.getRevenue()
        );

        holder.tvExpense.setText(
                "Expense : ₹"
                        + model.getTotalExpense()
        );

        holder.tvProfit.setText(
                "Profit : ₹"
                        + model.getNetProfit()
        );
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvRoute,
                tvBus,
                tvDate,
                tvRevenue,
                tvExpense,
                tvProfit;

        public ViewHolder(
                @NonNull View itemView
        ) {
            super(itemView);

            tvRoute =
                    itemView.findViewById(
                            R.id.tvRoute
                    );

            tvBus =
                    itemView.findViewById(
                            R.id.tvBus
                    );

            tvDate =
                    itemView.findViewById(
                            R.id.tvDate
                    );

            tvRevenue =
                    itemView.findViewById(
                            R.id.tvRevenue
                    );

            tvExpense =
                    itemView.findViewById(
                            R.id.tvExpense
                    );

            tvProfit =
                    itemView.findViewById(
                            R.id.tvProfit
                    );
        }
    }
}