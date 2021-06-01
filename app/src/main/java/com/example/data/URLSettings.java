package com.example.data;

/*
public class URLFactory {

    public final String HOST_PLAIN, HOST_HTTPS, JITSI_PLAIN, JITSI_HTTPS, PORT, HOST_WSS;

    public URLFactory(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        HOST_PLAIN = pref.getString("host_url", "avatar.mintclub.org");
        HOST_HTTPS = "https://" + HOST_PLAIN;

        JITSI_PLAIN = pref.getString("jitsi_url", "meet.jit.si");
        JITSI_HTTPS = "https://" + JITSI_PLAIN;

        PORT = pref.getString("host_port", "22222");
        HOST_WSS = "wss://" + HOST_PLAIN + ":" + PORT;
    }
}
*/

public class URLSettings {

    private final static String HOSTURLKEY = "host_url",
            JITSIURLKEY = "jitsi_url",
            HOSTPORTKEY = "host_port",
            HTTPS = "https://",
            WSS = "wss://";

    public LocalPreferenceDAO db;

    public URLSettings(LocalPreferenceDAO db) {
        this.db = db;
    }

    public void saveURLs(Triple urls) {
        db.insertAll(
                new LocalPreference(HOSTURLKEY, urls.getHostURL()),
                new LocalPreference(JITSIURLKEY, urls.getJitsiURL()),
                new LocalPreference(HOSTPORTKEY, urls.getPort())
        );
    }

    public Triple getAll() {
        return new Triple(db.get(HOSTURLKEY), db.get(JITSIURLKEY), db.get(HOSTPORTKEY));
    }

    public String getHost_plain() {
        return db.get(HOSTURLKEY);
    }

    public String getHost_https() {
        return HTTPS + db.get(HOSTURLKEY);
    }

    public String getJitsi_plain() {
        return db.get(JITSIURLKEY);
    }

    public String getJitsi_https() {
        return HTTPS + db.get(JITSIURLKEY);
    }

    public String getPort() {
        return db.get(HOSTPORTKEY);
    }

    public String getHost_wss() {
        return WSS + db.get(HOSTURLKEY) + ":" + db.get(HOSTPORTKEY);
    }

    public static class Triple {
        private String hostURL;
        private String jitsiURL;
        private String port;

        public Triple(String hostURL, String jitsiURL, String port) {
            this.hostURL = hostURL;
            this.jitsiURL = jitsiURL;
            this.port = port;
        }

        public String getHostURL() {
            return hostURL;
        }

        public String getJitsiURL() {
            return jitsiURL;
        }

        public String getPort() {
            return port;
        }
    }
}
