package com.example.smartbussystem;

public class ProfitRouteModel {

    String routeName;
    int profitAmount;

    public ProfitRouteModel(
            String routeName,
            int profitAmount
    ) {
        this.routeName =
                routeName;

        this.profitAmount =
                profitAmount;
    }

    public String getRouteName() {
        return routeName;
    }

    public int getProfitAmount() {
        return profitAmount;
    }
}