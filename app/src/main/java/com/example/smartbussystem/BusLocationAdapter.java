package com.example.smartbussystem;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BusLocationAdapter
        extends RecyclerView.Adapter<BusLocationAdapter.ViewHolder> {

    Context context;

    ArrayList<BusLocationModel> list;

    public BusLocationAdapter(
            Context context,
            ArrayList<BusLocationModel> list
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
                                R.layout.item_bus_location,
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
        BusLocationModel model =
                list.get(position);

        holder.tvBusId.setText(
                model.getBusId()
        );

        holder.tvLatitude.setText(
                "Latitude : "
                        + model.getLatitude()
        );

        holder.tvLongitude.setText(
                "Longitude : "
                        + model.getLongitude()
        );

        holder.tvTimestamp.setText(
                "Timestamp : "
                        + model.getTimestamp()
        );

        holder.itemView.setOnClickListener(v -> {

            String uri =
                    "geo:"
                            + model.getLatitude()
                            + ","
                            + model.getLongitude()
                            + "?q="
                            + model.getLatitude()
                            + ","
                            + model.getLongitude();

            Intent intent =
                    new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(uri)
                    );

            intent.setPackage(
                    "com.google.android.apps.maps"
            );

            context.startActivity(intent);

        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvBusId;
        TextView tvLatitude;
        TextView tvLongitude;
        TextView tvTimestamp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvBusId =
                    itemView.findViewById(
                            R.id.tvBusId
                    );

            tvLatitude =
                    itemView.findViewById(
                            R.id.tvLatitude
                    );

            tvLongitude =
                    itemView.findViewById(
                            R.id.tvLongitude
                    );

            tvTimestamp =
                    itemView.findViewById(
                            R.id.tvTimestamp
                    );
        }
    }
}