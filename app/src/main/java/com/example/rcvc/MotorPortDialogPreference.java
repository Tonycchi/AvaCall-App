package com.example.rcvc;

import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.DialogPreference;

public class MotorPortDialogPreference extends DialogPreference {
    public MotorPortDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        int dialogLayoutResId = R.layout.preference_dialog_motors;
        setDialogLayoutResource(dialogLayoutResId);
    }
}
