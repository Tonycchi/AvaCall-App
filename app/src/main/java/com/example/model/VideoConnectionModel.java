package com.example.model;

import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;

import com.example.data.LocalPreferenceDAO;
import com.example.data.URLSettings;
import com.example.model.robot.Controller;

import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.URI;
import java.net.URISyntaxException;

public class VideoConnectionModel {

    private final String TAG = "VideoConnection";

    private URLSettings urlSettings;
    private WebClient wc;
    private SessionData session;

    private Controller controller;

    public VideoConnectionModel(LocalPreferenceDAO db) {
        urlSettings = new URLSettings(db);
    }

    /**
     * creates connection w/ WebSocket, fetches share link and saves in inviteLink
     */
    public void invitePartner() {
        boolean connectionError = false;
        if (session == null) {
            String jitsi = urlSettings.getJitsi_https();
//            TODO: hardcode nur zum testen
//            String jitsi = "https://meet.jit.si";
            try {
                wc = new WebClient(new URI(urlSettings.getHost_wss()), urlSettings.getJitsi_plain(), null);
//                TODO: harcode nur zum testen
//                wc = new WebClient(new URI("wss://avatar.mintclub.org:22222"), "meet.jit.si", null);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            wc.connect();
            //continue with share link when ws is connected

            long startTime = System.currentTimeMillis();
            //check if a timeout occurs while connecting to server
            while (!wc.isReady()) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - startTime >= 5000) {
                    connectionError = true;
                    break;
                }
            }

            if (!connectionError) {
                String id = wc.getId();
                session = new SessionData(jitsi, urlSettings.getHost_https(), id);
                // TODO: Zum Videocall hier auf visible setzen!
            } else {
                // TODO: Fehlernachricht anzeigen
            }
        }
    }

    public URLSettings.Triple getCurrentURLs() {
        return urlSettings.getAll();
    }

    public void saveURLs(URLSettings.Triple urls) {
        urlSettings.saveURLs(urls);
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public String getShareURL() {
        return session.getShareURL();
    }

    public JitsiMeetConferenceOptions getOptions() {
        return session.getOptions();
    }

    public void setReceiveCommands(){
        wc.setReceiveCommands();
    }
}
