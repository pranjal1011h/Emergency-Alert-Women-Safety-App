package com.example.safetyapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SafetyApp.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "recordings";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_FILE_PATH = "file_path";
    private static final String COLUMN_DATE = "date";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTable = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_FILE_PATH + " TEXT,"
                + COLUMN_DATE + " TEXT"
                + ")";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long insertRecording(String filePath, String date) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        // Store audio file path
        values.put(COLUMN_FILE_PATH, filePath);

        // Store recording date
        values.put(COLUMN_DATE, date);

        // Insert into database
        long result = db.insert(TABLE_NAME, null, values);

        db.close();

        return result;
    }
}