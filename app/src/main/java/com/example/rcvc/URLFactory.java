package com.example.rcvc;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class URLFactory {

    public final String hostPlain, hostHttps, jitsiPlain, jitsiHttps;

    public URLFactory(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        hostPlain = pref.getString("host_url", "avatar.mintclub.org");
        hostHttps = "https://" + hostPlain;

        jitsiPlain = pref.getString("jitsi_url", "meet.jit.si");
        jitsiHttps = "https://" + jitsiPlain;
    }
}
