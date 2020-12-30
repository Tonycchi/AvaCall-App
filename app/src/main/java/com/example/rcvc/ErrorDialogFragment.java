package com.example.rcvc;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class ErrorDialogFragment extends DialogFragment {
    public static final String MSG_KEY = "errnopair";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        int messageID = getArguments().getInt(MSG_KEY);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(messageID)
                .setNegativeButton(R.string.dialog_close, (dialog, id) -> {
                    // User cancelled the dialog
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
