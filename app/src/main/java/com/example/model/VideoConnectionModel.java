package com.example.model;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.data.LocalPreferenceDAO;
import com.example.data.URLSettings;
import com.example.model.robot.Controller;

import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;

public class VideoConnectionModel {

    private final String TAG = "VideoConnection";

    private URLSettings urlSettings;
    private WebClient webClient;
    private SessionData sessionData;
    private MutableLiveData<Boolean> videoReady;

    private Controller controller;

    public VideoConnectionModel(LocalPreferenceDAO db) {
        urlSettings = new URLSettings(db);
        videoReady = new MutableLiveData<>(false);
    }

    /**
     * creates connection w/ WebSocket, fetches share link and saves in inviteLink
     */
    public void invitePartner() {
        Log.d(TAG, "invite");
        //TODO: return wheter an connectionError occured or throw error! because when connectionError occurs the whole app crashes
        boolean connectionError = false;
        if (sessionData == null) {
            String videoURL = urlSettings.getVideoURL_https();
            try {
                //Log.d(TAG, "service " + ((EV3Controller)controller).service.toString());
                webClient = new WebClient(new URI(urlSettings.getHost_wss()), urlSettings.getVideoURL_plain(), controller);
            } catch (URISyntaxException e) {
                Log.d(TAG, "e");
                e.printStackTrace();
            }

            Log.d(TAG, "wc? " + (webClient != null));
            webClient.connect();//TODO: when error occurs in wc, stop waiting and directly throw error


            //continue with share link when ws is connected
            long currentTime;
            long startTime = System.currentTimeMillis();
            //check if a timeout occurs while connecting to server
            while (true) {
                try {
                    if (webClient.isReady()) break;
                } catch (ConnectException e) {
                    connectionError = true;
                    break;
                }
                currentTime = System.currentTimeMillis();
                if (currentTime - startTime >= 5000) {
                    connectionError = true;
                    Log.e(TAG, "connection timeout: jist:" + videoURL + " hostURL:" + urlSettings.getHost_https() + " port:" + urlSettings.getPort());
                    break;
                }
            }

            if (!connectionError) {
                String id = webClient.getId();
                //TODO: generalize
                sessionData = new JitsiSessionData(videoURL, urlSettings.getHost_https(), id);
                videoReady.setValue(true); // enables change to call button
            } else {
                Log.e(TAG, "connectionError on: jist:" + videoURL + " hostURL:" + urlSettings.getHost_https() + " port:" + urlSettings.getPort());
                // TODO: Passende fehlermedlung in app anzeigen
            }
        }
    }

    public MutableLiveData<Boolean> isVideoReady() {
        if (videoReady == null) videoReady = new MutableLiveData<>();
        return videoReady;
    }

    public URLSettings.stringTriple getCurrentURLs() {
        return urlSettings.getAll();
    }

    public String getID() {
        if (sessionData == null)
            return null;
        return sessionData.getID();
    }

    public void saveURLs(URLSettings.stringTriple urls) {
        urlSettings.saveURLs(urls);
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public String getShareURL() {
        return sessionData.getShareURL();
    }

    public Object getOptions() {
        return sessionData.getOptions();
    }

    public void setReceiveCommands() {
        webClient.setReceiveCommands();
    }
}
