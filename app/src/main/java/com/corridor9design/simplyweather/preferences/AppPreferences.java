package com.corridor9design.simplyweather.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

public class AppPreferences {

    private final SharedPreferences sharedPreferences;

    public AppPreferences(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    // Generic getter for any preference
    public String getStringPreference(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }
    
    // Generic setter for any preference
    public void setStringPreference(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    // Example for other data types
    public boolean getBooleanPreference(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public void setBooleanPreference(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public int getIntPreference(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    public void setIntPreference(String key, int value) {
        sharedPreferences.edit().putInt(key, value).apply();
    }

    // Add more methods as needed (like for long, float, etc.)
}
