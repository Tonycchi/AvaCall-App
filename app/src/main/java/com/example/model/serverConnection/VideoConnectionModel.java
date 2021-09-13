package com.example.model.serverConnection;

import static com.example.Constants.JITSI_VIDEOCALL;
import static com.example.Constants.USED_VIDEOALL;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.data.LocalPreferenceDAO;
import com.example.data.URLSettings;
import com.example.model.robot.Controller;
import com.example.model.robot.ev3.EV3Controller;

import java.net.URI;
import java.net.URISyntaxException;

public class VideoConnectionModel {

    private final String TAG = "VideoConnectionModel";

    private final URLSettings urlSettings;
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
    public boolean invitePartner() {
        Log.d(TAG, "invite");
        if (sessionData == null || webClient == null || webClient.getStatus()!=1) {
            String videoURL = urlSettings.getVideoURL_https();
            try {
                Log.d(TAG, "service " + ((EV3Controller)controller).service.toString());
                webClient = new WebClient(new URI(urlSettings.getHost_wss()), urlSettings.getVideoURL_plain(), controller);
            } catch (URISyntaxException e) {
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            }

            Log.d(TAG, "wc!=null " + (webClient != null));
            webClient.connect();

            //continue with share link when ws is connected
            long currentTime;
            long startTime = System.currentTimeMillis();
            //check if a timeout occurs while connecting to server
            while (webClient.getStatus()==0) { //while there is no connection
                currentTime = System.currentTimeMillis();
                if (currentTime - startTime >= 5000) {
                    Log.e(TAG, "connection timeout: videoURL:"+videoURL+" hostURL:"+urlSettings.getHost_https()+" port:"+urlSettings.getPort());
                    break;
                }
            }

            if (webClient.getStatus()==1) { //success
                String id = webClient.getId();
                switch(USED_VIDEOALL){
                    case JITSI_VIDEOCALL:
                        sessionData = new JitsiSessionData(videoURL, urlSettings.getHost_https(), id);
                        break;
                    default:
                        Log.e(TAG, "No videocall software used");
                }
                videoReady.setValue(true); //enables change to call button
            } else {//error
                cancelConnection();
                Log.e(TAG, "connectionError on: videoURL:"+videoURL+" hostURL:"+urlSettings.getHost_https()+" port:"+urlSettings.getPort());
            }
        }

        return webClient.getStatus()==1;
    }

    public MutableLiveData<Boolean> isVideoReady() {
        if (videoReady == null) videoReady = new MutableLiveData<>();
        return videoReady;
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

    public void setReceiveCommands(boolean receiveCommands){
        if(webClient!=null)
            webClient.setReceiveCommands(receiveCommands);
    }

    public void cancelConnection() {
        if(webClient != null && webClient.getStatus()==1)
            webClient.close();
        videoReady.setValue(false);
    }

    public void sendStallDetected(String controlElementType, int controlElementId) {
        webClient.sendStallDetected(controlElementType, controlElementId);
    }

    public void sendStallEnded(String controlElementType, int controlElementId) {
        webClient.sendStallEnded(controlElementType, controlElementId);
    }
}
