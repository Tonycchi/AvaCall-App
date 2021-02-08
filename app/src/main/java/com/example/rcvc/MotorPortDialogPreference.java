package com.example.rcvc;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.preference.DialogPreference;

import java.util.Set;

public class MotorPortDialogPreference extends DialogPreference {

    private String ports;
    private int dialogLayoutResId = R.layout.preference_dialog_motors;

    public MotorPortDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.preference_dialog_motors);
    }

    public String getPorts() {
        return ports;
    }

    public void setPorts(String motors) {
        char[] m = motors.toCharArray();
        if (m[0] != m[1] && m.length ==2) {
            ports = motors;
            persistString(motors);
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    public int getDialogLayoutResource() {
        return dialogLayoutResId;
    }
}
