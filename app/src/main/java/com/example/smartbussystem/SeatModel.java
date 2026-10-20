package com.example.smartbussystem;

public class SeatModel {

    String route;
    String date;

    long totalSeats;
    long bookedSeats;

    public SeatModel() {
    }

    public SeatModel(
            String route,
            String date,
            long totalSeats,
            long bookedSeats
    ) {

        this.route = route;
        this.date = date;
        this.totalSeats = totalSeats;
        this.bookedSeats = bookedSeats;
    }

    public String getRoute() {
        return route;
    }

    public String getDate() {
        return date;
    }

    public long getTotalSeats() {
        return totalSeats;
    }

    public long getBookedSeats() {
        return bookedSeats;
    }

    public long getAvailableSeats() {
        return totalSeats - bookedSeats;
    }
}