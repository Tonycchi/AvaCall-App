package com.example.rcvc;

import android.util.Patterns;
import java.net.MalformedURLException;

public class TrimmedURL {

    private String hostname;

    public TrimmedURL(String url) throws MalformedURLException {
        this.hostname = url;
        if (!Patterns.WEB_URL.matcher(url).matches()) {
            throw new MalformedURLException();
        }
        this.hostname = hostname.replaceFirst("^(http[s]?://www\\.|http[s]?://|www\\.)", "");
        this.hostname = hostname.replaceFirst("/.*", "");
    }

    public String getUrl(){
        return "https://"+hostname;
    }

    public String getHostname(){
        return hostname;
    }
}
