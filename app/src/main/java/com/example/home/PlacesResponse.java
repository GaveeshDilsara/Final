package com.example.home;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PlacesResponse {
    @SerializedName("results")
    public List<PlaceResult> results;
}
