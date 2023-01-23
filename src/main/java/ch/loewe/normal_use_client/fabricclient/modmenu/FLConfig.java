package ch.loewe.normal_use_client.fabricclient.modmenu;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Properties;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.LoggerFactory;

import static ch.loewe.normal_use_client.fabricclient.modmenu.DefaultConfig.propertyKeys;

public class FLConfig {
    private static final Properties properties = new Properties();
    private static final Path path = FabricLoader.getInstance().getConfigDir().resolve("Fastload".toLowerCase() + ".properties");

    public FLConfig() {
    }

    public static void init() {
    }

    protected static int getChunkTryLimit() {
        return getInt(propertyKeys.tryLimit(), DefaultConfig.getTryLimit(), DefaultConfig.getTryLimitBound());
    }

    protected static int getRawChunkPregenRadius() {
        return getInt(propertyKeys.pregen(), DefaultConfig.getPregenRadius(), DefaultConfig.getRawRadiusBound());
    }

    protected static int getRawPreRenderRadius() {
        return getInt(propertyKeys.render(), DefaultConfig.getRenderRadius(), DefaultConfig.getRawRadiusBound());
    }

    protected static boolean getCloseLoadingScreenUnsafely() {
        return getBoolean(propertyKeys.unsafeClose(), DefaultConfig.getCloseUnsafely());
    }

    protected static boolean getRawDebug() {
        return getBoolean(propertyKeys.debug(), DefaultConfig.getDebug());
    }

    private static void logError(String key) {
        LoggerFactory.getLogger("Fastload").error("Failed to parse variable '" + key + "' in Fastload's config, generating a new one!");
    }

    private static void write() {
        try {
            OutputStream out = Files.newOutputStream(path, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE, StandardOpenOption.CREATE);

            try {
                properties.store(out, "Fastload Configuration File");
            } catch (Throwable var5) {
                if (out != null) {
                    try {
                        out.close();
                    } catch (Throwable var4) {
                        var5.addSuppressed(var4);
                    }
                }

                throw var5;
            }

            if (out != null) {
                out.close();
            }
        } catch (IOException var8) {
            throw new RuntimeException(var8);
        }

        try {
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
                if (comment != null) {
                    try {
                        comment.close();
                    } catch (Throwable var3) {
                        var6.addSuppressed(var3);
                    }
                }

                throw var6;
            }

            if (comment != null) {
                comment.close();
            }

        } catch (IOException var7) {
            throw new RuntimeException(var7);
        }
    }

    private static String writable(String key) {
        return "'" + key.toLowerCase() + "'";
    }

    private static int getInt(String key, int def, MinMaxHolder holder) {
        try {
            int i = FLMath.parseMinMax(Integer.parseInt(properties.getProperty(key)), holder);
            properties.setProperty(key, String.valueOf(i));
            return i;
        } catch (NumberFormatException var4) {
            logError(key);
            properties.setProperty(key, String.valueOf(def));
            return def;
        }
    }

    private static boolean parseBoolean(String string) {
        if (string == null) {
            throw new NumberFormatException("null");
        } else if (string.trim().equalsIgnoreCase("true")) {
            return true;
        } else if (string.trim().equalsIgnoreCase("false")) {
            return false;
        } else {
            throw new NumberFormatException(string);
        }
    }

    private static boolean getBoolean(String key, boolean def) {
        try {
            boolean b = parseBoolean(properties.getProperty(key));
            properties.setProperty(key, String.valueOf(b));
            return b;
        } catch (NumberFormatException var3) {
            logError(key);
            properties.setProperty(key, String.valueOf(def));
            return def;
        }
    }

    public static void storeProperty(String key, String value) {
        properties.setProperty(key, value);
        System.out.println(key + ":" + value);
    }

    public static void writeToDisk() {
        write();
    }

    static {
        if (Files.isRegularFile(path, new LinkOption[0])) {
            try {
                InputStream in = Files.newInputStream(path, StandardOpenOption.CREATE);

                try {
                    properties.load(in);
                } catch (Throwable var4) {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (Throwable var3) {
                            var4.addSuppressed(var3);
                        }
                    }

                    throw var4;
                }

                if (in != null) {
                    in.close();
                }
            } catch (IOException var5) {
                throw new RuntimeException(var5);
            }
        }

        getChunkTryLimit();
        getRawChunkPregenRadius();
        getRawPreRenderRadius();
        getCloseLoadingScreenUnsafely();
        getRawDebug();
        write();
    }
}
