package ch.loewe.normal_use_client.fabricclient.openrgb;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class OpenRGB {
    public static void loadMode(String mode) {
        String urlString = "http://127.0.0.1:6742/" + mode;
        try {
            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();
            InputStream is = conn.getInputStream();
            is.close();
        } catch (IOException ignored) {}
    }
}
