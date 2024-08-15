package com.example.home;

import com.google.gson.annotations.SerializedName;

public class Location {
    @SerializedName("lat")
    public double lat;

    @SerializedName("lng")
    public double lng;
}
