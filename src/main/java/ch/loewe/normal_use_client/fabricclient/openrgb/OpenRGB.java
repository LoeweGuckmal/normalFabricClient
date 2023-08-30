package ch.loewe.normal_use_client.fabricclient.openrgb;

import ch.loewe.normal_use_client.fabricclient.client.FabricClientClient;
import ch.loewe.normal_use_client.fabricclient.loewe.DamageRGB;
import ch.loewe.normal_use_client.fabricclient.modmenu.Config;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import static ch.loewe.normal_use_client.fabricclient.client.FabricClientClient.logger;

public class OpenRGB {
    public static void loadMode(String mode, boolean alsoOnMonopoly) {
        new Thread(() -> {
            if (FabricClientClient.isOnMonopoly() && !alsoOnMonopoly) {
                logger.warn("You're on Monopoly, can't load colors!");
                return;
            }

            String urlString;
            if (mode.equals("current"))
                urlString = "http://192.168.100.168:8881/sdk?mode=direct&color=" + hexToRgb(DamageRGB.currentColor) + "&uuid=" + Config.getRgbUuid();
            else urlString = "http://192.168.100.168:8880/color?color=" + mode + "&uuid=" + Config.getRgbUuid();
            try {
                URL url = new URL(urlString);
                URLConnection conn = url.openConnection();
                InputStream is = conn.getInputStream();
                if (new String(is.readAllBytes()).contains("#")) {
                    is.close();
                    return;
                }
                is.close();
            } catch (IOException ignored) {}
            logger.warn("Could not load color " + FabricClientClient.colorMap.get(Config.getStandardColor()));
        }).start();
    }

    public static String hexToRgb(String hex) {
        if (hex.contains("-"))
            return "255;255;255";
        hex = hex.replace("#", "");
        int r = Integer.parseInt(hex.substring(0, 2), 16);
        int g = Integer.parseInt(hex.substring(2, 4), 16);
        int b = Integer.parseInt(hex.substring(4, 6), 16);
        return r + ";" + g + ";" + b;
    }
}
