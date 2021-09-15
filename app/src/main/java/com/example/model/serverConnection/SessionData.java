package com.example.model.serverConnection;

import android.util.Log;

public abstract class SessionData<OptionType> {

    private static final String TAG = "SessionData";

    private final String SHARE_URL;
    private final String ID;

    public SessionData(String host, String id) {
        Log.d(TAG, " host:" + host + " id:" + id);
        this.ID = id;
        this.SHARE_URL = host + "/?meetingId=" + id;
    }

    public String getShareURL() {
        return this.SHARE_URL;
    }

    public String getID() {
        return this.ID;
    }

    abstract public OptionType getOptions();
}
