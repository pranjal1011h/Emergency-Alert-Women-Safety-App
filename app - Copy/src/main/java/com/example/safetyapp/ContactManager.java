package com.example.safetyapp;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ContactManager {

    private static final String PREF_NAME = "sos_contacts";
    private static final String KEY_CONTACTS = "contacts_json";

    // Save full list
    public static void saveContacts(Context ctx, List<SosContact> contacts) {

        JSONArray array = new JSONArray();

        for (SosContact c : contacts) {

            try {
                JSONObject obj = new JSONObject();

                obj.put("name", c.getName());
                obj.put("phone", c.getPhone());
                obj.put("primary", c.isPrimary());

                array.put(obj);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        getPrefs(ctx)
                .edit()
                .putString(KEY_CONTACTS, array.toString())
                .apply();
    }

    // Load full list
    public static List<SosContact> loadContacts(Context ctx) {

        List<SosContact> list = new ArrayList<>();

        String json =
                getPrefs(ctx)
                        .getString(KEY_CONTACTS, null);

        // First app launch
        if (json == null) {

            list.add(new SosContact(
                    "Mother",
                    "9975181645",
                    true));

            list.add(new SosContact(
                    "Pranjal",
                    "9146245098"));

            list.add(new SosContact(
                    "Sakshi",
                    "8975699948"));

            list.add(new SosContact(
                    "Shivani",
                    "8830208711"));

            saveContacts(ctx, list);

            return list;
        }

        try {

            JSONArray array =
                    new JSONArray(json);

            for (int i = 0; i < array.length(); i++) {

                JSONObject obj =
                        array.getJSONObject(i);

                list.add(
                        new SosContact(
                                obj.getString("name"),
                                obj.getString("phone"),
                                obj.optBoolean(
                                        "primary",
                                        false)
                        )
                );
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Add a single contact
    public static void addContact(
            Context ctx,
            SosContact contact) {

        List<SosContact> list =
                loadContacts(ctx);

        list.add(contact);

        saveContacts(ctx, list);
    }

    // Remove by index
    public static void removeContact(
            Context ctx,
            int index) {

        List<SosContact> list =
                loadContacts(ctx);

        if (index >= 0 &&
                index < list.size()) {

            list.remove(index);

            saveContacts(ctx, list);
        }
    }

    // Set primary contact
    public static void setPrimaryContact(
            Context ctx,
            int index) {

        List<SosContact> contacts =
                loadContacts(ctx);

        for (int i = 0;
             i < contacts.size();
             i++) {

            contacts.get(i)
                    .setPrimary(i == index);
        }

        saveContacts(ctx, contacts);
    }
    public static SosContact
    getPrimaryContact(Context ctx) {

        List<SosContact> contacts =
                loadContacts(ctx);

        for (SosContact c : contacts) {

            if (c.isPrimary()) {

                return c;
            }
        }

        return null;
    }

    private static SharedPreferences getPrefs(
            Context ctx) {

        return ctx.getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE);
    }
}