package com.example.signalbooster;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class secondary extends AppCompatActivity {
    private static final int MY_PERMISSION_REQUEST_ACCESS_WIFI_STATE = 1001;
    private static final int REQUEST_CODE_WIFI_PERMISSION = 123;
    private WifiManager wifiManager;
    private EditText ssidEditText;
    private EditText passwordEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secondary);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        // Find the Connect button
        Button connectButton = findViewById(R.id.connectWifi);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(secondary.this, Manifest.permission.ACCESS_WIFI_STATE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted, request it
                    ActivityCompat.requestPermissions(secondary.this,
                            new String[]{Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.CHANGE_WIFI_STATE,
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.CHANGE_NETWORK_STATE,
                            Manifest.permission.INTERNET},
                            REQUEST_CODE_WIFI_PERMISSION);
                } else {
                    // Permission is granted, proceed with the action
                    showPopup();
                }
            }
        });

        // Check and display the current Wi-Fi SSID
        TextView tv5 = findViewById(R.id.textView5);
        if (isWifiConnected()) {
            String currentWifiSSID = getCurrentWifiSSID();
            if (!currentWifiSSID.isEmpty()) {
                tv5.setText(currentWifiSSID);
                connectButton.setText("Change Server"); // Change the button text
            } else {
                tv5.setText("Unknown SSID");
            }
        } else {
            tv5.setText("WifiName");
        }
    }

    void showPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogBg);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.wifi_credentials_dialog, null);
        ssidEditText = dialogView.findViewById(R.id.wifiInformation);
        ssidEditText.setKeyListener(null);
        passwordEditText = dialogView.findViewById(R.id.passwordEditText);

        TextView tv5 = findViewById(R.id.textView5);
        // Set the current Wi-Fi network name if connected
        if (isWifiConnected()) {
            // Get the current Wi-Fi network name
            String currentWifiSSID = getCurrentWifiSSID();
            if (!currentWifiSSID.isEmpty()) {
                tv5.setText(currentWifiSSID);
            } else {
                tv5.setText("Unknown SSID");
            }
        }
        builder.setView(dialogView)
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        String wifiName = ssidEditText.getText().toString();
                        String wifiPass = passwordEditText.getText().toString();
                        connectToWifi(wifiName, wifiPass);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    void connectToWifi(String ssid, String password) {
        if (ssid.isEmpty()) {
            // Show a message indicating that both SSID and password are required
            Toast.makeText(this, "SSID are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (wifiManager != null) {
            // Create a WifiConfiguration for the new network
            WifiConfiguration wifiConfig = new WifiConfiguration();
            wifiConfig.SSID = "\"" + ssid + "\"";
            wifiConfig.preSharedKey = "\"" + password + "\"";
            wifiConfig.status = WifiConfiguration.Status.ENABLED;
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

            // Add the network to the list of configured networks
            int networkId = wifiManager.addNetwork(wifiConfig);
            if (networkId != -1) {
                // Attempt to enable the network
                boolean success = wifiManager.enableNetwork(networkId, true);
                if (success) {
                    // Connection successful
                    Toast.makeText(this, "Connecting to " + ssid, Toast.LENGTH_SHORT).show();
                } else {
                    // Connection failed
                    Toast.makeText(this, "Failed to connect to " + ssid, Toast.LENGTH_SHORT).show();
                }
            } else {
                // Failed to add the network
                Toast.makeText(this, "Failed to add " + ssid, Toast.LENGTH_SHORT).show();
            }
        } else {
            // WifiManager is null, handle accordingly
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_WIFI_PERMISSION) {
            // Check if all permissions are granted
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (allPermissionsGranted) {
                // All permissions granted, proceed with Wi-Fi connection
                String wifiName = ssidEditText.getText().toString();
                String wifiPass = passwordEditText.getText().toString();
                connectToWifi(wifiName, wifiPass);
            } else {
                // Permission denied, handle accordingly
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isWifiConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkCapabilities capabilities = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            }
            if (capabilities != null) {
                return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
            }
        }
        return false;
    }
    private String getCurrentWifiSSID() {
        if (wifiManager != null) {
            String ssidWithQuotes = wifiManager.getConnectionInfo().getSSID();
            return ssidWithQuotes.replaceAll("^\"|\"$", "");
        } else {
            return "";
        }
    }
}