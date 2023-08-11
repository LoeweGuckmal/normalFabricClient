package ch.loewe.normal_use_client.fabricclient.loewe;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class DataFromUrl {
    public static String getData(String urlString) {
        try {
            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();
            InputStream is = conn.getInputStream();
            String returnString = new String(is.readAllBytes());
            is.close();
            return returnString;
        } catch (IOException ignored) {return null;}
    }
}
