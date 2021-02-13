package com.example.geochat_hack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    TextView currentLocation;
    Button locationButton, settingsButton, chatButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Assign variables
        currentLocation = (TextView) findViewById(R.id.currentLocation);
        locationButton = (Button) findViewById(R.id.locationButton);
        settingsButton = (Button) findViewById(R.id.settingsButton);
        chatButton = (Button) findViewById(R.id.chatButton);
        // Set currentLocation
        String dummyLocation = "Southampton";
        currentLocation.setText(Html.fromHtml("<font color='#197d02'><b>Current Location: </b></font>" + dummyLocation));
        // Assign button methods
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, LocationActivity.class));
            }
        });
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ChatActivity.class));
            }
        });
    }
}