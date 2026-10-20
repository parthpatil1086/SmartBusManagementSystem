package com.example.smartbussystem;

public class PassengerCountModel {

    private String busId;
    private String routeName;
    private int passengerCount;

    public PassengerCountModel(String busId,
                               String routeName,
                               int passengerCount) {
        this.busId = busId;
        this.routeName = routeName;
        this.passengerCount = passengerCount;
    }

    public String getBusId() {
        return busId;
    }

    public String getRouteName() {
        return routeName;
    }

    public int getPassengerCount() {
        return passengerCount;
    }
}