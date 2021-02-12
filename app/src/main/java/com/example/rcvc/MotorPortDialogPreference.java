package com.example.rcvc;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;

import androidx.preference.DialogPreference;
import androidx.preference.PreferenceManager;

import java.util.Set;

public class MotorPortDialogPreference extends DialogPreference {
    public MotorPortDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        int dialogLayoutResId = R.layout.preference_dialog_motors;
        setDialogLayoutResource(dialogLayoutResId);
    }
}
