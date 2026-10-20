package com.example.smartbussystem;

public class PassengerModel {

    String ticketId,
            passengerName,
            route,
            from,
            to,
            date,
            time;

    int passengers,
            total;

    public PassengerModel() {

    }

    public String getTicketId() {
        return ticketId;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public String getRoute() {
        return route;
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
}