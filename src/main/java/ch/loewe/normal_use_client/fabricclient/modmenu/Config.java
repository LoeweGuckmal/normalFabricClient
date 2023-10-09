package ch.loewe.normal_use_client.fabricclient.modmenu;

import ch.loewe.normal_use_client.fabricclient.loewe.WayPoints;
import ch.loewe.normal_use_client.fabricclient.modmenu.DefaultConfig.propertyKeys;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

import static ch.loewe.normal_use_client.fabricclient.client.FabricClientClient.logger;
import static ch.loewe.normal_use_client.fabricclient.modmenu.DefaultConfig.*;

public class Config {
    public static final Properties properties = new Properties();
    public static final Path path = FabricLoader.getInstance().getConfigDir().resolve("Loewe".toLowerCase() + ".properties");

    public Config() {

    }


    //get the value of buttons
    //loewe
    public static boolean getDoRgb() {
        return getBoolean(propertyKeys.doRgb(), DefaultConfig.getDoRgb());
    }
    public static String getStandardColor(){
        return getString(propertyKeys.standardColor(), DefaultConfig.getStandardColor());
    }
    public static String getRgbUuid() {
        try {
            String s = properties.getProperty("rgb_uuid");
            if (s != null) {
                properties.setProperty("rgb_uuid", s);
                return s;
            }
        } catch (NumberFormatException ignored){}
        logError("rgb_uuid");
        properties.setProperty("rgb_uuid", UUID.randomUUID().toString());
        return properties.getProperty("rgb_uuid");
    }

    public static boolean getShowFps(){
        return getBoolean(propertyKeys.showFps(), DefaultConfig.getShowFps());
    }
    public static boolean getShowCords(){
        return getBoolean(propertyKeys.showCords(), DefaultConfig.getShowCords());
    }

    public static int getStandardZoom() {
        return getInt(propertyKeys.standardZoom(), DefaultConfig.getStandardZoom());
    }

    public static boolean getHasCapeGlint() {
        return getBoolean(propertyKeys.hasCapeGlint(), DefaultConfig.getHasCapeGlint());
    }
    public static boolean getCapeFromFile() {
        return getBoolean(propertyKeys.capeFromFile(), DefaultConfig.getCapeFromFile());
    }
    public static String getReloadCape() {
        return getString(propertyKeys.reloadCape(), DefaultConfig.getReloadCape());
    }

    public static boolean getSkipFrontView() {
        return getBoolean(propertyKeys.skipFrontView(), DefaultConfig.getSkipFrontView());
    }

    public static String getOpenAccountSwitcher() {
        return getString(propertyKeys.openAccountSwitcher(), DefaultConfig.getOpenAccountSwitcher());
    }

    public static String getRequestServerAccess() {
        return getString(propertyKeys.requestServerAccess(), DefaultConfig.getRequestServerAccess());
    }

    public static boolean getDebug() {
        return getBoolean(propertyKeys.debug(), DefaultConfig.getDebug());
    }

    //monopoly
    public static String getStartGame() {
        return getString(propertyKeys.startGame(), DefaultConfig.getStartGame());
    }



    public static void logError(String key) {
        logger.warn("Failed to parse variable '" + key + "' in Loewe's config, generating a new one!");
    }

    public static void write() {
        try {
            OutputStream out = Files.newOutputStream(path, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE, StandardOpenOption.CREATE);

            try {
                properties.store(out, "Loewe Configuration File");
            } catch (Throwable ignored) {}

            if (out != null) {
                out.close();
            }
        } catch (IOException ignored) {}
    }

    private static int getInt(String key, MinMaxDefHolder holder) {
        try {
            int i = Math.max(Math.min(Integer.parseInt(properties.getProperty(key)), holder.max()), holder.min());
            properties.setProperty(key, String.valueOf(i));
            return i;
        } catch (NumberFormatException e) {
            logError(key);
            properties.setProperty(key, String.valueOf(holder.def()));
            return holder.def();
        }
    }

    private static <E extends Enum<E>> String getString(String key, E def){
        try {
            String s = properties.getProperty(key);
            if (s != null) {
                properties.setProperty(key, s);
                return s;
            }
        } catch (NumberFormatException ignored){}
        logError(key);
        properties.setProperty(key, def.name().toLowerCase(Locale.ROOT));
        return def.name().toLowerCase(Locale.ROOT);
    }

    private static boolean getBoolean(String key, boolean def) {
        try {
            boolean b = parseBoolean(properties.getProperty(key));
            properties.setProperty(key, String.valueOf(b));
            return b;
        } catch (NumberFormatException e) {
            logError(key);
            properties.setProperty(key, String.valueOf(def));
            return def;
        }
    }

    public static boolean parseBoolean(String string) {
        if (string == null) {
            throw new NumberFormatException("null");
        } else if (string.trim().equalsIgnoreCase("true")) {
            return true;
        } else if (string.trim().equalsIgnoreCase("false")) {
            return false;
        } else {
            throw new NumberFormatException(string);
        }
    } //form String to boolean

    public static void storeProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    static {
        if (Files.isRegularFile(path)) {
            try {
                InputStream in = Files.newInputStream(path, StandardOpenOption.CREATE);

                try {
                    properties.load(in);
                } catch (IOException ignored) {}

                in.close();
            } catch (IOException ignored) {}
        }

        getDoRgb();
        getStandardColor();
        getRgbUuid();
        getShowFps();
        getShowCords();
        getStandardZoom();
        getHasCapeGlint();
        getCapeFromFile();
        getSkipFrontView();
        getDebug();
        WayPoints.getWayPoints();
        WayPoints.toggle();

        write();
    }
}
