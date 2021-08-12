package com.example.model;

import android.util.Log;

import com.example.data.LocalPreferenceDAO;
import com.example.data.URLSettings;
import com.example.model.robot.Controller;
import com.example.model.robot.ev3.EV3Controller;

import java.net.URI;
import java.net.URISyntaxException;

public class VideoConnectionModel {

    private final String TAG = "VideoConnectionModel";

    private URLSettings urlSettings;
    private WebClient webClient;
    private SessionData sessionData;

    private Controller controller;

    public VideoConnectionModel(LocalPreferenceDAO db) {
        urlSettings = new URLSettings(db);
    }

    /**
     * creates connection w/ WebSocket, fetches share link and saves in inviteLink
     */
    public boolean invitePartner() {
        Log.d(TAG, "invite");
        if (sessionData == null) {
            String videoURL = urlSettings.getVideoURL_https();
            try {
                Log.d(TAG, "service " + ((EV3Controller)controller).service.toString());
                webClient = new WebClient(new URI(urlSettings.getHost_wss()), urlSettings.getVideoURL_plain(), controller);
            } catch (URISyntaxException e) {
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            }

            Log.d(TAG, "wc? " + (webClient != null));
            webClient.connect();


            //continue with share link when ws is connected
            long startTime = System.currentTimeMillis();
            //check if a timeout occurs while connecting to server
            while (webClient.getStatus()==0) { //while there is no connection
                long currentTime = System.currentTimeMillis();
                if (currentTime - startTime >= 5000) {
                    Log.e(TAG, "connection timeout: jist:"+videoURL+" hostURL:"+urlSettings.getHost_https()+" port:"+urlSettings.getPort());
                    break;
                }
            }

            if (webClient.getStatus()==1) { //success
                String id = webClient.getId();
                //TODO: generalize
                sessionData = new JitsiSessionData(videoURL, urlSettings.getHost_https(), id);
                // TODO: Zum Videocall hier auf visible setzen!
            } else {//error
               Log.e(TAG, "connectionError on: jist:"+videoURL+" hostURL:"+urlSettings.getHost_https()+" port:"+urlSettings.getPort());
            }
        }

        return webClient.getStatus()==1;
    }

    public URLSettings.stringTriple getCurrentURLs() {
        return urlSettings.getAll();
    }

    public boolean isConnected(){
        if(webClient==null)
            return false;
        return webClient.getStatus()==1;
    }

    public String getID(){
        if(sessionData==null)
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

    public void setReceiveCommands(){
        webClient.setReceiveCommands();
    }
}
