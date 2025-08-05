package com.example.macrotester;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

public class SettingsPreferenceSubScheduleFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences_sub_schedule, rootKey);
    }
}