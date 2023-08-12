package ch.loewe.normal_use_client.fabricclient.loewe;

import ch.loewe.normal_use_client.fabricclient.modmenu.Config;
import ch.loewe.normal_use_client.fabricclient.openrgb.OpenRGB;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Objects;

import static ch.loewe.normal_use_client.fabricclient.cape.DownloadManager.isLocalPlayer;
import static ch.loewe.normal_use_client.fabricclient.client.FabricClientClient.*;

public class DamageRGB {
    public static int oldHealth = -1;
    public static boolean firstAfterJoined = true;
    public static String currentColor = "ffffff";

    //onServer
    public static void onTickOnServer(){
        if (mc.player != null) {
            PlayerEntity player = mc.player;
            if (isLocalPlayer(player) && ((int) Math.ceil(player.getHealth()) + (int) Math.ceil(player.getAbsorptionAmount())) != oldHealth) {
                if (((int) Math.ceil(player.getHealth()) + (int) Math.ceil(player.getAbsorptionAmount())) > oldHealth) {
                    if (firstAfterJoined) firstAfterJoined = false;
                    else damageRGB(true);
                }
                else {
                    if (firstAfterJoined) firstAfterJoined = false;
                    else damageRGB(false);
                }
                oldHealth = (int) Math.ceil(player.getHealth()) + (int) Math.ceil(player.getAbsorptionAmount());
            }
        }
    }
    public static void damageRGB(boolean heal){
        if (HealthTimeout <= 0 && Config.getDoRgb() && !isOnMonopoly())
            if (heal) {
                HealthTimeout = 25;
                HealthTimeoutBack = 5;
                currentColor = Objects.requireNonNull(DataFromUrl.getData("http://192.168.100.168:8881/sdk?mode=getCurrentColor&uuid=" + Config.getRgbUuid())).subSequence(2, 9).toString();
                OpenRGB.loadMode("gruen", false);
            } else {
                HealthTimeout = 30;
                HealthTimeoutBack = 16;
                currentColor = Objects.requireNonNull(DataFromUrl.getData("http://192.168.100.168:8881/sdk?mode=getCurrentColor&uuid=" + Config.getRgbUuid())).subSequence(2, 9).toString();
                OpenRGB.loadMode("rot", false);
            }
    }
}
