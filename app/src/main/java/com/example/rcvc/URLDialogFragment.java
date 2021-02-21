package com.example.rcvc;

import android.os.Bundle;
import android.view.View;

import androidx.preference.PreferenceDialogFragmentCompat;

public class URLDialogFragment extends PreferenceDialogFragmentCompat {

    public static URLDialogFragment newInstance(
            String key) {
        final URLDialogFragment
                fragment = new URLDialogFragment();
        final Bundle b = new Bundle(1);
        b.putString(ARG_KEY, key);
        fragment.setArguments(b);

        return fragment;
    }

    @Override
    public void onBindDialogView(View view) {

    }

    @Override
    public void onDialogClosed(boolean positiveResult) {

    }
}
