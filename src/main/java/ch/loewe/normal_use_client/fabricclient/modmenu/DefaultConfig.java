package ch.loewe.normal_use_client.fabricclient.modmenu;

import ch.loewe.normal_use_client.fabricclient.modmenu.ModMenuButtons.*;

public class DefaultConfig {
    public DefaultConfig() {
    }

    //loewe
    //rgb
    public static boolean getDoRgb() {
        return true;
    }
    public static StandardColor getStandardColor(){
        return StandardColor.CURRENT;
    }
    //overlay
    public static boolean getShowFps(){
        return true;
    }
    public static boolean getShowCords(){
        return true;
    }
    //zoom
    public static MinMaxDefHolder getStandardZoom() {
        return new MinMaxDefHolder(1, 6, 3);
    }
    //cape
    public static boolean getHasCapeGlint() {
        return true;
    }
    public static boolean getCapeFromFile() {
        return false;
    }
    public static ReloadCape getReloadCape() {
        return ReloadCape.RELOAD;
    }
    //f5
    public static boolean getSkipFrontView() {
        return true;
    }
    //accountSwitcher
    public static OpenAccountSwitcher getOpenAccountSwitcher() {
        return OpenAccountSwitcher.ACCOUNT;
    }
    //server
    public static RequestServerAccess getRequestServerAccess() {
        return RequestServerAccess.ACCESS;
    }
    //debug
    public static boolean getDebug() {
        return false;
    }

    //monopoly
    public static StartGame getStartGame() {
        return StartGame.START;
    }

    public static final class propertyKeys {
        public propertyKeys() {}

        //loewe settings
        public static String doRgb(){
            return "do_rgb_on_damage";
        }
        public static String standardColor(){
            return "standard_color";
        }

        public static String showFps(){
            return "show_fps";
        }
        public static String showCords(){
            return "show_cords";
        }

        public static String standardZoom() {
            return "standard_zoom";
        }

        public static String hasCapeGlint() {
            return "has_cape_glint";
        }
        public static String capeFromFile() {
            return "cape_from_file";
        }
        public static String reloadCape() {
            return "cape_reload";
        }

        public static String skipFrontView() {
            return "skip_front_view";
        }

        public static String openAccountSwitcher() {
            return "open_account_switcher";
        }

        public static String requestServerAccess() {
            return "request_server_access";
        }

        public static String debug() {
            return "debug";
        }

        //monopolyscreen
        public static String startGame() {
            return "start_game";
        }
        public static String stopGame() {
            return "stop_game";
        }
    }
}
