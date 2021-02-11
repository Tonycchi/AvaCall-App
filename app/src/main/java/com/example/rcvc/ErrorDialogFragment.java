package com.example.rcvc;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class ErrorDialogFragment extends DialogFragment {
    public static final String MSG_KEY = "errnopair";
    Context context;
    String intentMessage;

    public ErrorDialogFragment(Context context, String intentMessage) {
        this.context = context;
        this.intentMessage = intentMessage;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        int messageID = getArguments().getInt(MSG_KEY);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(messageID)
                .setNegativeButton(R.string.dialog_close, (dialog, which) -> {
                    Intent intent = new Intent(context.getString(R.string.action_negative_button));
                    intent.putExtra("intent message", intentMessage);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}