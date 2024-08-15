package com.example.home;

import com.google.gson.annotations.SerializedName;

public class PlaceResult {
    @SerializedName("geometry")
    public Geometry geometry;

    @SerializedName("name")
    public String name;
}
