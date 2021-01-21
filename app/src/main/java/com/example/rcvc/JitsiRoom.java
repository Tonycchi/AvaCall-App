package com.example.rcvc;

import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

public class JitsiRoom {

    private final char[] CHARS = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69,
            70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89,
            90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112,
            113, 114, 115, 116, 117, 118, 119, 120, 121};
    private final int ROOM_LINK_LENGTH = 6;

    public final String id, url;
    public JitsiMeetConferenceOptions options;

    public JitsiRoom(String host) {
        id = randomLinkString(ROOM_LINK_LENGTH);
        url = "https://meet." + host + "/" + id;
        try {
            options = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(new URL("https://meet." + host))
                    .setRoom(id)
                    .setSubject("Robot") //TODO besserer Titel
                    .setAudioMuted(false)
                    .setVideoMuted(false)
                    .setAudioOnly(false)
                    .setWelcomePageEnabled(true)
                    //.setFeatureFlag("pip.enabled", false) // (1)
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
            int rnd = random.nextInt(CHARS.length);
            out[i] = CHARS[rnd];
        }
        return new String(out);
    }
}
