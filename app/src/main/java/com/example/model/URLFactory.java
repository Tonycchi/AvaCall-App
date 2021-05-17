package com.example.model;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;
/*
public class URLFactory {

    public final String HOST_PLAIN, HOST_HTTPS, JITSI_PLAIN, JITSI_HTTPS, PORT, HOST_WSS;

    public URLFactory(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        HOST_PLAIN = pref.getString("host_url", "avatar.mintclub.org");
        HOST_HTTPS = "https://" + HOST_PLAIN;

        JITSI_PLAIN = pref.getString("jitsi_url", "meet.jit.si");
        JITSI_HTTPS = "https://" + JITSI_PLAIN;

        PORT = pref.getString("host_port", "22222");
        HOST_WSS = "wss://" + HOST_PLAIN + ":" + PORT;
    }
}
*/

public class URLFactory {



    private String host_plain;
    private String host_https;
    private String jitsi_plain;
    private String jitsi_https;
    private String port;
    private String host_wss;


    public URLFactory(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        this.setHost(pref.getString("host_url", "avatar.mintclub.org"));
        this.setJitsi(pref.getString("jitsi_url", "meet.jit.si"));
        this.setPort(pref.getString("host_port", "22222"));
    }


    public void setHost(String plain){
        this.host_plain = plain;
        this.host_https = "https://" + plain;
        this.setHost_wss();
    }

    public void setJitsi(String plain){
        this.jitsi_plain = plain;
        this.jitsi_https = "https://" + plain;
    }

    public void setPort(String port){
        this.port = port;
        this.setHost_wss();
    }

    public void setHost_wss(){
        this.host_wss = "wss://" + this.getHost_plain() + ":" + this.getPort();
    }

    public String getHost_plain(){
        return this.host_plain;
    }

    public String getHost_https(){
        return this.host_https;
    }

    public String getJitsi_plain(){
        return this.jitsi_plain;
    }

    public String getJitsi_https(){
        return this.jitsi_https;
    }

    public String getPort(){
        return this.port;
    }

    public String getHost_wss(){
        return this.host_wss;
    }
}
