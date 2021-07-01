package com.example.data;

public class URLSettings {

    public final static String HOSTURLKEY = "host_url",
            JITSIURLKEY = "jitsi_url",
            HOSTPORTKEY = "host_port",
            HTTPS = "https://",
            WSS = "wss://",
            DEFAULT_TEST_HOST = "test.avatar.mintclub.org", //TODO: make non test entries
            DEFAULT_TEST_JITSI = "meet.jit.si",
            DEFAULT_TEST_PORT = "22223";

    public LocalPreferenceDAO localPreferenceDAO;

    public URLSettings(LocalPreferenceDAO localPreferenceDAO) {
        this.localPreferenceDAO = localPreferenceDAO;
    }

    public void saveURLs(stringTriple urls) {
        localPreferenceDAO.insertAll(
                new LocalPreference(HOSTURLKEY, trimURL(urls.getHostURL())),
                new LocalPreference(JITSIURLKEY, trimURL(urls.getJitsiURL())),
                new LocalPreference(HOSTPORTKEY, trimURL(urls.getPort()))
        );
    }

    public stringTriple getAll() {
        return new stringTriple(localPreferenceDAO.get(HOSTURLKEY), localPreferenceDAO.get(JITSIURLKEY), localPreferenceDAO.get(HOSTPORTKEY));
    }

    public String getHost_plain() {
        return localPreferenceDAO.get(HOSTURLKEY);
    }

    public String getHost_https() {
        return HTTPS + localPreferenceDAO.get(HOSTURLKEY);
    }

    public String getJitsi_plain() {
        return localPreferenceDAO.get(JITSIURLKEY);
    }

    public String getJitsi_https() {
        return HTTPS + localPreferenceDAO.get(JITSIURLKEY);
    }

    public String getPort() {
        return localPreferenceDAO.get(HOSTPORTKEY);
    }

    public String getHost_wss() {
        return WSS + localPreferenceDAO.get(HOSTURLKEY) + ":" + localPreferenceDAO.get(HOSTPORTKEY);
    }

    /**
     * trim url, ie remove protocols, last /, etc
     *
     * @param url url
     * @return trimmed url
     */
    private String trimURL(String url) {
        String trimmedURL = url.replaceFirst("^(http[s]?://www\\.|http[s]?://|www\\.)", "");
        if (trimmedURL.length() > 0 && trimmedURL.charAt(trimmedURL.length() - 1) == '/') {
            trimmedURL = trimmedURL.substring(0, trimmedURL.length() - 1);
        }
        return trimmedURL;
    }

    public static class stringTriple {
        private final String hostURL, jitsiURL, port;

        public stringTriple(String hostURL, String jitsiURL, String port) {
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
