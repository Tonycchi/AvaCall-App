package com.example.rcvc;

import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;

public class SessionData {

    private JitsiMeetConferenceOptions options;
    public final String shareURL;

    public SessionData(String jitsi, String host, String id) {
        try {
            options = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(new URL(jitsi))
                    .setRoom(id)
                    .build();
            new URL(host);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        shareURL = host + "/" + id;
    }

    public JitsiMeetConferenceOptions getOptions() {
        return options;
    }
}
