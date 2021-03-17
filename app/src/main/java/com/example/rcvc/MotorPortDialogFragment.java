package com.example.rcvc;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.preference.PreferenceDialogFragmentCompat;
import androidx.preference.PreferenceManager;

public class MotorPortDialogFragment extends PreferenceDialogFragmentCompat {

    private SharedPreferences pref;

    private RadioGroup groupRight, groupLeft;
    private RadioButton[] buttonsRight, buttonsLeft;

    public static MotorPortDialogFragment newInstance(String key) {
        final MotorPortDialogFragment FRAGMENT = new MotorPortDialogFragment();
        final Bundle B = new Bundle(1);
        B.putString(ARG_KEY, key);
        FRAGMENT.setArguments(B);

        return FRAGMENT;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        Context context = getContext();
        if (context == null) throw new IllegalStateException();
        pref = PreferenceManager.getDefaultSharedPreferences(getContext());

        groupRight = view.findViewById(R.id.radio_group_right_port);
        groupLeft = view.findViewById(R.id.radio_group_left_port);

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

        // turn saved values into indices
        int rIdx = motorToIndex(pref.getInt("motor_right", 1));
        int lIdx = motorToIndex(pref.getInt("motor_left", 8));

        // and set up UI accordingly
        buttonsRight[rIdx].setChecked(true);
        buttonsLeft[lIdx].setChecked(true);

        buttonsRight[lIdx].setEnabled(false);
        buttonsLeft[rIdx].setEnabled(false);
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            SharedPreferences.Editor editor = pref.edit();

            // get indices of checked radio buttons for right
            int rid = groupRight.getCheckedRadioButtonId();
            View rb = groupRight.findViewById(rid);
            int rIdx = groupRight.indexOfChild(rb);
            // and left
            int lid = groupLeft.getCheckedRadioButtonId();
            View lb = groupLeft.findViewById(lid);
            int lIdx = groupLeft.indexOfChild(lb);

            // save them in preferences
            editor.putInt("motor_right", indexToMotor(rIdx));
            editor.putInt("motor_left", indexToMotor(lIdx));

            editor.apply();
        }
    }

    /**
     * enables all the radio buttons
     * @param buttons the buttons to enable
     */
    private void enableAll(RadioButton[] buttons) {
        for (RadioButton b : buttons) b.setEnabled(true);
    }

    /**
     * motor number to array index
     * @param x input, normally 1, 2, 4 oder 8, (corresponding to ev3 motors)
     * @return base 2 log of input as integer by bit shifting
     */
    private int motorToIndex(int x) {
        int y = 0, i = x;
        while (i > 1) {
            i = i >> 1;
            y++;
        }
        return y;
    }

    /**
     * array index to motor number
     * @param x shift amount
     * @return 1 shifted to the left by x
     */
    private int indexToMotor(int x) {
        int y = 1, i = x;
        while (i > 0) {
            y = y << 1;
            i--;
        }
        return y;
    }
}
