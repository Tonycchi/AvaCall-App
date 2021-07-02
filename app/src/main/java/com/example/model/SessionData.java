package com.example.model;

import android.util.Log;

import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;

public abstract class SessionData<OptionType extends Object>{

    private static final String TAG = "SessionData";

    private final String SHARE_URL;
    private final String ID;

    public SessionData(String host, String id) {
        Log.d(TAG, " host:"+host+" id:"+id);
        this.ID = id;
        this.SHARE_URL = host + "/" + id;
    }

    public String getShareURL() {return this.SHARE_URL; }

    public String getID(){
        return this.ID;
    }

    abstract public OptionType getOptions();
}
