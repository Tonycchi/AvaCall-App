package com.example.rcvc;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;

import androidx.preference.DialogPreference;

import java.util.Set;

public class MotorPortDialogPreference extends DialogPreference {

    private final int DEFAULT_PORTS = 128;
    private String ports;
    private int dialogLayoutResId = R.layout.preference_dialog_motors;

    public MotorPortDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.preference_dialog_motors);
        setPersistent(true);
    }

    public int[] getPorts() {
        ports = getPersistedString("129");
        return DirectCommander.stringToPorts(ports);
    }

    public void setPorts(int r, int l) {
        int y = 0;
        y = y + r + (l << 4);
        ports = String.valueOf(y);

        persistString(String.valueOf(y));
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

//    @Override
//    protected void onSetInitialValue(Object defaultValue) {
//        if (defaultValue == null) ports  = DEFAULT_PORTS;
//        try {
//            ports = Integer.parseInt(getPersistedString(defaultValue.toString()));
//        } catch (Exception e) {
//            ports = DEFAULT_PORTS;
//        }
//    }

    @Override
    public int getDialogLayoutResource() {
        return dialogLayoutResId;
    }
}
