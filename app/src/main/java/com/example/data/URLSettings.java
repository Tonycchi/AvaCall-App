package com.example.data;

public class URLSettings {

    public final static String HOSTURLKEY = "host_url",
            JITSIURLKEY = "jitsi_url",
            HOSTPORTKEY = "host_port",
            HTTPS = "https://",
            WSS = "wss://",
            DEFAULT_TEST_HOST = "test.avatar.mintclub.org",
            DEFAULT_TEST_JITSI = "meet.jit.si",
            DEFAULT_TEST_PORT = "22223";

    public LocalPreferenceDAO db;

    public URLSettings(LocalPreferenceDAO db) {
        this.db = db;
    }

    public void saveURLs(Triple urls) {
        db.insertAll(
                new LocalPreference(HOSTURLKEY, trimURL(urls.getHostURL())),
                new LocalPreference(JITSIURLKEY, trimURL(urls.getJitsiURL())),
                new LocalPreference(HOSTPORTKEY, trimURL(urls.getPort()))
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

    /**
     * trim url, ie remove protocols, last /, etc
     *
     * @param url url
     * @return trimmed url
     */
    private String trimURL(String url) {
        String r = url.replaceFirst("^(http[s]?://www\\.|http[s]?://|www\\.)", "");
        if (r.length() > 0 && r.charAt(r.length() - 1) == '/') {
            r = r.substring(0, r.length() - 1);
        }
        return r;
    }

    public static class Triple {
        private final String hostURL, jitsiURL, port;

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
