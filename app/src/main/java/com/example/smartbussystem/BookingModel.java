package com.example.smartbussystem;

public class BookingModel {

    String ticketId;
    String route;
    String busNumber;
    String from;
    String to;
    String date;
    String time;
    String uid;
    String passengerName;
    String status;

    int passengers;
    int total;

    public BookingModel() {
    }

    public String getTicketId() {
        return ticketId;
    }

    public String getRoute() {
        return route;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public int getPassengers() {
        return passengers;
    }

    public int getTotal() {
        return total;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public String getStatus() {
        return status;
    }
}