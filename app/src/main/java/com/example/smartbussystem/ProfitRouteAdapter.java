package com.example.smartbussystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ProfitRouteAdapter
        extends RecyclerView.Adapter<
        ProfitRouteAdapter.ViewHolder> {

    Context context;

    ArrayList<ProfitRouteModel>
            profitList;

    public ProfitRouteAdapter(
            Context context,
            ArrayList<ProfitRouteModel>
                    profitList
    ) {

        this.context =
                context;

        this.profitList =
                profitList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater.from(
                                context
                        )
                        .inflate(
                                R.layout
                                        .profit_route_item,
                                parent,
                                false
                        );

        return new ViewHolder(
                view
        );
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        ProfitRouteModel model =
                profitList.get(
                        position
                );

        holder.tvRouteName
                .setText(
                        model
                                .getRouteName()
                );

        holder.tvProfit
                .setText(
                        "Profit: ₹"
                                + model
                                .getProfitAmount()
                );
    }

    @Override
    public int getItemCount() {
        return profitList.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvRouteName,
                tvProfit;

        public ViewHolder(
                @NonNull View itemView
        ) {
            super(itemView);

            tvRouteName =
                    itemView.findViewById(
                            R.id.tvRouteName
                    );

            tvProfit =
                    itemView.findViewById(
                            R.id.tvProfit
                    );
        }
    }
}