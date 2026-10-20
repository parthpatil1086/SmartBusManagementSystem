package com.example.smartbussystem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RegisteredPassengerAdapter extends
        RecyclerView.Adapter<
                RegisteredPassengerAdapter.ViewHolder> {

    ArrayList<RegisteredPassengerModel> list;

    public RegisteredPassengerAdapter(
            ArrayList<RegisteredPassengerModel> list
    ) {
        this.list = list;
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
                        R.layout.item_registered_passenger,
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

        RegisteredPassengerModel p =
                list.get(position);

        holder.tvName.setText(
                p.getName()
        );

        holder.tvEmail.setText(
                p.getEmail()
        );

        holder.tvPhone.setText(
                p.getPhone()
        );
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvName,
                tvEmail,
                tvPhone;

        public ViewHolder(
                @NonNull View itemView
        ) {
            super(itemView);

            tvName =
                    itemView.findViewById(
                            R.id.tvName
                    );

            tvEmail =
                    itemView.findViewById(
                            R.id.tvEmail
                    );

            tvPhone =
                    itemView.findViewById(
                            R.id.tvPhone
                    );
        }
    }
}