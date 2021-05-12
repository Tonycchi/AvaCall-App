package com.example.model;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class URLFactory {

    public final String HOST_PLAIN, HOST_HTTPS, JITSI_PLAIN, JITSI_HTTPS, PORT, HOST_WSS;

    public URLFactory(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        HOST_PLAIN = pref.getString("host_url", "avatar.mintclub.org");
        HOST_HTTPS = "https://" + HOST_PLAIN;

        JITSI_PLAIN = pref.getString("jitsi_url", "meet.jit.si");
        JITSI_HTTPS = "https://" + JITSI_PLAIN;

        PORT = pref.getString("host_port", "22222");
        HOST_WSS = "wss://" + HOST_PLAIN + ":" + PORT;
    }
}
