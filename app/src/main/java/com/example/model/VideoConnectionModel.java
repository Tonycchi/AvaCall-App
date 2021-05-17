package com.example.model;

import androidx.lifecycle.MutableLiveData;

import java.net.URI;
import java.net.URISyntaxException;

public class VideoConnectionModel {

    private AvaCallModel model;
    private MutableLiveData<String> inviteLink = new MutableLiveData<String>();

    public VideoConnectionModel (AvaCallModel model) {
        this.model = model;
    }

    public void invitePartner() {
        boolean connectionError = false;
        if (model.getSession() == null) {
//            String jitsi = model.getUrlFactory().getJitsi_https(); TODO: hardcode nur zum testen
            String jitsi = "https://meet.jit.si";
            try {
//                model.setWebClient(new WebClient(new URI(model.getUrlFactory().getHost_wss()), model.getUrlFactory().getJitsi_plain(), null));
//                TODO: harcode nur zum testen
                model.setWebClient(new WebClient(new URI("wss://avatar.mintclub.org:22222"), "meet.jit.si", null));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            model.getWebClient().connect();
            //continue with share link when ws is connected

            long startTime = System.currentTimeMillis();
            //check if a timeout occurs while connecting to server
            while(!model.getWebClient().isReady()) {
                long currentTime = System.currentTimeMillis();
                if (currentTime -startTime >= 5000) {
                    connectionError = true;
                    break;
                }
            }

            if (!connectionError) {
                model.setId(model.getWebClient().getId());
//                model.setSession(new SessionData(jitsi, model.getUrlFactory().getHost_https(), model.getId()));
//                TODO: hardcode nur zum testen
                model.setSession(new SessionData(jitsi, "https://avatar.mintclub.org", model.getId()));
                inviteLink.setValue(model.getSession().getShareURL());
                // TODO: Zum Videocall hier auf visible setzen!
            } else {
                // TODO: Fehlernachricht anzeigen
            }
        }
    }

    public MutableLiveData<String> getInviteLink() {
        return this.inviteLink;
    }
}
