package com.example.geochat_hack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationActivity extends AppCompatActivity {

    public static final int DEFAULT_UPDATE_INTERVAL = 30;
    public static final int FAST_UPDATE_INTERVAL = 5;
    public static final int PERMISSIONS_FINE_LOCATION = 99; // Value is any arbitrary value

    TextView tv_lat, tv_lon, tv_altitude, tv_accuracy, tv_speed, tv_sensor, tv_address, tv_updates, tv_locality;

    Switch sw_locationupdates, sw_gps;

    // variable to remember if we are tracking location or not
    boolean updateOn = false;

    // Location request is a config file for all settings related to FuseLocationProviderClient
    LocationRequest locationRequest;

    LocationCallback locationCallBack;

    // Google's API for location services. The majority of app functions using this class
    FusedLocationProviderClient fusedLocationProviderClient;

    private Map<String, String> locationInfo = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        tv_lat = findViewById(R.id.tv_lat);
        tv_lon = findViewById(R.id.tv_lon);
        tv_altitude = findViewById(R.id.tv_altitude);
        tv_accuracy = findViewById(R.id.tv_accuracy);
        tv_speed = findViewById(R.id.tv_speed);
        tv_sensor = findViewById(R.id.tv_sensor);
        tv_address = findViewById(R.id.tv_address);
        tv_updates = findViewById(R.id.tv_updates);
        tv_locality = findViewById(R.id.tv_locality);
        sw_locationupdates = findViewById(R.id.sw_locationsupdates);
        sw_gps = findViewById(R.id.sw_gps);

        // set all properties of LocationRequest
        locationRequest = new LocationRequest();

        locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);

        locationRequest.setFastestInterval(1000 * FAST_UPDATE_INTERVAL);

        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        // Event is triggered whenever the update interval is set
        locationCallBack = new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                // Save the location
                updateUIValues(locationResult.getLastLocation());
            }
        };

        sw_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sw_gps.isChecked()) {
                    // Most accurate - use GPS
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    tv_sensor.setText("Using GPS");
                    locationInfo.put("tv_sensor", "Using GPS");
                } else {
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    tv_sensor.setText("Using Towers + WIFI");
                    locationInfo.put("tv_sensor", "Using Towers + WIFI");
                }
            }
        });

        sw_locationupdates.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (sw_locationupdates.isChecked()) {
                    // Turn on location tracking
                    startLocationUpdates();
                } else {
                    // Turn off tracking
                    stopLocationUpdates();
                }
            }
        });


        updateGPS();
    }  // end onCreate method

    private void stopLocationUpdates() {
        tv_updates.setText("Location is NOT being tracked");
        locationInfo.put("tv_updates", "Location is NOT being tracked");
        tv_lat.setText("Not tracking location");
        locationInfo.put("tv_lat","Not tracking location");
        tv_lon.setText("Not tracking location");
        locationInfo.put("tv_lon","Not tracking location");
        tv_speed.setText("Not tracking location");
        locationInfo.put("tv_speed","Not tracking location");
        tv_address.setText("Not tracking location");
        locationInfo.put("tv_address","Not tracking location");
        tv_locality.setText("Not tracking location");
        locationInfo.put("tv_locality","Not tracking location");
        tv_accuracy.setText("Not tracking location");
        locationInfo.put("tv_accuracy","Not tracking location");
        tv_altitude.setText("Not tracking location");
        locationInfo.put("tv_altitude","Not tracking location");
        tv_sensor.setText("Not tracking location");
        locationInfo.put("tv_sensor","Not tracking location");

        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
    }

    private void startLocationUpdates() {
        tv_updates.setText("Location is being tracked");
        locationInfo.put("tv_updates","Not tracking location");
        tv_lat.setText("Trying to track location");
        locationInfo.put("tv_lat","Not tracking location");
        tv_lon.setText("Trying to track location");
        locationInfo.put("tv_lon","Not tracking location");
        tv_speed.setText("Trying to track location");
        locationInfo.put("tv_speed","Not tracking location");
        tv_address.setText("Trying to track location");
        locationInfo.put("tv_address","Not tracking location");
        tv_locality.setText("Trying to track location");
        locationInfo.put("tv_locality","Not tracking location");
        tv_accuracy.setText("Trying to track location");
        locationInfo.put("tv_accuracy","Not tracking location");
        tv_altitude.setText("Trying to track location");
        locationInfo.put("tv_altitude","Not tracking location");
        tv_sensor.setText("Trying to track location");
        locationInfo.put("tv_sensor","Not tracking location");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);
            updateGPS();
            return;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS_FINE_LOCATION:
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateGPS();
            }
            else{
                Toast.makeText(this, "This app requires permission granted in order to work properly", Toast.LENGTH_SHORT).show();
                finish();
            }
            break;
        }
    }

    private void updateGPS(){
        // get permissions from the user to track GPS
        // get the current location from the fused client
        // update the UI - i.e. set all properties in their associated text view items
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(LocationActivity.this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            // user provided the permission
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // We got permissions. Put the values of location XXX into the UI components.
                    if(location != null) {
                        updateUIValues(location);
                    }else{
                        Log.i("Location", "Location is null");
                    }
                }
            });
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }
    }


    private void updateUIValues(Location location) {

        // update all of the text view objects with a new location.
        tv_lat.setText(String.valueOf(location.getLatitude()));
        locationInfo.put("tv_lat",String.valueOf(location.getLatitude()));
        tv_lon.setText(String.valueOf(location.getLongitude()));
        locationInfo.put("tv_lon",String.valueOf(location.getLongitude()));
        tv_accuracy.setText(String.valueOf(location.getAccuracy()));
        locationInfo.put("tv_accuracy",String.valueOf(location.getAccuracy()));

        if ( location.hasAltitude()) {
            tv_altitude.setText(String.valueOf(location.getAltitude()));
            locationInfo.put("tv_altitude",String.valueOf(location.getAltitude()));
        }
        else{
            tv_altitude.setText("Not available");
            locationInfo.put("tv_altitude","Not available");
        }

        if ( location.hasSpeed()) {
            tv_speed.setText(String.valueOf(location.getSpeed()));
            locationInfo.put("tv_speed",String.valueOf(location.getSpeed()));
        }
        else{
            tv_speed.setText("Not available");
            locationInfo.put("tv_speed","Not available");
        }

        Geocoder geocoder = new Geocoder(LocationActivity.this);

        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            tv_address.setText(addresses.get(0).getAddressLine(0));
            locationInfo.put("tv_address",addresses.get(0).getAddressLine(0));
            tv_locality.setText(addresses.get(0).getLocality());
            locationInfo.put("tv_locality",addresses.get(0).getLocality());
        } catch(Exception e){
            tv_address.setText("Unable to get street address");
            locationInfo.put("tv_address","Unable to get street address");
            tv_locality.setText("Unable to get street address");
            locationInfo.put("tv_locality","Unable to get street address");
        }
    }

    public Map<String, String> getLocationInfo(){
        return locationInfo;
    }

    public String getLocality() {
        String locality = "N/A";
        if(this.locationInfo.containsKey("tv_locality")){
            locality = this.locationInfo.get("tv_locality");
        }
        return locality;
    }
}