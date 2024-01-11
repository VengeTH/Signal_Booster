package com.example.signalbooster;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class secondary extends AppCompatActivity {
    private static final int MY_PERMISSION_REQUEST_ACCESS_WIFI_STATE = 1001;
    private static final int REQUEST_CODE_WIFI_PERMISSION = 123;
    private WifiManager wifiManager;
    private EditText ssidEditText, passwordEditText;

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
                            new String[]{Manifest.permission.ACCESS_WIFI_STATE},
                            REQUEST_CODE_WIFI_PERMISSION);
                } else {
                    // Permission is granted, proceed with the action
                    showPopup();
                }
            }

        });
    }
        /*
        // Define your permission request code
        final int MY_PERMISSION_REQUEST_ACCESS_WIFI_STATE = 1001;

        // Check if the ACCESS_WIFI_STATE permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_WIFI_STATE},
                    MY_PERMISSION_REQUEST_ACCESS_WIFI_STATE);*/
    void showPopup(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogBg);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.wifi_credentials_dialog, null);
        final EditText ssidEditText = dialogView.findViewById(R.id.editTextSSID);
        final EditText passwordEditText = dialogView.findViewById(R.id.passwordEditText);

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
        Log.d("WiFiConnection", "Connecting to Wi-Fi");
        // Replace "networkSSID" and "networkPassword" with your network details
        String networkSSID = ssid;
        String networkPassword = password;

        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = networkSSID;
        wifiConfig.preSharedKey = networkPassword;

        // If the network is open (no password), set the security type to NONE
        if (networkPassword.isEmpty()) {
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        } else {
            // If the network has a password, set the password and security type accordingly
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            wifiConfig.preSharedKey = "\"" + networkPassword + "\"";
        }

        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.isWifiEnabled();
        wifiManager.disconnect();
        boolean enableNetworkResult = wifiManager.enableNetwork(netId, true);
        boolean reconnectResult = wifiManager.reconnect();
        if (enableNetworkResult && reconnectResult) {
            Toast.makeText(this, "Connecting to Wi-Fi...", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to connect to Wi-Fi", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_WIFI_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with the action
                showPopup();
            } else {
                // Permission denied, handle the denial gracefully
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private boolean isWifiConnected() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            return wifiManager.isWifiEnabled() && wifiManager.getConnectionInfo().getNetworkId() != -1;
        }
        return false;
    }
    private String getCurrentWifiSSID() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            return wifiManager.getConnectionInfo().getSSID().replace("\"", "");
        } else {
            return "";
        }
    }
}