package com.example.safetyapp;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;


public class AudioRecorderHelper {

    private MediaRecorder recorder;

    private final Context context;

    private String filePath;

    public AudioRecorderHelper(Context context) {
        this.context = context;
    }

    public String startRecording() {

        try {

            File folder = new File(
                    context.getExternalFilesDir(
                            Environment.DIRECTORY_MUSIC
                    ),
                    "EmergencyRecordings"
            );

            if (!folder.exists()) {

                boolean created = folder.mkdirs();

                Log.d(
                        "Recorder",
                        "Folder Created: " + created
                );
            }

            filePath = folder.getAbsolutePath()
                    + "/REC_"
                    + System.currentTimeMillis()
                    + ".mp4";

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

                recorder = new MediaRecorder(context);

            } else {

                recorder = new MediaRecorder();
            }

            recorder.setAudioSource(
                    MediaRecorder.AudioSource.MIC
            );

            recorder.setOutputFormat(
                    MediaRecorder.OutputFormat.MPEG_4
            );

            recorder.setAudioEncoder(
                    MediaRecorder.AudioEncoder.AAC
            );

            recorder.setOutputFile(filePath);

            recorder.prepare();

            Log.d(
                    "Recorder",
                    "Prepare Success"
            );

            recorder.start();

            Log.d(
                    "Recorder",
                    "Start Success"
            );

            Log.d(
                    "Recorder",
                    "Recording Started"
            );

            Toast.makeText(
                    context,
                    "Recording Started",
                    Toast.LENGTH_SHORT
            ).show();

        } catch (Exception e) {

            Log.e(
                    "Recorder",
                    "Recording Failed: " + e.getMessage()
            );

            Toast.makeText(
                    context,
                    "Recording Failed",
                    Toast.LENGTH_LONG
            ).show();
        }

        return filePath;
    }

    public void stopRecording() {

        try {

            if (recorder != null) {

                recorder.stop();

                recorder.release();

                recorder = null;

                Log.d(
                        "Recorder",
                        "Recording Saved"
                );

                File file = new File(filePath);

                Toast.makeText(
                        context,
                        "Saved\nSize: " + file.length() + " bytes",
                        Toast.LENGTH_LONG
                ).show();
            }

        } catch (Exception e) {

            Log.e(
                    "Recorder",
                    "Stop Failed: " + e.getMessage()
            );
        }
    }
}