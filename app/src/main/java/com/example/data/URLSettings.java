package com.example.data;

public class URLSettings {

    public final static String HOSTURLKEY = "host_url",
            VIDEOURLKEY = "jitsi_url",
            HOSTPORTKEY = "host_port",
            HTTPS = "https://",
            WSS = "wss://",
            DEFAULT_TEST_HOST = "avatar.mintclub.org",
            DEFAULT_TEST_JITSI = "meet.jit.si",
            DEFAULT_TEST_PORT = "22222";

    public final LocalPreferenceDAO localPreferenceDAO;

    public URLSettings(LocalPreferenceDAO localPreferenceDAO) {
        this.localPreferenceDAO = localPreferenceDAO;
    }

    public void saveURLs(stringTriple urls) {
        localPreferenceDAO.insertAll(
                new LocalPreference(HOSTURLKEY, trimURL(urls.getHostURL())),
                new LocalPreference(VIDEOURLKEY, trimURL(urls.getVideoURL())),
                new LocalPreference(HOSTPORTKEY, trimURL(urls.getPort()))
        );
    }

    public stringTriple getAll() {
        return new stringTriple(localPreferenceDAO.get(HOSTURLKEY), localPreferenceDAO.get(VIDEOURLKEY), localPreferenceDAO.get(HOSTPORTKEY));
    }

    public String getHost_plain() {
        return localPreferenceDAO.get(HOSTURLKEY);
    }

    public String getHost_https() {
        return HTTPS + localPreferenceDAO.get(HOSTURLKEY);
    }

    public String getVideoURL_plain() {
        return localPreferenceDAO.get(VIDEOURLKEY);
    }

    public String getVideoURL_https() {
        return HTTPS + localPreferenceDAO.get(VIDEOURLKEY);
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
        private final String hostURL, videoURL, port;

        public stringTriple(String hostURL, String videoURL, String port) {
            this.hostURL = hostURL;
            this.videoURL = videoURL;
            this.port = port;
        }

        public String getHostURL() {
            return hostURL;
        }

        public String getVideoURL() {
            return videoURL;
        }

        public String getPort() {
            return port;
        }
    }
}
