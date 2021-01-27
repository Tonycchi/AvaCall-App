package com.example.rcvc;

import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        final ListPreference rightList = (ListPreference) findPreference("right_port");
        final ListPreference leftList = (ListPreference) findPreference("left_port");

        rightList.setOnPreferenceClickListener(preference -> {
            setLists(rightList, leftList);
            return false;
        });
        leftList.setOnPreferenceClickListener(preference -> {
            setLists(leftList, rightList);
            return false;
        });
    }

    private static void setLists(ListPreference set, ListPreference other) {
        CharSequence[] defEntries = {"A", "B", "C", "D"};
        CharSequence[] defEntryValues = {"1", "2", "4", "8"};
        CharSequence[] entries = new CharSequence[3];
        CharSequence[] entryValues = new CharSequence[3];
        CharSequence otherEntry = other.getEntry();

        int k = 0;
        for (int i = 0; i < 4; i++) {
            if (!defEntries[i].equals(otherEntry)) {
                entries[k] = defEntries[i];
                entryValues[k] = defEntryValues[i];
                k++;
            }
        }

        set.setEntries(entries);
        set.setEntryValues(entryValues);
    }
}
