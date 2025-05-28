package com.corridor9design.simplyweather.preferences;

import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferenceKeys;

public class PreferenceKeys {
    public static final Preferences.Key<String> API_KEY = PreferencesKeys.stringKey("uapi_key");
    public static final Preferences.Key<Boolean> LOG_INFO = PreferencesKeys.booleanKey("loginfo");
    public static final Preferences.Key<Boolean> IMPERIAL_UNITS = PreferencesKeys.booleanKey("imperial_units");
    public static final Preferences.Key<Boolean> USE_24_HOUR = PreferencesKeys.booleanKey("use_24_hour");
}