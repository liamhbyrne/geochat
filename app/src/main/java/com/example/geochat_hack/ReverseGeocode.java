package com.example.geochat_hack;

import okhttp3.*;
import java.io.IOException;

class ReverseGeocode{

    //*
    // (51.604798, -1.242574) Lat, Long
    // Reverse Geocoding

    private double latitude;
    private double longitude;
    private String BASE_URL = "https://us1.locationiq.com/v1/reverse.php?";
    private String access_token = "pk.faeff0542624005061e9477657990282";
    private final OkHttpClient httpClient = new OkHttpClient();

    public ReverseGeocode(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void query(){
        OkHttpClient obj = new OkHttpClient();
        Request request = new Request.Builder()
                .url(this.get_url())
                //.addHeader("custom-key", "mkyong")  // add request headers
                .addHeader("User-Agent", "GeoChat Bot")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {

            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            // Get response body
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to encode the data into a URL for the API request
    private String get_url(){
        String key = "key=" + this.access_token;
        String format = "&format=json";
        String latlong = "&lat=" + this.latitude + "&lon=" + this.longitude;
        String url = this.BASE_URL + key + format + latlong;
        return url;
    }
}