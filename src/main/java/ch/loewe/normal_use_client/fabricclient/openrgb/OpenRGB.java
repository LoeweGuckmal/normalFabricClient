package ch.loewe.normal_use_client.fabricclient.openrgb;

import ch.loewe.normal_use_client.fabricclient.client.FabricClientClient;
import net.minecraft.network.ClientConnection;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLConnection;

public class OpenRGB {
    public static boolean loadMode(String mode, boolean alsoOnMonopoly) {
        if (FabricClientClient.isOnMonopoly() && !alsoOnMonopoly) {
            FabricClientClient.logger.warn("You're on Monopoly, can't load colors!");
            return false;
        }
        String urlString = "http://192.168.100.168:8880/color?color=" + mode;
        try {
            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();
            InputStream is = conn.getInputStream();
            if (new String(is.readAllBytes()).contains("success")) {
                is.close();
                return true;
            }
            is.close();
        } catch (IOException ignored) {}
        return false;
    }
}
