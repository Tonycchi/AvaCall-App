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

        int[] motors = null;
        DialogPreference pref = getPreference();
        if (pref instanceof MotorPortDialogPreference) {
            motors = ((MotorPortDialogPreference) pref).getPorts();
        }

        if (motors != null) {
            int ri = binLogIndex(motors[0])-1;
            int li = binLogIndex(motors[1])-1;

            ((RadioButton) right.getChildAt(ri)).setChecked(true);
            ((RadioButton) left.getChildAt(li)).setChecked(true);
        }
    }

    private int binLogIndex(int x) {
        int y = 0, i = x;
        while (i > 0) {
            i = i >> 1;
            y++;
        }
        return y;
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            int rid = right.getCheckedRadioButtonId();
            View rb = right.findViewById(rid);
            int r = right.indexOfChild(rb);

            int lid = left.getCheckedRadioButtonId();
            View lb = left.findViewById(lid);
            int l = left.indexOfChild(lb);

            DialogPreference pref = getPreference();
            if (pref instanceof MotorPortDialogPreference) {
                MotorPortDialogPreference mPref = (MotorPortDialogPreference) pref;
                mPref.setPorts((1 << r), (1 << l));
            }
        }
    }
}
