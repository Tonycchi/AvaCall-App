package com.example.rcvc;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.preference.DialogPreference;
import androidx.preference.PreferenceDialogFragmentCompat;

public class MotorPortDialogFragment extends PreferenceDialogFragmentCompat {

    private RadioGroup right, left;
    private RadioButton[] buttonsRight, buttonsLeft;

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

        buttonsRight = new RadioButton[4];
        buttonsLeft = new RadioButton[4];
        for (int i = 0; i < 4; i++) {
            int a = i;
            buttonsRight[i] = (RadioButton) right.getChildAt(i);
            buttonsRight[i].setOnClickListener((View v) -> {
                for (int k = 0; k < 4; k++) {
                    buttonsLeft[k].setEnabled(true);
                }
                buttonsLeft[a].setEnabled(false);
            });
            buttonsLeft[i] = (RadioButton) left.getChildAt(i);
            buttonsLeft[i].setOnClickListener((View v) -> {
                for (int k = 0; k < 4; k++) {
                    buttonsRight[k].setEnabled(true);
                }
                buttonsRight[a].setEnabled(false);
            });
        }

        if (motors != null) {
            int ri = binLogIndex(motors[0])-1;
            int li = binLogIndex(motors[1])-1;

            buttonsRight[ri].setChecked(true);
            buttonsLeft[li].setChecked(true);

            buttonsRight[li].setEnabled(false);
            buttonsLeft[ri].setEnabled(false);
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
