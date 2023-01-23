package ch.loewe.normal_use_client.fabricclient.modmenu;

public class DefaultConfig {
    public DefaultConfig() {
    }

    protected static MinMaxHolder getRawRadiusBound() {
        return new MinMaxHolder(32, 0);
    }

    protected static MinMaxHolder getTryLimitBound() {
        return new MinMaxHolder(1000, 1);
    }

    protected static int getRenderRadius() {
        return 0;
    }

    protected static int getPregenRadius() {
        return 5;
    }

    protected static int getTryLimit() {
        return 100;
    }

    protected static boolean getCloseUnsafely() {
        return false;
    }

    protected static boolean getDebug() {
        return false;
    }

    public static class propertyKeys {
        public propertyKeys() {
        }

        public static String pregen() {
            return pregen(true) + "_radius";
        }

        public static String pregen(boolean raw) {
            return raw ? "pregen_chunk" : pregen();
        }

        public static String render() {
            return render(true) + "_radius";
        }

        public static String render(boolean raw) {
            return raw ? "pre_render" : render();
        }

        public static String tryLimit() {
            return "chunk_try_limit";
        }

        public static String unsafeClose() {
            return "close_loading_screen_unsafely";
        }

        public static String debug() {
            return "debug";
        }
    }
}
