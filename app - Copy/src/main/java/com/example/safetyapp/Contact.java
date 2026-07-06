package com.example.safetyapp;

public class Contact {
    private String name;
    private String phone;
    private String relationship;

    // Constructor
    public Contact(String name, String phone, String relationship) {
        this.name = name;
        this.phone = phone;
        this.relationship = relationship;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getRelationship() {
        return relationship;
    }
}