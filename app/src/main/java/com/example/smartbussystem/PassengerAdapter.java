package com.example.smartbussystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PassengerAdapter
        extends RecyclerView.Adapter<
        PassengerAdapter.ViewHolder> {

    Context context;
    ArrayList<PassengerModel> list;

    public PassengerAdapter(
            Context context,
            ArrayList<PassengerModel> list
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
                                R.layout.passenger_item,
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

        PassengerModel model =
                list.get(position);

        holder.ticketId.setText(
                "Ticket ID: "
                        + model.getTicketId()
        );

        holder.passengerName.setText(
                "Passenger: "
                        + model.getPassengerName()
        );

        holder.route.setText(
                "Route: "
                        + model.getRoute()
        );

        holder.fromTo.setText(
                "From → To: "
                        + model.getFrom()
                        + " → "
                        + model.getTo()
        );

        holder.passengers.setText(
                "Passengers: "
                        + model.getPassengers()
        );

        holder.date.setText(
                "Date: "
                        + model.getDate()
        );

        holder.time.setText(
                "Time: "
                        + model.getTime()
        );

        holder.total.setText(
                "₹"
                        + model.getTotal()
        );
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView ticketId,
                passengerName,
                route,
                fromTo,
                passengers,
                date,
                time,
                total;

        public ViewHolder(
                @NonNull View itemView
        ) {
            super(itemView);

            ticketId =
                    itemView.findViewById(
                            R.id.tvTicketId
                    );

            passengerName =
                    itemView.findViewById(
                            R.id.tvPassengerName
                    );

            route =
                    itemView.findViewById(
                            R.id.tvRoute
                    );

            fromTo =
                    itemView.findViewById(
                            R.id.tvFromTo
                    );

            passengers =
                    itemView.findViewById(
                            R.id.tvPassengers
                    );

            date =
                    itemView.findViewById(
                            R.id.tvDate
                    );

            time =
                    itemView.findViewById(
                            R.id.tvTime
                    );

            total =
                    itemView.findViewById(
                            R.id.tvTotal
                    );
        }
    }
}