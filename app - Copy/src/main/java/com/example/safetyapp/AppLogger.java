package com.example.safetyapp;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AppLogger {

    private static final String TAG = "SafetyApp";
    private static final String LOG_FILE = "safetyapp_log.txt";
    private static BufferedWriter fileWriter;

    public static void init(Context context) {
        try {
            File logDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            if (logDir != null && !logDir.exists()) logDir.mkdirs();
            File logFile = new File(logDir, LOG_FILE);
            fileWriter = new BufferedWriter(new FileWriter(logFile, true));
        } catch (IOException e) {
            Log.e(TAG, "[LOGGER] Failed to open log file", e);
        }
    }

    public static void sos(String phase, String detail) {
        write("E", "SOS", phase + " | " + detail);
    }

    public static void call(String number, String label, String result) {
        write("I", "CALL", "number=" + number + " label=" + label + " result=" + result);
    }

    public static void permission(String perm, String outcome) {
        write("W", "PERM", "perm=" + perm + " outcome=" + outcome);
    }

    public static void location(String status) {
        write("I", "LOC", status);
    }

    public static void nav(String screen) {
        write("D", "NAV", "screen=" + screen);
    }

    public static void error(String tag, Throwable t) {
        write("E", "ERR", tag + " | " + (t.getMessage() != null ? t.getMessage() : "unknown error"));
        Log.e(TAG, "[ERR] " + tag, t);
    }

    private static void write(String level, String category, String message) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)
                .format(new Date());
        String line = timestamp + "  " + level + "/[" + category + "] " + message;

        switch (level) {
            case "E": Log.e(TAG, line); break;
            case "W": Log.w(TAG, line); break;
            case "I": Log.i(TAG, line); break;
            default:  Log.d(TAG, line); break;
        }

        if (fileWriter != null) {
            try {
                fileWriter.write(line);
                fileWriter.newLine();
                fileWriter.flush();
            } catch (IOException e) {
                Log.e(TAG, "[LOGGER] File write failed", e);
            }
        }
    }

    public static void close() {
        try {
            if (fileWriter != null) {
                fileWriter.flush();
                fileWriter.close();
                fileWriter = null;
            }
        } catch (IOException ignored) {}
    }
}