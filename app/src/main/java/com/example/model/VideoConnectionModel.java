package com.example.model;

import androidx.lifecycle.MutableLiveData;

import java.net.URI;
import java.net.URISyntaxException;

public class VideoConnectionModel {

    private String TAG = "VideoConnection";

    private AvaCallModel model;
    private final MutableLiveData<String> inviteLink = new MutableLiveData<String>();

    private URLFactory urlFactory;
    private WebClient wc;
    private SessionData session;

    public VideoConnectionModel() {

    }

    public void invitePartner() {
        boolean connectionError = false;
        if (session == null) {
//            String jitsi = getUrlFactory().getJitsi_https(); TODO: hardcode nur zum testen
            String jitsi = "https://meet.jit.si";
            try {
//                setWebClient(new WebClient(new URI(getUrlFactory().getHost_wss()), getUrlFactory().getJitsi_plain(), null));
//                TODO: harcode nur zum testen
                wc = new WebClient(new URI("wss://avatar.mintclub.org:22222"), "meet.jit.si", null);
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
//                model.setSession(new SessionData(jitsi, model.getUrlFactory().getHost_https(), model.getId()));
//                TODO: hardcode nur zum testen
                session = new SessionData(jitsi, "https://avatar.mintclub.org", id);
                inviteLink.setValue(session.getShareURL());
                // TODO: Zum Videocall hier auf visible setzen!
            } else {
                // TODO: Fehlernachricht anzeigen
            }
        }
    }

    public MutableLiveData<String> getInviteLink() {
        return this.inviteLink;
    }

    public SessionData getSession() {
        return this.session;
    }
}
