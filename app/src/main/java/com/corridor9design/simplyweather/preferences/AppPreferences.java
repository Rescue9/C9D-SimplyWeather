package com.corridor9design.simplyweather.preferences;

import android.content.Context;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.rxjava2.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava2.RxDataStore;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class AppPreferences {

    private final RxDataStore<Preferences> dataStore;

    public AppPreferences(Context context) {
        dataStore = new RxPreferenceDataStoreBuilder(context, "app_preferences").build();
    }

    // Get String
    public Single<String> getStringPreference(String key, String defaultValue) {
        Preferences.Key<String> dataKey = PreferencesKeys.stringKey(key);
        return dataStore.data()
                .map(prefs -> prefs.get(dataKey) != null ? prefs.get(dataKey) : defaultValue)
                .first(defaultValue);
    }

    // Set String
    public void setStringPreference(String key, String value) {
        Preferences.Key<String> dataKey = PreferencesKeys.stringKey(key);
        dataStore.updateDataAsync(prefsIn -> {
            MutablePreferences mutable = prefsIn.toMutablePreferences();
            mutable.set(dataKey, value);
            return Single.just(mutable);
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    // Get Boolean
    public Single<Boolean> getBooleanPreference(String key, boolean defaultValue) {
        Preferences.Key<Boolean> dataKey = PreferencesKeys.booleanKey(key);
        return dataStore.data()
                .map(prefs -> prefs.get(dataKey) != null ? prefs.get(dataKey) : defaultValue)
                .first(defaultValue);
    }

    // Set Boolean
    public void setBooleanPreference(String key, boolean value) {
        Preferences.Key<Boolean> dataKey = PreferencesKeys.booleanKey(key);
        dataStore.updateDataAsync(prefsIn -> {
            MutablePreferences mutable = prefsIn.toMutablePreferences();
            mutable.set(dataKey, value);
            return Single.just(mutable);
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    // Get Int
    public Single<Integer> getIntPreference(String key, int defaultValue) {
        Preferences.Key<Integer> dataKey = PreferencesKeys.intKey(key);
        return dataStore.data()
                .map(prefs -> prefs.get(dataKey) != null ? prefs.get(dataKey) : defaultValue)
                .first(defaultValue);
    }

    // Set Int
    public void setIntPreference(String key, int value) {
        Preferences.Key<Integer> dataKey = PreferencesKeys.intKey(key);
        dataStore.updateDataAsync(prefsIn -> {
            MutablePreferences mutable = prefsIn.toMutablePreferences();
            mutable.set(dataKey, value);
            return Single.just(mutable);
        }).subscribeOn(Schedulers.io()).subscribe();
    }
}
