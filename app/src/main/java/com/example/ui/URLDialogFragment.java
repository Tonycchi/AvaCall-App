package com.example.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.MainViewModel;
import com.example.data.URLSettings;
import com.example.rcvc.R;

import org.jetbrains.annotations.NotNull;

public class URLDialogFragment extends DialogFragment {

    public static final String TAG = "URLDialogFragment";
    MainViewModel viewModel;

    @NotNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        // get editText views
        View dialogView = requireActivity().getLayoutInflater().inflate(R.layout.url_settings, null);
        EditText editWebURL = dialogView.findViewById(R.id.edit_web_url),
                editJitsiURL = dialogView.findViewById(R.id.edit_jitsi_url),
                editWebPort = dialogView.findViewById(R.id.edit_web_port);

        // set editText texts = current values
        URLSettings.stringTriple t = viewModel.getCurrentURLs();
        String currentWebURL = t.getHostURL(),
                currentJitsiURL = t.getVideoURL(),
                currentWebPort = t.getPort();
        editWebURL.setText(currentWebURL);
        editJitsiURL.setText(currentJitsiURL);
        editWebPort.setText(currentWebPort);

        // build dialog and
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
        // define what happens on press ok (save strings)
        builder.setPositiveButton(getResources().getString(R.string.ok), (dialog, which) -> {
            String webURL = editWebURL.getText().toString();
            String jitsiURL = editJitsiURL.getText().toString();
            String webPort = editWebPort.getText().toString();

            viewModel.saveURLs(new URLSettings.stringTriple(webURL, jitsiURL, webPort));
        });

        AlertDialog alertDialog = builder.create();

        // urlWatcher verifies that only valid urls can be saved
        TextWatcher urlWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final Button okButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);

                okButton.setEnabled(Patterns.WEB_URL.matcher(s).matches());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        editJitsiURL.addTextChangedListener(urlWatcher);
        editWebURL.addTextChangedListener(urlWatcher);

        // set ok disabled if no values yet present
        alertDialog.setOnShowListener(dialog -> {
            if (currentWebURL == null || currentWebURL.length() == 0 || currentJitsiURL.length() == 0 || currentWebPort.length() == 0) {
                final Button okButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                okButton.setEnabled(false);
            }
        });

        return alertDialog;
    }
}
