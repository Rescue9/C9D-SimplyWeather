package com.corridor9design.simplyweather.preferences;

import android.content.Context;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.preference.PreferenceDataStore;
import androidx.datastore.rxjava2.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava2.RxDataStore;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class DataStorePreferenceDataStore extends PreferenceDataStore {

    private final RxDataStore<Preferences> dataStore;

    public DataStorePreferenceDataStore(Context context) {
        dataStore = new RxPreferenceDataStoreBuilder(context, "app_preferences").build();
    }

    @Override
    public void putString(String key, String value) {
        Preferences.Key<String> dataKey = PreferencesKeys.stringKey(key);
        dataStore.updateDataAsync(prefs -> {
            MutablePreferences mutable = prefs.toMutablePreferences();
            mutable.set(dataKey, value);
            return Single.just(mutable);
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public String getString(String key, String defValue) {
        Preferences.Key<String> dataKey = PreferencesKeys.stringKey(key);
        try {
            return dataStore.data()
                .map(prefs -> prefs.get(dataKey) != null ? prefs.get(dataKey) : defValue)
                .first(defValue)
                .blockingGet();
        } catch (Exception e) {
            return defValue;
        }
    }

    // Add similar overrides for getBoolean, putBoolean, etc.
}
