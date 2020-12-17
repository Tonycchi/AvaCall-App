package com.example.rcvc;

import android.content.Intent;
import android.net.Uri;
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


    private FloatingActionButton endCall;

    private String roomID;
    JitsiMeetConferenceOptions options;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        {
            try {
                Bundle extras = getIntent().getExtras();
                String inputID = extras.getString("ROOM_ID");

                //TODO: do something about room title
                roomID = inputID+"#"
                        //+"config.disableInviteFunctions=true" //disable invite function of the app
                        +"&config.prejoinPageEnabled=true"; //show an intermediate page before joining to allow for adjustment of devices
                options = new JitsiMeetConferenceOptions.Builder()
                        .setServerURL(new URL("https://meet.jit.si"))
                        .setRoom(roomID)
                        .setAudioMuted(false)
                        .setVideoMuted(false)
                        .setAudioOnly(false)
                        .setWelcomePageEnabled(true)
                        .setFeatureFlag("pipEnabled", true)
                        .build();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        JitsiMeetActivity.launch(JitsiActivity.this, options);
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