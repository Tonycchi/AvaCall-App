package com.example.rcvc;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class SettingsFragment extends PreferenceFragmentCompat {

    private SharedPreferences pref;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        if (getActivity() != null) {
            pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        }

        EditTextPreference port = findPreference("host_port");
        if (port != null) {
            port.setOnBindEditTextListener(
                    editText -> editText.setInputType(InputType.TYPE_CLASS_NUMBER));
        }
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        // Try if the preference is one of our custom Preferences
        DialogFragment dialogFragment = null;
        if (preference instanceof MotorPortDialogPreference) {
            // Create a new instance of TimePreferenceDialogFragment with the key of the related
            // Preference
            dialogFragment = MotorPortDialogFragment
                    .newInstance(preference.getKey());
        }

        // If it was one of our custom Preferences, show its dialog
        if (dialogFragment != null) {
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(this.getParentFragmentManager(),
                    "android.support.v7.preference" +
                            ".PreferenceFragment.DIALOG");
        }
        // Could not be handled here. Try with the super method.
        else {
            super.onDisplayPreferenceDialog(preference);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(Preference p) {
        if (p.getKey().equals("host_url") || p.getKey().equals("jitsi_url")) {
            // start building url dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = requireActivity().getLayoutInflater();

            View dialogView = inflater.inflate(R.layout.preference_url, null);
            EditText editText = dialogView.findViewById(R.id.input_url);

            String defVal = pref.getString(p.getKey(), "");

            // no auto-correct
            editText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

            String currentVal = pref.getString(p.getKey(), defVal);
            editText.setText(currentVal);

            builder.setView(dialogView)
                    .setPositiveButton(R.string.ok, (dialog, which) -> {
                        SharedPreferences.Editor editor = pref.edit();
                        String url = editText.getText().toString();

                        // trim url, ie remove protocols, last /, etc
                        url = url.replaceFirst("^(http[s]?://www\\.|http[s]?://|www\\.)", "");
                        if (url.charAt(url.length() - 1) == '/') {
                            url = url.substring(0, url.length() - 1);
                        }

                        editor.putString(p.getKey(), url);
                        editor.apply();
                    })
                    .setNegativeButton(R.string.dialog_close, (dialog, which) -> dialog.cancel());

            AlertDialog d = builder.create();

            if (p.getKey().equals("host_url")) {
                d.setTitle(R.string.settings_title_host_url);
            } else {
                d.setTitle(R.string.settings_title_jitsi_url);
            }

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    handleText();
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    handleText();
                }

                /**
                 * checks if entered string is a valid url (syntax)
                 */
                private void handleText() {
                    final Button okButton = d.getButton(AlertDialog.BUTTON_POSITIVE);
                    String text = editText.getText().toString();
                    okButton.setEnabled(Patterns.WEB_URL.matcher(text).matches());
                }
            });

            d.show();
        }
        return true;
    }
}
