package com.example.smartbussystem;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BookingAdapter
        extends RecyclerView.Adapter<
        BookingAdapter.ViewHolder> {

    List<BookingModel> bookingList;

    public BookingAdapter(
            List<BookingModel> bookingList
    ) {
        this.bookingList =
                bookingList;
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvTicketId,
                tvRoute,
                tvBus,
                tvFromTo,
                tvPassengers,
                tvDate,
                tvTime,
                tvTotal;

        Button btnViewTicket;

        public ViewHolder(
                @NonNull View itemView
        ) {
            super(itemView);

            tvTicketId =
                    itemView.findViewById(
                            R.id.tvTicketId
                    );

            tvRoute =
                    itemView.findViewById(
                            R.id.tvRoute
                    );

            tvBus =
                    itemView.findViewById(
                            R.id.tvBus
                    );

            tvFromTo =
                    itemView.findViewById(
                            R.id.tvFromTo
                    );

            tvPassengers =
                    itemView.findViewById(
                            R.id.tvPassengers
                    );

            tvDate =
                    itemView.findViewById(
                            R.id.tvDate
                    );

            tvTime =
                    itemView.findViewById(
                            R.id.tvTime
                    );

            tvTotal =
                    itemView.findViewById(
                            R.id.tvTotal
                    );

            btnViewTicket =
                    itemView.findViewById(
                            R.id.btnViewTicket
                    );
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater.from(
                        parent.getContext()
                ).inflate(
                        R.layout.booking_item,
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

        BookingModel booking =
                bookingList.get(position);

        holder.tvTicketId.setText(
                "Ticket ID: "
                        + booking.getTicketId()
        );

        holder.tvRoute.setText(
                "Route: "
                        + booking.getRoute()
        );

        holder.tvBus.setText(
                "Bus: "
                        + booking.getBusNumber()
        );

        holder.tvFromTo.setText(
                booking.getFrom()
                        + " → "
                        + booking.getTo()
        );

        holder.tvPassengers.setText(
                "Passengers: "
                        + booking.getPassengers()
        );

        holder.tvDate.setText(
                "Date: "
                        + booking.getDate()
        );

        holder.tvTime.setText(
                "Time: "
                        + booking.getTime()
        );

        holder.tvTotal.setText(
                "₹"
                        + booking.getTotal()
        );

        holder.btnViewTicket
                .setOnClickListener(v -> {

                    Context context =
                            v.getContext();

                    Intent intent =
                            new Intent(
                                    context,
                                    TicketViewActivity.class
                            );

                    intent.putExtra(
                            "ticketId",
                            booking.getTicketId()
                    );

                    context.startActivity(
                            intent
                    );

                });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }
}