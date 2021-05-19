package com.example.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.rcvc.R;

public class URLDialogFragment extends DialogFragment {

    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireContext())
                .setView(R.layout.url_settings)
                .setPositiveButton("ok", this::onPositive)
                .create();
    }

    private void onPositive(DialogInterface dialog, int which) {
        
    }

    public static final String TAG = "URLDialogFragment";
}
