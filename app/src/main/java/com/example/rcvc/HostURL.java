package com.example.rcvc;

import android.util.Patterns;
import java.net.MalformedURLException;

public class HostURL {

    public String url;

    public HostURL(String url) throws MalformedURLException {
        this.url = url;
        if (!Patterns.WEB_URL.matcher(url).matches()) {
            throw new MalformedURLException();
        }
    }
}
