package edu.ranken.kyoung.myweatherapp;

import com.google.gson.annotations.SerializedName;

public class MainData {
    @SerializedName("temp")
    private double temperature;

    public double getTemperature() {
        return temperature;
    }
}
