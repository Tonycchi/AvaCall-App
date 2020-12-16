package com.example.rcvc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.incoming_call.IncomingCallView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

public class JitsiActivity extends JitsiMeetActivity  {

    private char[] chars = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70, 71, 72,
            73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100,
            101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117,
            118, 119, 120, 121};

    private FloatingActionButton endCall;

    private String roomID;
    JitsiMeetConferenceOptions options;

    {
        try {
            roomID = randomLinkString(6);
            options = new JitsiMeetConferenceOptions.Builder()
                        .setServerURL(new URL("https://meet.jit.si"))
                        .setRoom(roomID)
                        .setAudioMuted(false)
                        .setVideoMuted(false)
                        .setAudioOnly(false)
                        .setWelcomePageEnabled(true)
                        .build();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        JitsiMeetActivity.launch(JitsiActivity.this, options);
    }

    private String randomLinkString(int length) {
        Random random = new Random();
        char[] out = new char[length];
        for (int i = 0; i < length; i++) {
            int rnd = random.nextInt(chars.length);
            out[i] = chars[rnd];
        }
        return new String(out);
    }

    /**
     * @param v
     * Switches back to the main activity and closes the jitsi room
     */
    public void onClickEndCall(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}