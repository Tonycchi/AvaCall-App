package com.example.rcvc;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.preference.PreferenceDialogFragmentCompat;

public class MotorPortDialogFragment extends PreferenceDialogFragmentCompat {

    private SharedPreferences P;

    private RadioGroup groupRight, groupLeft;
    private RadioButton[] buttonsRight, buttonsLeft;

    /*
    private LinearLayout testRight, testLeft;
    private CheckBox[] cr, cl;
    private boolean rchecked, lchecked;
     */

    //private Button test;

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

        P = getContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);

        groupRight = view.findViewById(R.id.radio_group_right_port);
        groupLeft = view.findViewById(R.id.radio_group_left_port);

        /*
        test = view.findViewById(R.id.clear_test);
        test.setOnClickListener((View v) -> {
            groupRight.clearCheck();
            groupLeft.clearCheck();
            enableAll(buttonsRight);
            enableAll(buttonsLeft);
        });
         */

        if (groupRight == null || groupLeft == null) {
            throw new IllegalStateException();
        }

        buttonsRight = new RadioButton[4];
        buttonsLeft = new RadioButton[4];
        for (int i = 0; i < 4; i++) {
            int self = i;
            buttonsRight[i] = (RadioButton) groupRight.getChildAt(i);
            buttonsRight[i].setOnClickListener((View v) -> {
                enableAll(buttonsLeft);
                buttonsRight[self].setChecked(true);
                buttonsLeft[self].setEnabled(false);
            });
            buttonsLeft[i] = (RadioButton) groupLeft.getChildAt(i);
            buttonsLeft[i].setOnClickListener((View v) -> {
                enableAll(buttonsRight);
                buttonsLeft[self].setChecked(true);
                buttonsRight[self].setEnabled(false);
            });
        }

        int ri = log2(P.getInt("motor_right", 1));
        int li = log2(P.getInt("motor_left", 8));

        buttonsRight[ri].setChecked(true);
        buttonsLeft[li].setChecked(true);

        buttonsRight[li].setEnabled(false);
        buttonsLeft[ri].setEnabled(false);

        /*
        testRight = view.findViewById(R.id.test_right);
        testLeft = view.findViewById(R.id.test_left);

        cr = new CheckBox[4];
        cl = new CheckBox[4];

        for (int i = 0; i < testRight.getChildCount(); i++) {
            cr[i] = (CheckBox) testRight.getChildAt(i);
            cl[i] = (CheckBox) testLeft.getChildAt(i);
            int self = i;

            cr[i].setOnClickListener((View v) -> {
                if (((CheckBox) v).isChecked()) {
                    ((CheckBox) v).setChecked(false);
                    rchecked = false;
                    cl[self].setEnabled(true);
                } else {
                    if (!rchecked) {
                        rchecked = true;
                        ((CheckBox) v).setChecked(true);
                        cl[self].setEnabled(false);
                    }
                }
            });
            cl[i].setOnClickListener((View v) -> {
                if (((CheckBox) v).isChecked()) {
                    ((CheckBox) v).setChecked(false);
                    lchecked = false;
                    cr[self].setEnabled(true);
                } else {
                    if (!lchecked) {
                        lchecked = true;
                        ((CheckBox) v).setChecked(true);
                        cr[self].setEnabled(false);
                    }
                }
            });
        }
        */
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            SharedPreferences.Editor editor = P.edit();

            int rid = groupRight.getCheckedRadioButtonId();
            View rb = groupRight.findViewById(rid);
            int r = groupRight.indexOfChild(rb);

            int lid = groupLeft.getCheckedRadioButtonId();
            View lb = groupLeft.findViewById(lid);
            int l = groupLeft.indexOfChild(lb);

            editor.putInt("motor_right", pow2(r));
            editor.putInt("motor_left", pow2(l));

            editor.apply();

            /*DialogPreference pref = getPreference();
            if (pref instanceof MotorPortDialogPreference) {
                MotorPortDialogPreference mPref = (MotorPortDialogPreference) pref;
                mPref.setPorts((1 << r), (1 << l));
            }*/
        }
    }

    private void enableAll(RadioButton[] buttons) {
        for (RadioButton b : buttons) b.setEnabled(true);
    }

    private int log2(int x) {
        int y = 0, i = x;
        while (i > 1) {
            i = i >> 1;
            y++;
        }
        return y;
    }

    private int pow2(int x) {
        int y = 1, i = x;
        while (i > 0) {
            y = y << 1;
            i--;
        }
        return y;
    }
}
