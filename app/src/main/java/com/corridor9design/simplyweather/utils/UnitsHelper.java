package com.corridor9design.simplyweather.utils;

import com.corridor9design.simplyweather.preferences.AppPreferences;

public class UnitsHelper {
  public static String getTemperatureUnit(AppPreferences prefs) {
    return prefs.getBooleanPreference("imperial_units", false) ? "°F" : "°C";
  }

  public static String getSpeedUnit(AppPreferences prefs) {
    return prefs.getBooleanPreference("imperial_units", false) ? "mph" : "kph";
  }

  public static String is24HourTimeFormat(AppPreferences prefs) {
    return prefs.getBooleanPreference("use_24_hour", false)
        ? "EEEE HH:mm:ss"
        : "EEEE hh:mm a";
  }

  public static Units getUnits(AppPreferences prefs) {
    return prefs.getBooleanPreference("imperial_units", false)
        ? new Units("°F", "mph")
        : new Units("°C", "kph");
  }
}
