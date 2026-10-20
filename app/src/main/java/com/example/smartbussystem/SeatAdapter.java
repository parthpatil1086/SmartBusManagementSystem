package com.example.smartbussystem;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class SeatAdapter
        extends RecyclerView.Adapter<SeatAdapter.ViewHolder> {

    Context context;
    ArrayList<SeatModel> seatList;

    FirebaseFirestore db =
            FirebaseFirestore.getInstance();

    public SeatAdapter(
            Context context,
            ArrayList<SeatModel> seatList
    ) {
        this.context = context;
        this.seatList = seatList;
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
                                R.layout.seat_item,
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

        SeatModel seat =
                seatList.get(position);

        holder.tvDate.setText(
                "Date : " + seat.getDate()
        );

        holder.tvTotal.setText(
                "Total Seats : "
                        + seat.getTotalSeats()
        );

        holder.tvBooked.setText(
                "Booked : "
                        + seat.getBookedSeats()
        );

        holder.tvAvailable.setText(
                "Available : "
                        + seat.getAvailableSeats()
        );

        holder.btnReset.setOnClickListener(v -> {

            new AlertDialog.Builder(context)

                    .setTitle("Reset Seats")

                    .setMessage(
                            "Reset booked seats for "
                                    + seat.getDate()
                                    + " ?"
                    )

                    .setPositiveButton(
                            "Yes",
                            (dialog, which) -> {

                                resetSeats(
                                        seat,
                                        holder.getAdapterPosition()
                                );

                            })

                    .setNegativeButton(
                            "No",
                            null
                    )

                    .show();

        });
    }

    private void resetSeats(
            SeatModel seat,
            int position
    ) {

        String docId =
                seat.getRoute()
                        + "_"
                        + seat.getDate();

        db.collection("seats")
                .document(docId)
                .delete()
                .addOnSuccessListener(
                        unused -> {

                            seatList.remove(
                                    position
                            );

                            notifyItemRemoved(
                                    position
                            );

                            notifyItemRangeChanged(
                                    position,
                                    seatList.size()
                            );

                            Toast.makeText(
                                    context,
                                    "Seats Reset Successfully",
                                    Toast.LENGTH_SHORT
                            ).show();

                        })
                .addOnFailureListener(
                        e -> Toast.makeText(
                                context,
                                e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
    }

    @Override
    public int getItemCount() {
        return seatList.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvDate;
        TextView tvTotal;
        TextView tvBooked;
        TextView tvAvailable;

        Button btnReset;

        public ViewHolder(
                @NonNull View itemView
        ) {
            super(itemView);

            tvDate =
                    itemView.findViewById(
                            R.id.tvDate
                    );

            tvTotal =
                    itemView.findViewById(
                            R.id.tvTotal
                    );

            tvBooked =
                    itemView.findViewById(
                            R.id.tvBooked
                    );

            tvAvailable =
                    itemView.findViewById(
                            R.id.tvAvailable
                    );

            btnReset =
                    itemView.findViewById(
                            R.id.btnReset
                    );
        }
    }
}