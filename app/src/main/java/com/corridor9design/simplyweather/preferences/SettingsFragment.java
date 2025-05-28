package com.corridor9design.simplyweather.preferences;

import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.corridor9design.simplyweather.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Attach custom DataStorePreferenceDataStore
        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setPreferenceDataStore(new DataStorePreferenceDataStore(requireContext()));

        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}
