package com.example.geochat_hack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final int DEFAULT_UPDATE_INTERVAL = 30;
    public static final int FAST_UPDATE_INTERVAL = 5;
    public static final int PERMISSIONS_FINE_LOCATION = 99; // Value is any arbitrary value

    private TextView tv_locality;
    private String locality;
    private double latitude, longitude;

    private GoogleMap map;

    private List<Address> addresses;

    private Switch sw_locationupdates, sw_gps;

    // variable to remember if we are tracking location or not
    private boolean updateOn = false;

    // Location request is a config file for all settings related to FuseLocationProviderClient
    private LocationRequest locationRequest;

    private LocationCallback locationCallBack;

    // Google's API for location services. The majority of app functions using this class
    private FusedLocationProviderClient fusedLocationProviderClient;

    private Map<String, String> locationInfo = new HashMap<>();

    private Button locationButton, settingsButton, chatButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        tv_locality = findViewById(R.id.chatButton);
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
                    Log.i("MAL","Using GPS");
                } else {
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    Log.i("MAL","Using Towers + WIFI");
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

        locationButton = (Button) findViewById(R.id.locationButton);
        settingsButton = (Button) findViewById(R.id.settingsButton);
        chatButton = (Button) findViewById(R.id.chatButton);

        // Assign button methods
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu menu = new PopupMenu(getApplicationContext(), v);
                menu.getMenu().add(Menu.NONE, 1, 1, getAddresses().get(0).getPremises());
                menu.getMenu().add(Menu.NONE, 2, 2, getAddresses().get(0).getLocality());
                menu.getMenu().add(Menu.NONE, 3, 3, getAddresses().get(0).getPostalCode().split(" ")[0]);
                menu.getMenu().add(Menu.NONE, 4, 4, getAddresses().get(0).getSubAdminArea());
                menu.getMenu().add(Menu.NONE, 5, 5, getAddresses().get(0).getAdminArea());


                menu.show();

                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        int i = item.getItemId();
                        switch (i) {
                            case 1:
                                locality = getAddresses().get(0).getPremises();
                                updateGPS();
                                return true;

                            case 2:
                                locality = getAddresses().get(0).getLocality();
                                updateGPS();
                                return true;

                            case 3:
                                locality = getAddresses().get(0).getPostalCode().split(" ")[0];
                                updateGPS();
                                return true;

                            case 4:
                                locality = getAddresses().get(0).getSubAdminArea();
                                updateGPS();
                                return true;

                            case 5:
                                locality = getAddresses().get(0).getAdminArea();
                                updateGPS();
                                return true;
                        }
                        return false;
                    }

                });
            }
        });
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });
        final Intent toChat = new Intent(MainActivity.this, ChatActivity.class);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                toChat.putExtra("locality", getLocality());
                startActivity(toChat);
            }
        });

    }  // end onCreate method

    private void stopLocationUpdates() {
        Log.i("MAL","Stopped tracking location");
        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
    }

    private void startLocationUpdates() {
        Log.i("MAL","Trying to track location");

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
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            // user provided the permission
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // We got permissions. Put the values of location XXX into the UI components.
                    if(location != null) {
                        updateUIValues(location);
                    }else{
                        Log.i("MAL","null location given");
                    }
                }
            });
        }
        else {
            Log.i("MAL","Permissions not accepted");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }
    }


    private void updateUIValues(Location location) {

        // update all of the text view objects with a new location.
        Geocoder geocoder = new Geocoder(MainActivity.this);

        try {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            this.addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (locality == null) {
                locality = addresses.get(0).getLocality();
            }
            //tv_locality.setText(addresses.get(0).getLocality());
            Log.i("MAL","Locality="+addresses.get(0).getLocality());
            LatLng currentLocation = new LatLng(latitude, longitude);
            map.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 13f));
            TextView locationText = findViewById(R.id.currentLocation);
            locationText.setText("Current Chat: " + getLocality());
        } catch(Exception e){
            Log.i("MAL","Unable to get location");
        }
    }

    public String getLocality(){
        return this.locality;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
    }
}