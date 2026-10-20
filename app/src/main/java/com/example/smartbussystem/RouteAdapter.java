package com.example.smartbussystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RouteAdapter
        extends RecyclerView.Adapter<RouteAdapter.RouteViewHolder> {

    Context context;

    ArrayList<RouteModel> routeList;

    OnRouteClickListener listener;

    public interface OnRouteClickListener {
        void onRouteClick(RouteModel route);
    }

    public RouteAdapter(
            Context context,
            ArrayList<RouteModel> routeList,
            OnRouteClickListener listener
    ) {
        this.context = context;
        this.routeList = routeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater.from(context)
                        .inflate(
                                R.layout.route_item,
                                parent,
                                false
                        );

        return new RouteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull RouteViewHolder holder,
            int position
    ) {

        RouteModel route =
                routeList.get(position);

        holder.tvRouteName.setText(
                route.getRouteName()
        );

        holder.itemView.setOnClickListener(v -> {

            listener.onRouteClick(route);

        });
    }

    @Override
    public int getItemCount() {
        return routeList.size();
    }

    public static class RouteViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvRouteName;

        public RouteViewHolder(
                @NonNull View itemView
        ) {
            super(itemView);

            tvRouteName =
                    itemView.findViewById(
                            R.id.tvRouteName
                    );
        }
    }
}