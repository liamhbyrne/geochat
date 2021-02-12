package com.example.geochat_hack;

class ProvinceLocator{
    // (51.604798, -1.242574) Lat, Long

    private int latitude;
    private int longitude;

    public static void main(String[] args){

    }

    public String create_bbox(){
        return this.longitude + "," + this.latitude +"," + this.longitude + "," + this.latitude;
    }
}