package com.example.smartbussystem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PassengerCountAdapter
        extends RecyclerView.Adapter<PassengerCountAdapter.ViewHolder> {

    private final List<PassengerCountModel> list;

    public PassengerCountAdapter(List<PassengerCountModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_passenger_count,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        PassengerCountModel model = list.get(position);

        holder.busId.setText(
                model.getBusId()
        );

        holder.routeName.setText(
                model.getRouteName()
        );

        holder.passengerCount.setText(
                String.valueOf(
                        model.getPassengerCount()
                )
        );

        // Bus capacity = 60 seats
        holder.progressBar.setMax(60);

        holder.progressBar.setProgress(
                model.getPassengerCount()
        );
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView busId;
        TextView routeName;
        TextView passengerCount;
        ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            busId =
                    itemView.findViewById(
                            R.id.txtBusId
                    );

            routeName =
                    itemView.findViewById(
                            R.id.txtRouteName
                    );

            passengerCount =
                    itemView.findViewById(
                            R.id.txtPassengerCount
                    );

            progressBar =
                    itemView.findViewById(
                            R.id.progressOccupancy
                    );
        }
    }
}