package ch.loewe.normal_use_client.fabricclient.client;

import ch.loewe.normal_use_client.fabricclient.cape.DownloadManager;
import ch.loewe.normal_use_client.fabricclient.cape.MessageLogger;
import ch.loewe.normal_use_client.fabricclient.mixin.MinecraftClientAccessor;
import ch.loewe.normal_use_client.fabricclient.modmenu.Config;
import ch.loewe.normal_use_client.fabricclient.modmenu.ConfigScreen;
import ch.loewe.normal_use_client.fabricclient.modmenu.DefaultConfig.propertyKeys;
import ch.loewe.normal_use_client.fabricclient.openrgb.OpenRGB;
import ch.loewe.normal_use_client.fabricclient.zoom.Zoom;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.HashMap;

import static ch.loewe.normal_use_client.fabricclient.modmenu.Config.getShowCords;
import static ch.loewe.normal_use_client.fabricclient.modmenu.Config.getShowFps;

@Environment(EnvType.CLIENT)
public class FabricClientClient implements ClientModInitializer {
    private static KeyBinding settingsKeyBinding;
    private static int oldHealth = 0;
    public static MinecraftClient mc = MinecraftClient.getInstance();
    private static int timeout = 9;
    private static int timeoutBack = 10;
    public static final DecimalFormat df = new DecimalFormat("#.00");
    public static Logger logger = LoggerFactory.getLogger("Loewe");
    //public static MessageLogger logger = new MessageLogger();
    public static HashMap<String, String> colorMap = new HashMap<>();

    @Override
    public void onInitializeClient() {
        logger.info("Loaded client");
        Zoom.onInitializeClient();
        //rgb
        colorMap.put("yellow", "gelb");
        colorMap.put("bluegreen", "bg");

        //overlay
        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            TextRenderer renderer = mc.textRenderer;
            if (!mc.options.debugEnabled && getShowFps()) {
                if (!getShowCords())
                    renderer.draw(matrixStack, ((MinecraftClientAccessor) mc).getCurrentFps() + " FPS", 2, 2, 0xffffff);
                else
                    renderer.draw(matrixStack, ((MinecraftClientAccessor) mc).getCurrentFps() + " FPS", 2, 12, 0xffffff);
            }
        }); //FPS
        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            TextRenderer renderer = mc.textRenderer;
            if (mc.player != null) {
                if (!mc.options.debugEnabled && getShowCords())
                    renderer.draw(matrixStack, "X: " + df.format(mc.player.getX()) +
                            ", Y: " + df.format(mc.player.getY()) + ", Z: " + df.format(mc.player.getZ()), 2, 2, 0xffffff);
            }
        }); //X, Y, Z
        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            TextRenderer renderer = mc.textRenderer;
            if (Zoom.isZooming())
                if (!mc.options.debugEnabled)
                    if (!getShowFps() || !getShowCords()){
                        if (!getShowFps() && !getShowCords())
                            renderer.draw(matrixStack, "Zoom: " + df.format(Zoom.zoom_X)  + "x", 2, 2, 0xffffff);
                        else
                            renderer.draw(matrixStack, "Zoom: " + df.format(Zoom.zoom_X)  + "x", 2, 12, 0xffffff);
                    } else
                        renderer.draw(matrixStack, "Zoom: " + df.format(Zoom.zoom_X)  + "x", 2, 22, 0xffffff);
        }); //Zoom
        settingsKeyBinding = new KeyBinding("loewe.key.settings", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_R, "loewe.category");
        KeyBindingHelper.registerKeyBinding(settingsKeyBinding);
    }

    public static void onTick(){
        if (settingsKeyBinding.isPressed()) {
            mc.setScreen(new ConfigScreen(mc.currentScreen));
        }
        damageRGB();
    }

    private static void damageRGB(){
        ClientPlayerEntity player = mc.player;
        int health = 0;
        if (player != null)
            health = (int) player.getHealth() + (int) player.getAbsorptionAmount();

        if (timeout == timeoutBack){
            timeout -= 1;
            OpenRGB.loadMode(colorMap.get(Config.getStandardColor()));
        }
        else if (timeout > 0)
            timeout -= 1;
        else if (player != null) {
            if (Config.getDoRgb()) {
                if (oldHealth > health) {
                    timeout = 40;
                    timeoutBack = 20;
                    OpenRGB.loadMode("rot");
                } else if (oldHealth < health) {
                    timeout = 30;
                    timeoutBack = 10;
                    OpenRGB.loadMode("gruen");
                }
            }
        }
        if (player != null){
            oldHealth = health;
        }
    }

    public static void doCustom(String key){
        if (key.equals(propertyKeys.standardColor())){
            OpenRGB.loadMode(colorMap.get(Config.getStandardColor()));
        }
        if (key.equals(propertyKeys.hasCapeGlint()) || key.equals(propertyKeys.capeFromFile()) || key.equals(propertyKeys.reloadCape())){
            logger.info("1");
            if (mc.player != null) {
                logger.info("2");
                DownloadManager.prepareDownload(mc.player, true);
            }
        }
    }
}
