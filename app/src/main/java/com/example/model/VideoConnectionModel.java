package com.example.model;

import androidx.lifecycle.MutableLiveData;

import org.jitsi.meet.sdk.JitsiMeetActivity;

import java.net.URI;
import java.net.URISyntaxException;

public class VideoConnectionModel {

    private AvaCallModel model;
    private MutableLiveData<String> inviteLink = new MutableLiveData<String>();

    private URLFactory urlFactory;
    private WebClient wc;
    private String id;
    private SessionData session;

    public VideoConnectionModel () {
        this.model = model;
    }

    public void invitePartner() {
        boolean connectionError = false;
        if (getSession() == null) {
//            String jitsi = getUrlFactory().getJitsi_https(); TODO: hardcode nur zum testen
            String jitsi = "https://meet.jit.si";
            try {
//                setWebClient(new WebClient(new URI(getUrlFactory().getHost_wss()), getUrlFactory().getJitsi_plain(), null));
//                TODO: harcode nur zum testen
                setWebClient(new WebClient(new URI("wss://avatar.mintclub.org:22222"), "meet.jit.si", null));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            getWebClient().connect();
            //continue with share link when ws is connected

            long startTime = System.currentTimeMillis();
            //check if a timeout occurs while connecting to server
            while(getWebClient().isReady()) {
                long currentTime = System.currentTimeMillis();
                if (currentTime -startTime >= 5000) {
                    connectionError = true;
                    break;
                }
            }

            if (!connectionError) {
                setId(getWebClient().getId());
//                model.setSession(new SessionData(jitsi, model.getUrlFactory().getHost_https(), model.getId()));
//                TODO: hardcode nur zum testen
                setSession(new SessionData(jitsi, "https://avatar.mintclub.org", getId()));
                inviteLink.setValue(getSession().getShareURL());
                // TODO: Zum Videocall hier auf visible setzen!
            } else {
                // TODO: Fehlernachricht anzeigen
            }
        }
    }

    public MutableLiveData<String> getInviteLink() {
        return this.inviteLink;
    }

    public URLFactory getUrlFactory() {
        return this.urlFactory;
    }

    public WebClient getWebClient() {
        return this.wc;
    }

    public void setWebClient (WebClient webClient) {
        this.wc = webClient;
    }

    public String getId() {
        return this.id;
    }

    public void setId (String ID) {
        this.id = ID;
    }

    public SessionData getSession() {
        return this.session;
    }

    public void setSession(SessionData sessionData) {
        this.session = sessionData;
    }
}
