package com.example.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.rcvc.R;

public class ErrorDialogFragment extends DialogFragment {
    public static final String MSG_KEY = "errnopair";
    private final Context CONTEXT;
    private final String INTENT_MESSAGE;

    public ErrorDialogFragment(Context context, String intentMessage) {
        this.CONTEXT = context;
        this.INTENT_MESSAGE = intentMessage;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        int messageID = getArguments().getInt(MSG_KEY);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(messageID)
                .setNegativeButton(R.string.dialog_close, (dialog, which) -> {
                    Intent intent = new Intent(CONTEXT.getString(R.string.action_negative_button));
                    intent.putExtra("intent message", INTENT_MESSAGE);
                    LocalBroadcastManager.getInstance(CONTEXT).sendBroadcast(intent);
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}