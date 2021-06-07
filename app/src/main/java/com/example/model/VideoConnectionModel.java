package com.example.model;

import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;

import com.example.data.URLFactory;
import com.example.model.robot.Controller;

import java.net.URI;
import java.net.URISyntaxException;

public class VideoConnectionModel {

    private final String TAG = "VideoConnection";

    // observed by VideoConnectionFragment
    private final MutableLiveData<String> inviteLink = new MutableLiveData<>();

    private URLFactory urlFactory;
    private WebClient wc;
    private SessionData session;

    private Controller controller;

    public VideoConnectionModel(SharedPreferences sharedPreferences) {
        urlFactory = new URLFactory(sharedPreferences);
    }

    /**
     * creates connection w/ WebSocket, fetches share link and saves in inviteLink
     */
    public void invitePartner() {
        boolean connectionError = false;
        if (session == null) {
            String jitsi = urlFactory.getJitsi_https();
//            TODO: hardcode nur zum testen
//            String jitsi = "https://meet.jit.si";
            try {
                wc = new WebClient(new URI(urlFactory.getHost_wss()), urlFactory.getJitsi_plain(), controller);
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
                session = new SessionData(jitsi, urlFactory.getHost_https(), id);
//                TODO: hardcode nur zum testen
//                session = new SessionData(jitsi, "https://avatar.mintclub.org", id);
                inviteLink.setValue(session.getShareURL());
                // TODO: Zum Videocall hier auf visible setzen!
            } else {
                // TODO: Fehlernachricht anzeigen
            }
        }
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public MutableLiveData<String> getInviteLink() {
        return this.inviteLink;
    }

    public SessionData getSession() {
        return this.session;
    }

    public void setReceiveCommands() { wc.setReceiveCommands(); }
}
