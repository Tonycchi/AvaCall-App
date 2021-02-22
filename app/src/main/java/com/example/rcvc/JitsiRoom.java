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
            /*
            see github.com/jitsi/jitsi-meet/blob/master/react/features/base/flags/constants.js
            for feature flags
             */
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
