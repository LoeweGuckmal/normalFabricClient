package ch.loewe.normal_use_client.fabricclient.modmenu;

import ch.loewe.normal_use_client.fabricclient.modmenu.ModMenuButtons.*;

public class DefaultConfig {
    public DefaultConfig() {
    }

    //rgb
    public static boolean getDoRgb() {
        return true;
    }
    public static StandardColor getStandardColor(){
        return StandardColor.YELLOW;
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

    //debug
    public static boolean getDebug() {
        return false;
    }

    /*public static MinMaxDefHolder getTestSlider() {
        return new MinMaxDefHolder(2, 12, 5);
    }*/


    public static class propertyKeys {
        public propertyKeys() {
        }

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

        public static String debug() {
            return "debug";
        }
    }
}
