package com.example.model;

import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;

public class SessionData {

    private JitsiMeetConferenceOptions options;
    public final String SHARE_URL;
    public final String ID;

    public SessionData(String jitsi, String host, String id) {
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

    public String getID(){
        return ID;
    }

    public JitsiMeetConferenceOptions getOptions() {
        return options;
    }
}
