package com.example.safetyapp;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class LiveLocationHelper {

    private final DatabaseReference locationRef;

    public LiveLocationHelper(String phone) {

        locationRef = FirebaseDatabase
                .getInstance("https://safetyapp-52c12-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("LiveLocation")
                .child(phone);
    }

    public void updateLocation(double latitude,
                               double longitude) {

        HashMap<String, Object> map = new HashMap<>();

        map.put("latitude", latitude);
        map.put("longitude", longitude);
        map.put("timestamp", System.currentTimeMillis());

        locationRef.setValue(map);
    }
}