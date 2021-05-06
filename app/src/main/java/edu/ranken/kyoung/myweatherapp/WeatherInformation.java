package edu.ranken.kyoung.myweatherapp;

import com.google.gson.annotations.SerializedName;

public class WeatherInformation {
    @SerializedName("main")
    private MainData mainData;

    // Add a getter

    public MainData getMainData() {
        return mainData;
    }
}
