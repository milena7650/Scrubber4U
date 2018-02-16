package com;
/**
 * Created by milen_000 on 2/14/2018.
 */
import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;


public class Link implements Serializable {
    private String url;
    private boolean valid;

    public Link(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid() {
        if(doesURLExist())
            this.valid = true;
        else
            this.valid = false;
    }

    private boolean doesURLExist()
    {
        URL u = null;
        if (url.startsWith("/"))
            return false;
        int i;

        try {
            u = new URL(url);
        } catch (MalformedURLException e) {
            return false;
        }

        try {
            u.toURI();
        } catch (URISyntaxException e) {
            return false;
        }

        // We want to check the current URL
        //HttpURLConnection.setFollowRedirects(false);

        HttpURLConnection httpURLConnection = null;
        try {

            httpURLConnection = (HttpURLConnection) u.openConnection();
            // We don't need to get data
            httpURLConnection.setRequestMethod("HEAD");
            int responseCode = httpURLConnection.getResponseCode();

            // We only accept response code 200
            // return responseCode == HttpURLConnection.HTTP_OK;
            return (responseCode >= 200 && responseCode < 500);      // suppose response code in range 200-500 is valid
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Link link = (Link) o;
        return url.equals(link.url);
    }

    @Override
    public int hashCode() {
        int result = url.hashCode();
        result = 31 * result + (valid ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        String isValid = (valid) ? " is valid" : " is invalid";
        return "Link " + url + isValid;
    }
}
