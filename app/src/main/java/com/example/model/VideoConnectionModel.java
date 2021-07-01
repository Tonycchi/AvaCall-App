package com.example.model;

import android.util.Log;

import com.example.data.LocalPreferenceDAO;
import com.example.data.URLSettings;
import com.example.model.robot.Controller;

import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.URI;
import java.net.URISyntaxException;

public class VideoConnectionModel {

    private final String TAG = "VideoConnection";

    private URLSettings urlSettings;
    private WebClient webClient;
    private JitsiSessionData jitsiSessionData;

    private Controller controller;

    public VideoConnectionModel(LocalPreferenceDAO db) {
        urlSettings = new URLSettings(db);
    }

    /**
     * creates connection w/ WebSocket, fetches share link and saves in inviteLink
     */
    public void invitePartner() {
        Log.d(TAG, "invite");
        //TODO: return wheter an connectionError occured or throw error! because when connectionError occurs the whole app crashes
        boolean connectionError = false;
        if (jitsiSessionData == null) {
            String jitsi = urlSettings.getJitsi_https();
            try {
                //Log.d(TAG, "service " + ((EV3Controller)controller).service.toString());
                webClient = new WebClient(new URI(urlSettings.getHost_wss()), urlSettings.getJitsi_plain(), controller);
            } catch (URISyntaxException e) {
                Log.d(TAG, "e");
                e.printStackTrace();
            }

            Log.d(TAG, "wc? " + (webClient != null));
            webClient.connect();//TODO: when error occurs in wc, stop waiting and directly throw error


            //continue with share link when ws is connected
            long startTime = System.currentTimeMillis();
            //check if a timeout occurs while connecting to server
            while (!webClient.isReady()) { //TODO: when error occurs in wc, stop waiting and directly throw error
                long currentTime = System.currentTimeMillis();
                if (currentTime - startTime >= 5000) {
                    connectionError = true;
                    Log.e(TAG, "connection timeout: jist:"+jitsi+" hostURL:"+urlSettings.getHost_https()+" port:"+urlSettings.getPort());
                    break;
                }
            }

            if (!connectionError) {
                String id = webClient.getId();
                jitsiSessionData = new JitsiSessionData(jitsi, urlSettings.getHost_https(), id);
                // TODO: Zum Videocall hier auf visible setzen!
            } else {
               Log.e(TAG, "connectionError on: jist:"+jitsi+" hostURL:"+urlSettings.getHost_https()+" port:"+urlSettings.getPort());
                // TODO: Passende fehlermedlung in app anzeigen
            }
        }
    }

    public URLSettings.stringTriple getCurrentURLs() {
        return urlSettings.getAll();
    }

    public void saveURLs(URLSettings.stringTriple urls) {
        urlSettings.saveURLs(urls);
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public String getShareURL() {
        return jitsiSessionData.getShareURL();
    }

    public JitsiMeetConferenceOptions getOptions() {
        return jitsiSessionData.getOptions();
    }

    public void setReceiveCommands(){
        webClient.setReceiveCommands();
    }
}
