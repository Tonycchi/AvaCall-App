package com.example.rcvc;

import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;

public class JitsiRoom {
    public static JitsiMeetConferenceOptions createRoom(String url, String id) throws MalformedURLException {
        return new JitsiMeetConferenceOptions.Builder()
                .setServerURL(new URL(url))
                .setRoom(id)
                .build();
    }
}
