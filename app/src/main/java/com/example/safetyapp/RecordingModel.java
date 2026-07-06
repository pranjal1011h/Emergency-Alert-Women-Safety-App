package com.example.safetyapp;

public class RecordingModel {

    private final String filePath;
    private final String date;

    public RecordingModel(String filePath, String date) {
        this.filePath = filePath;
        this.date = date;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getDate() {
        return date;
    }
}