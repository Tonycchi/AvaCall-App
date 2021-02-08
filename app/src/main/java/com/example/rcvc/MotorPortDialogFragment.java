package com.example.rcvc;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.preference.DialogPreference;
import androidx.preference.PreferenceDialogFragmentCompat;

public class MotorPortDialogFragment extends PreferenceDialogFragmentCompat {

    private RadioGroup right, left;

    public static MotorPortDialogFragment newInstance(
            String key) {
        final MotorPortDialogFragment
                fragment = new MotorPortDialogFragment();
        final Bundle b = new Bundle(1);
        b.putString(ARG_KEY, key);
        fragment.setArguments(b);

        return fragment;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        right = view.findViewById(R.id.right_port);
        left = view.findViewById(R.id.left_port);

        if (right == null || left == null) {
            throw new IllegalStateException();
        }

        String motors = null;
        DialogPreference pref = getPreference();
        if (pref instanceof MotorPortDialogPreference) {
            motors = ((MotorPortDialogPreference) pref).getPorts();
        }

        if (motors != null) {
            char[] m = motors.toCharArray();
            int ri = m[0] - 65, li = m[1] - 65;

            ((RadioButton) right.getChildAt(ri)).setChecked(true);
            ((RadioButton) left.getChildAt(li)).setChecked(true);
        }
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            int r = right.getCheckedRadioButtonId();
            int l = left.getCheckedRadioButtonId();
            char[] m = new char[2];

            m[0] = (char) (65 + r);
            m[1] = (char) (65 + l);

            DialogPreference pref = getPreference();
            if (pref instanceof MotorPortDialogPreference) {
                MotorPortDialogPreference mPref = (MotorPortDialogPreference) pref;
                mPref.setPorts(new String(m));
            }
        }
    }
}
