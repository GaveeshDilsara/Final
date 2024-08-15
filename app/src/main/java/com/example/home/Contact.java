package com.example.home;

public class Contact {
    private String name;
    private String phoneNumber;
    private int imageResourceId;

    public Contact(String name, String phoneNumber, int imageResourceId) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.imageResourceId = imageResourceId;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }
}
