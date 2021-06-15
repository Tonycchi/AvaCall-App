package com.example.model;

import android.util.Log;

import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;

public class SessionData {

    private static final String TAG = "SessionData";

    private JitsiMeetConferenceOptions options;
    private final String SHARE_URL;
    private final String ID;

    public SessionData(String jitsi, String host, String id) {
        Log.d(TAG, "jitsi:"+jitsi+" host:"+host+" id:"+id);
        this.ID = id;
        try {
            options = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(new URL(jitsi))
                    .setRoom(id)
                    .build();
            new URL(host);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        SHARE_URL = host + "/" + id;
    }

    public JitsiMeetConferenceOptions getOptions() {
        return this.options;
    }

    public String getShareURL() {return this.SHARE_URL; }

    public String getID(){
        return this.ID;
    }
}
