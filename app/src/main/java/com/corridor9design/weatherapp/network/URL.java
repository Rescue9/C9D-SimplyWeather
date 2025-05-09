package com.corridor9design.weatherapp.network;

import android.content.Context;
import com.corridor9design.weatherapp.location.LocationCord;

public class URL {
    private String link;
    private String units;
    
    public URL(Context context, String apikey, Boolean imperial) {
        if(imperial){
            units = "imperial";
        } else {
            units = "metric";
        }
        
        link = "https://api.openweathermap.org/data/3.0/onecall?exclude=minutely&lat="
                + LocationCord.lat + "&lon=" + LocationCord.lon + "&units=" + units + "&appid=" + apikey;
    }
    
    public URL(Context context, String apikey) {
        link = "https://api.openweathermap.org/data/3.0/onecall?exclude=minutely&lat="
                + LocationCord.lat + "&lon=" + LocationCord.lon + "&units=" + units + "&appid=" + apikey;
    }
    
    public String getLink() {
        return link;
    }
}