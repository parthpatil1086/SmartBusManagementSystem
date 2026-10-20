package com.example.smartbussystem;

public class BusLocationModel {

    String busId;

    double latitude;

    double longitude;

    long timestamp;

    public BusLocationModel() {
    }

    public BusLocationModel(
            String busId,
            double latitude,
            double longitude,
            long timestamp
    ) {
        this.busId = busId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    public String getBusId() {
        return busId;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public long getTimestamp() {
        return timestamp;
    }
}