package com.example.signalbooster;

import android.Manifest;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class secondary extends AppCompatActivity {
    private static final int MY_PERMISSION_REQUEST_ACCESS_WIFI_STATE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secondary);

        // Define your permission request code
        final int MY_PERMISSION_REQUEST_ACCESS_WIFI_STATE = 1001;

        // Find the Connect button
        Button connectButton = findViewById(R.id.connectWifi);

        // Check if the ACCESS_WIFI_STATE permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_WIFI_STATE},
                    MY_PERMISSION_REQUEST_ACCESS_WIFI_STATE);
        }
    }
}