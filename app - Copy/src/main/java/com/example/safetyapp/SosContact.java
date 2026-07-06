package com.example.safetyapp;

public class SosContact {

    private String name;
    private String phone;
    private boolean primary;

    public SosContact(String name,
                      String phone,
                      boolean primary) {

        this.name = name;
        this.phone = phone;
        this.primary = primary;
    }

    public SosContact(String name,
                      String phone) {

        this(name, phone, false);
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }
}