package ch.loewe.normal_use_client.fabricclient.modmenu;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Locale;
import java.util.Properties;

import static ch.loewe.normal_use_client.fabricclient.client.FabricClientClient.logger;
import static ch.loewe.normal_use_client.fabricclient.modmenu.DefaultConfig.*;

public class Config {
    public static final Properties properties = new Properties();
    public static final Path path = FabricLoader.getInstance().getConfigDir().resolve("Loewe".toLowerCase() + ".properties");

    public Config() {

    }


    //get the value of buttons


    public static boolean getDoRgb() {
        return getBoolean(propertyKeys.doRgb(), DefaultConfig.getDoRgb());
    }
    public static String getStandardColor(){
        return getString(propertyKeys.standardColor(), DefaultConfig.getStandardColor());
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

    public static boolean getDebug() {
        return getBoolean(propertyKeys.debug(), DefaultConfig.getDebug());
    }
    /*public static int getTestSlider() {
        return getInt(propertyKeys.testSlider(), DefaultConfig.getTestSlider());
    }*/



    private static void logError(String key) {
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

        /*try {
            BufferedWriter comment = Files.newBufferedWriter(path, StandardOpenOption.APPEND, StandardOpenOption.WRITE, StandardOpenOption.CREATE);

            try {
                comment.write("\n# Definitions");
                comment.write("\n# " + writable(propertyKeys.tryLimit()) + " = how many times in a row should the same count of loaded chunks be ignored before we cancel pre-rendering.");
                comment.write("\n# Min = 1, Max = 1000. Set 1000 for infinity");
                comment.write("\n#");
                comment.write("\n# " + writable(propertyKeys.unsafeClose()) + " = should skip 'Joining World', and 'Downloading Terrain'. Potentially can result in joining world before chunks are properly loaded");
                comment.write("\n# Enabled = true, Disabled = false");
                comment.write("\n#");
                comment.write("\n# " + writable(propertyKeys.debug()) + " = debug (log) all things happening in fastload to aid in diagnosing issues.");
                comment.write("\n# Enabled = true, Disabled = false");
                comment.write("\n#");
                comment.write("\n# " + writable(propertyKeys.render()) + " = how many chunks are loaded until 'building terrain' is completed. Adjusts with FOV to decide how many chunks are visible");
                comment.write("\n# Min = 0, Max = 32 or your render distance, Whichever is smaller. Set 0 to disable.");
                comment.write("\n#");
                comment.write("\n# " + writable(propertyKeys.pregen()) + " = how many chunks (from 441 Loading) are pre-generated until the server starts");
                comment.write("\n# Min = 0, Max = 32. Set 0 to only pregen 1 chunk.");
            } catch (Throwable var6) {
                try {
                    comment.close();
                } catch (Throwable var3) {
                    var6.addSuppressed(var3);
                }

                throw var6;
            }

            comment.close();

        } catch (IOException var7) {
            throw new RuntimeException(var7);
        }*/
    }

    /*private static String writable(String key) {
        return "'" + key.toLowerCase() + "'";
    }*/

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
    } //form String to boolean (only used in getBoolean())

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
        getShowFps();
        getShowCords();
        getDebug();

        write();
    }
}
