package com.example.safetyapp;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class ShakeDetector implements SensorEventListener {

    private static final float SHAKE_THRESHOLD = 12.0f;

    private long lastShakeTime = 0;

    private OnShakeListener listener;

    public interface OnShakeListener {
        void onShake();
    }

    public void setOnShakeListener(OnShakeListener listener) {
        this.listener = listener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float acceleration =
                (float) Math.sqrt(x * x + y * y + z * z);

        if (acceleration > SHAKE_THRESHOLD) {

            long currentTime = System.currentTimeMillis();

            // Prevent multiple triggers instantly
            if (currentTime - lastShakeTime > 2000) {

                lastShakeTime = currentTime;

                if (listener != null) {
                    listener.onShake();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}