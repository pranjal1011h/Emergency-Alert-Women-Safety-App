package com.example.safetyapp;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class FirebaseHelper {

    private final DatabaseReference databaseReference;

    public FirebaseHelper() {

        databaseReference = FirebaseDatabase
                .getInstance("https://safetyapp-52c12-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("SOSHistory");

    }

    public void saveSOS(String phone,
                        String location,
                        int battery) {

        String id = databaseReference.push().getKey();

        HashMap<String,Object> data =
                new HashMap<>();

        data.put("phone", phone);
        data.put("location", location);
        data.put("battery", battery);

        data.put(
                "time",
                new SimpleDateFormat(
                        "dd-MM-yyyy HH:mm:ss",
                        Locale.getDefault())
                        .format(new Date())
        );

        databaseReference.child(id)
                .setValue(data)
                .addOnSuccessListener(unused -> {
                    android.util.Log.d("Firebase", "SOS Saved Successfully");
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("Firebase", "Firebase Error", e);
                });
    }
}