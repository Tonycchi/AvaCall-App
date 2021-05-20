package com.example.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceManager;

import com.dropbox.core.v2.sharing.ShareFolderBuilder;
import com.example.rcvc.R;

public class URLDialogFragment extends DialogFragment {

    SharedPreferences pref;

    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        pref = PreferenceManager.getDefaultSharedPreferences(requireActivity());

        // get editText views
        View dialogView = requireActivity().getLayoutInflater().inflate(R.layout.url_settings, null);
        EditText editWebURL = dialogView.findViewById(R.id.edit_web_url),
                editJitsiURL = dialogView.findViewById(R.id.edit_jitsi_url),
                editWebPort = dialogView.findViewById(R.id.edit_web_port);

        // set editText texts = current values
        editWebURL.setText(pref.getString("host_url", ""));
        editJitsiURL.setText(pref.getString("jitsi_url", ""));
        editWebPort.setText(pref.getString("host_port", ""));

        // build dialog and
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
        // define what happens on press ok
        builder.setPositiveButton(getResources().getString(R.string.ok), (dialog, which) -> {
            SharedPreferences.Editor editor = pref.edit();
            String webURL = trimURL(editWebURL.getText().toString());
            String jitsiURL = trimURL(editJitsiURL.getText().toString());
            String webPort = trimURL(editWebPort.getText().toString());

            editor.putString("host_url", webURL);
            editor.putString("jitsi_url", jitsiURL);
            editor.putString("host_port", webPort);
            editor.apply();
        });

        AlertDialog alertDialog = builder.create();

        TextWatcher urlWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                handleText();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
            /**
             * checks if entered string is a valid url (syntax)
             */
            private void handleText() {
                final Button okButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                String webURL = editWebURL.getText().toString();
                String jitsiURL = editJitsiURL.getText().toString();
                okButton.setEnabled(
                    Patterns.WEB_URL.matcher(webURL).matches() &&
                    Patterns.WEB_URL.matcher(jitsiURL).matches()
                );
            }
        };

        editJitsiURL.addTextChangedListener(urlWatcher);
        editWebURL.addTextChangedListener(urlWatcher);

        return alertDialog;
    }

    /**
     * trim url, ie remove protocols, last /, etc
     * @param url url
     * @return trimmed url
     */
    private String trimURL(String url) {
        String r = url.replaceFirst("^(http[s]?://www\\.|http[s]?://|www\\.)", "");
        if (r.charAt(r.length() - 1) == '/') {
            r = r.substring(0, r.length() - 1);
        }
        return r;
    }

    public static final String TAG = "URLDialogFragment";
}
