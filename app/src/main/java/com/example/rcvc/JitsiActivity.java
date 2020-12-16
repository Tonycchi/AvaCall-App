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

public class JitsiActivity extends JitsiMeetActivity  {

    private FloatingActionButton endCall;

    JitsiMeetConferenceOptions options;

    {
        try {
            options = new JitsiMeetConferenceOptions.Builder()
                        .setServerURL(new URL("https://meet.jit.si"))
                        .setRoom("test123")
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
        setContentView(R.layout.activity_jitsi);

        JitsiMeetActivity.launch(JitsiActivity.this, options);

        //endCall = findViewById(R.id.button_end_call);
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