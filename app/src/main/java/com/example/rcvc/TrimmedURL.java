package com.example.rcvc;

import android.util.Patterns;
import java.net.MalformedURLException;

public class TrimmedURL {

    public String url;

    public TrimmedURL(String url) throws MalformedURLException {
        this.url = url;
        if (!Patterns.WEB_URL.matcher(url).matches()) {
            throw new MalformedURLException();
        }
        this.url = this.url.replaceFirst("^(http[s]?://www\\.|http[s]?://|www\\.)", "");
        this.url = this.url.replaceFirst("/.*", "");
        this.url = "https://" + this.url;
    }
}