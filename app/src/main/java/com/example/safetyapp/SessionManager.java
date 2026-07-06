package com.example.safetyapp;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "user_session";

    private static final String KEY_LOGIN =
            "is_logged_in";

    public static void setLoggedIn(
            Context context,
            boolean value
    ) {

        SharedPreferences prefs =
                context.getSharedPreferences(
                        PREF_NAME,
                        Context.MODE_PRIVATE);

        prefs.edit()
                .putBoolean(KEY_LOGIN, value)
                .apply();
    }

    public static boolean isLoggedIn(
            Context context
    ) {

        SharedPreferences prefs =
                context.getSharedPreferences(
                        PREF_NAME,
                        Context.MODE_PRIVATE);

        return prefs.getBoolean(
                KEY_LOGIN,
                false
        );
    }
}