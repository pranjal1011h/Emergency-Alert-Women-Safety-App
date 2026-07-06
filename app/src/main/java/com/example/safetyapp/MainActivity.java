
package com.example.safetyapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.Toast;
import com.example.safetyapp.AudioRecorderHelper;
import android.media.MediaPlayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.biometric.BiometricPrompt;
import androidx.biometric.BiometricManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;
import android.os.BatteryManager;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    private MediaPlayer mediaPlayer;
    private int fingerprintCount = 0;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private ShakeDetector shakeDetector;

    private Button btnSOS;

    private FirebaseHelper firebaseHelper;
    private AudioRecorderHelper audioRecorderHelper;
    private LiveLocationHelper liveLocationHelper;

    private LocationCallback locationCallback;

    private boolean isTracking = false;



    String emergencyNumber = "112";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSOS = findViewById(R.id.btn_sos);
        firebaseHelper = new FirebaseHelper();
        String phone = getSharedPreferences(
                "user_data",
                MODE_PRIVATE
        ).getString("phone", "Unknown");

        liveLocationHelper =
                new LiveLocationHelper(phone);

        audioRecorderHelper =
                new AudioRecorderHelper(this);

        fusedLocationClient =
                LocationServices
                        .getFusedLocationProviderClient(this);

        requestPermissions();

        sensorManager =
                (SensorManager)
                        getSystemService(SENSOR_SERVICE);

        if (sensorManager != null) {

            accelerometer =
                    sensorManager.getDefaultSensor(
                            Sensor.TYPE_ACCELEROMETER);

            shakeDetector = new ShakeDetector();

            shakeDetector.setOnShakeListener(() -> {

                Toast.makeText(
                        this,
                        "Shake Detected!",
                        Toast.LENGTH_SHORT
                ).show();

                sendSOS();
            });
        }

        btnSOS.setOnClickListener(v -> sendSOS());
        findViewById(R.id.btn_logout)
                .setOnClickListener(v -> {

                    SessionManager.setLoggedIn(
                            this,
                            false
                    );

                    startActivity(
                            new Intent(
                                    MainActivity.this,
                                    LoginActivity.class
                            )
                    );

                    finish();
                });

        findViewById(R.id.btn_call112_top)
                .setOnClickListener(v ->
                        callNumber(emergencyNumber));

        findViewById(R.id.btn_get_location)
                .setOnClickListener(v ->
                        getLocationAndSendToContacts());
        findViewById(R.id.btn_contacts)
                .setOnClickListener(v ->

                        startActivity(
                                new Intent(
                                        MainActivity.this,
                                        ContactActivity.class
                                )));
        findViewById(R.id.btn_fingerprint)
                .setOnClickListener(v -> authenticateFingerprint());

        findViewById(R.id.btn_stop_siren)
                .setOnClickListener(v -> {
                    stopLiveTracking();


                    if (mediaPlayer != null
                            && mediaPlayer.isPlaying()) {

                        mediaPlayer.stop();

                        mediaPlayer.release();

                        mediaPlayer = null;
                    }
                });
        findViewById(R.id.card_cybercrime)
                .setOnClickListener(v ->
                        callNumber("1930"));
        findViewById(R.id.card_hospital)
                .setOnClickListener(v ->
                        openNearbyHospitals());
        findViewById(R.id.card_police)
                .setOnClickListener(v ->
                        callNumber("112"));
        findViewById(R.id.card_emergency)
                .setOnClickListener(v ->
                        callNumber("112"));
        findViewById(R.id.card_women_helpline)
                .setOnClickListener(v ->
                        callNumber("1091"));
        findViewById(R.id.card_fake_call)
                .setOnClickListener(v -> {

                    Intent intent =
                            new Intent(Intent.ACTION_DIAL);

                    intent.setData(
                            Uri.parse("tel:9999999999"));

                    startActivity(intent);
                });
    }

    private void requestPermissions() {

        String[] permissions = {

                Manifest.permission.SEND_SMS,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.RECORD_AUDIO
        };

        List<String> neededPermissions =
                new ArrayList<>();

        for (String permission : permissions) {

            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
            ) != PackageManager.PERMISSION_GRANTED) {

                neededPermissions.add(permission);
            }
        }

        if (!neededPermissions.isEmpty()) {

            ActivityCompat.requestPermissions(
                    this,
                    neededPermissions.toArray(
                            new String[0]),
                    1
            );
        }
    }

    private void sendSOS() {
        startLiveTracking();

        // Start recording first
        // 1. Start Recording
        if (audioRecorderHelper != null) {

            audioRecorderHelper.startRecording();

            new Handler().postDelayed(() -> {

                audioRecorderHelper.stopRecording();

            }, 10000);
        }

        // 2. Send SMS + Location
        getLocationAndSendSOS();


        // Stop recording after 60 seconds
        new Handler().postDelayed(() -> {

            if (mediaPlayer == null) {

                mediaPlayer =
                        MediaPlayer.create(
                                MainActivity.this,
                                R.raw.siren
                        );
            }

            if (mediaPlayer != null) {

                mediaPlayer.start();

                // Stop siren after 30 seconds
                new Handler().postDelayed(() -> {

                    stopSiren();

                }, 30000);
            }

        }, 10000);
    }

    private void stopSiren() {

        if (mediaPlayer != null &&
                mediaPlayer.isPlaying()) {

            mediaPlayer.stop();

            mediaPlayer.release();

            mediaPlayer = null;
        }
    }
    private void getLocationAndSendSOS() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(
                    this,
                    "Location Permission Denied",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {


                    String message;
                    if (location != null) {

                        double lat = location.getLatitude();
                        double lng = location.getLongitude();

                        String mapsLink =
                                "https://www.google.com/maps?q="
                                        + lat + "," + lng;
                        BatteryManager batteryManager =
                                (BatteryManager) getSystemService(BATTERY_SERVICE);

                        int battery =
                                batteryManager.getIntProperty(
                                        BatteryManager.BATTERY_PROPERTY_CAPACITY);

                        // Save SOS in Firebase
                        String phone = getSharedPreferences(
                                "user_data",
                                MODE_PRIVATE
                        ).getString("phone", "Unknown");

                        firebaseHelper.saveSOS(
                                phone,
                                mapsLink,
                                battery
                        );
                       message =
                                "🚨 EMERGENCY SOS ALERT 🚨\n\n"
                                        + "I am in danger.\n"
                                        + "Please track me LIVE:\n\n"
                                        + "LIVE TRACKING LINK:\n"
                                        + mapsLink
                                        + "\n\n⚠️ This shows my real-time position in Google Maps\n"
                                        + "- Sent from Sakhi Rakshak Safety App";

                    } else {

                        message =
                                "EMERGENCY SOS ALERT\n\n"
                                        + "I am in danger.\n"
                                        + "Please help me immediately.\n\n"
                                        + "Location unavailable.\n\n"
                                        + "- Sent from Sakhi Rakshak";
                    }

                        sendSMS(message);
                        callPrimaryContact();
                    
                });
    }

    private void sendSMS(String message) {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
        ) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        List<SosContact> contacts =
                ContactManager.loadContacts(this);

        SosContact primary =
                ContactManager.getPrimaryContact(this);

        SmsManager smsManager =
                SmsManager.getDefault();

        // Send to primary contact first
        if (primary != null) {

            ArrayList<String> parts =
                    smsManager.divideMessage(message);

            smsManager.sendMultipartTextMessage(
                    primary.getPhone(),
                    null,
                    parts,
                    null,
                    null
            );
        }

        // Send to remaining contacts
        for (SosContact contact : contacts) {

            if (contact.isPrimary()) {
                continue;
            }

            ArrayList<String> parts =
                    smsManager.divideMessage(message);

            smsManager.sendMultipartTextMessage(
                    contact.getPhone(),
                    null,
                    parts,
                    null,
                    null
            );
        }

        Toast.makeText(
                this,
                "SOS Sent",
                Toast.LENGTH_SHORT
        ).show();
        callPrimaryContact();

    }

    private void callPrimaryContact() {

        SosContact primary =
                ContactManager.getPrimaryContact(this);

        if (primary != null) {

            callNumber(primary.getPhone());
        }
    }

    private void callNumber(String number) {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
        ) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        Intent intent =
                new Intent(Intent.ACTION_CALL);

        intent.setData(Uri.parse("tel:" + number));

        startActivity(intent);
    }

    private void authenticateFingerprint() {

        BiometricManager biometricManager =
                BiometricManager.from(this);

        if (biometricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG)
                != BiometricManager.BIOMETRIC_SUCCESS) {

            Toast.makeText(
                    this,
                    "Fingerprint not available",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        BiometricPrompt biometricPrompt =
                new BiometricPrompt(
                        this,
                        ContextCompat.getMainExecutor(this),
                        new BiometricPrompt.AuthenticationCallback() {

                            @Override
                            public void onAuthenticationSucceeded(
                                    BiometricPrompt.AuthenticationResult result) {

                                super.onAuthenticationSucceeded(result);

                                Toast.makeText(
                                        MainActivity.this,
                                        "Fingerprint Verified",
                                        Toast.LENGTH_SHORT
                                ).show();

                                sendSOS();
                            }
                        });

        BiometricPrompt.PromptInfo promptInfo =
                new BiometricPrompt.PromptInfo.Builder()
                        .setTitle("SOS Authentication")
                        .setSubtitle("Place your finger on sensor")
                        .setNegativeButtonText("Cancel")
                        .build();

        biometricPrompt.authenticate(promptInfo);
    }
    private void getLocation() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(
                    this,
                    "Location Permission Denied",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {

                    if (location != null) {

                        String mapsLink =
                                "https://maps.google.com/?q="
                                        + location.getLatitude()
                                        + ","
                                        + location.getLongitude();

                        Toast.makeText(
                                this,
                                mapsLink,
                                Toast.LENGTH_LONG
                        ).show();

                    } else {

                        Toast.makeText(
                                this,
                                "Unable to get location. Turn ON GPS.",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
    private void getLocationAndSendToContacts() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(
                    this,
                    "Location Permission Denied",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {

                    if (location != null) {

                        String mapsLink =
                                "https://maps.google.com/?q="
                                        + location.getLatitude()
                                        + ","
                                        + location.getLongitude();

                        List<SosContact> contacts =
                                ContactManager.loadContacts(this);

                        SmsManager smsManager =
                                SmsManager.getDefault();

                        String message =
                                "My Current Location:\n\n"
                                        + mapsLink
                                        + "\n\n- Sent from Sakhi Rakshak";

                        for (SosContact contact : contacts) {

                            ArrayList<String> parts =
                                    smsManager.divideMessage(message);

                            smsManager.sendMultipartTextMessage(
                                    contact.getPhone(),
                                    null,
                                    parts,
                                    null,
                                    null
                            );
                        }

                        Toast.makeText(
                                this,
                                "Location Sent To Contacts",
                                Toast.LENGTH_LONG
                        ).show();

                    } else {

                        Toast.makeText(
                                this,
                                "Unable to get location",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }



    @Override
    protected void onResume() {
        super.onResume();

        if (sensorManager != null
                && accelerometer != null
                && shakeDetector != null) {

            sensorManager.registerListener(
                    shakeDetector,
                    accelerometer,
                    SensorManager.SENSOR_DELAY_UI
            );
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (sensorManager != null
                && shakeDetector != null) {

            sensorManager.unregisterListener(
                    shakeDetector);
        }
    }
    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (mediaPlayer != null) {

            mediaPlayer.stop();

            mediaPlayer.release();

            mediaPlayer = null;
        }
    }
    private void openNearbyHospitals() {

        Uri uri = Uri.parse("geo:0,0?q=Hospitals");

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);

        intent.setPackage("com.google.android.apps.maps");

        if (intent.resolveActivity(getPackageManager()) != null) {

            startActivity(intent);

        } else {

            Toast.makeText(
                    this,
                    "Google Maps not installed",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void startLiveTracking() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        if (isTracking)
            return;

        isTracking = true;

        LocationRequest locationRequest =
                LocationRequest.create();

        locationRequest.setInterval(5000);

        locationRequest.setFastestInterval(3000);

        locationRequest.setPriority(
                LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback =
                new LocationCallback() {

                    @Override
                    public void onLocationResult(
                            LocationResult locationResult) {

                        if (locationResult == null)
                            return;

                        double lat =
                                locationResult
                                        .getLastLocation()
                                        .getLatitude();

                        double lng =
                                locationResult
                                        .getLastLocation()
                                        .getLongitude();

                        liveLocationHelper.updateLocation(
                                lat,
                                lng
                        );
                    }
                };

        fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                getMainLooper()
        );
    }
    private void stopLiveTracking() {

        if (locationCallback != null) {

            fusedLocationClient.removeLocationUpdates(
                    locationCallback
            );

            isTracking = false;
        }
    }
}
