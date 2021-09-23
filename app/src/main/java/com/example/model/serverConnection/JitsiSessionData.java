package com.example.model.serverConnection;

import android.util.Log;

import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Represents options for a Jitsi video call session.
 */
public class JitsiSessionData extends SessionData<JitsiMeetConferenceOptions> {

    private static final String TAG = "JitsiSessionData";

    private JitsiMeetConferenceOptions jitsiOptions;

    public JitsiSessionData(String jitsi, String host, String id) {
        super(host, id);
        Log.d(TAG, "jitsi:"+jitsi+" host:"+host+" id:"+id);
        try {
            jitsiOptions = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(new URL(jitsi))
                    .setRoom(id)
                    .build();
            new URL(host);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public JitsiMeetConferenceOptions getOptions() {
        return this.jitsiOptions;
    }
}
