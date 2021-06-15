package com.example.model;

import android.content.SharedPreferences;
import android.util.Log;

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
            try {
                wc = new WebClient(new URI(urlSettings.getHost_wss()), urlSettings.getJitsi_plain(), null);
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
                    Log.e(TAG, "connection timeout: jist:"+jitsi+" hostURL:"+urlSettings.getHost_https()+" port:"+urlSettings.getPort());
                    break;
                }
            }

            if (!connectionError) {
                String id = wc.getId();
                session = new SessionData(jitsi, urlSettings.getHost_https(), id);
                // TODO: Zum Videocall hier auf visible setzen!
            } else {
               Log.e(TAG, "connectionError on: jist:"+jitsi+" hostURL:"+urlSettings.getHost_https()+" port:"+urlSettings.getPort());
                // TODO: Passende fehlermedlung in app anzeigen
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
