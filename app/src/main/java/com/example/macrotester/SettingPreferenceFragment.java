package com.example.macrotester;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

public class SettingPreferenceFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferencescreen_main, rootKey);
    }
}
