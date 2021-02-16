package com.example.rcvc;

import android.util.Log;

import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

public class JitsiRoom {

    private final int ROOM_LINK_LENGTH = 10;

    public JitsiMeetConferenceOptions options;

    public JitsiRoom(String id, String host) throws MalformedURLException {
        try {
            options = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(new URL("https://" + host))
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

    /**
     * @param length the length for the id of the roomlink
     * @return the whole roomlink as a string which then can be copied
     */
    private String randomLinkString(int length) {
        Random random = new Random();
        char[] out = new char[length];
        for (int i = 0; i < length; i++) {
            out[i] = (char) (random.nextInt(26) + 97);
        }
        return new String(out);
    }
}
