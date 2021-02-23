package com.example.rcvc;

import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;

public class JitsiRoom {

    public JitsiMeetConferenceOptions options;

    public JitsiRoom(String host, String id) {
        try {
            options = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(new URL(host))
                    .setRoom(id)
                    .build();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
