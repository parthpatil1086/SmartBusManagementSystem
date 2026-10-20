package com.example.smartbussystem;

public class RegisteredPassengerModel {

    String name;
    String email;
    String phone;

    public RegisteredPassengerModel() {
    }

    public RegisteredPassengerModel(
            String name,
            String email,
            String phone
    ) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }
}