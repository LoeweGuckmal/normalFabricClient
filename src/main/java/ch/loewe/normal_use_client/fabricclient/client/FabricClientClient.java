package ch.loewe.normal_use_client.fabricclient.client;

import ch.loewe.normal_use_client.fabricclient.account.ias.IAS;
import ch.loewe.normal_use_client.fabricclient.account.ias.gui.AccountListScreen;
import ch.loewe.normal_use_client.fabricclient.cape.DownloadManager;
import ch.loewe.normal_use_client.fabricclient.commands.RgbCommand;
import ch.loewe.normal_use_client.fabricclient.loewe.DamageRGB;
import ch.loewe.normal_use_client.fabricclient.loewe.HandleServerMessage;
import ch.loewe.normal_use_client.fabricclient.mixin.HealthMixin;
import ch.loewe.normal_use_client.fabricclient.mixin.MinecraftAccessor;
import ch.loewe.normal_use_client.fabricclient.modmenu.Config;
import ch.loewe.normal_use_client.fabricclient.modmenu.ConfigScreen;
import ch.loewe.normal_use_client.fabricclient.modmenu.DefaultConfig.propertyKeys;
import ch.loewe.normal_use_client.fabricclient.modmenu.MonopolyScreen;
import ch.loewe.normal_use_client.fabricclient.openrgb.OpenRGB;
import ch.loewe.normal_use_client.fabricclient.zoom.Zoom;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.HashMap;

import static ch.loewe.normal_use_client.fabricclient.modmenu.Config.getShowCords;
import static ch.loewe.normal_use_client.fabricclient.modmenu.Config.getShowFps;

@Environment(EnvType.CLIENT)
public class FabricClientClient implements ClientModInitializer {
    public static KeyBinding settingsKeyBinding;
    public static KeyBinding infoKeyBinding;
    public static int HealthTimeout = 9;
    public static int HealthTimeoutBack = 10;
    public static MinecraftClient mc = MinecraftClient.getInstance();
    public static final DecimalFormat df = new DecimalFormat("#.00");
    public static Logger logger = LoggerFactory.getLogger("Loewe");
    public static HashMap<String, String> colorMap = new HashMap<>();
    public static boolean isConnectedToServer = false;
    public static ServerAddress lastAddress = new ServerAddress("-", 25565);
    public static boolean isOnMonopoly(){return lastAddress.getAddress().equals("loewe-monopoly.feathermc.gg");}

    @Override
    public void onInitializeClient() {
        logger.info("Loaded client");
        Zoom.onInitializeClient();
        IAS.onInitializeClient();
        //rgb
        colorMap.put("yellow", "gelb");
        colorMap.put("bluegreen", "bg");
        ClientPlayNetworking.registerGlobalReceiver(new Identifier("monopoly", "loewe"), (client, handler, buf, responseSender) -> {
            String message = new String(buf.getWrittenBytes());
            if (message.length() > 0 && !Character.isLetter(message.charAt(0)))
                message = message.substring(1);
            HandleServerMessage.onReceiveMessage(client, handler, message, responseSender);
        });

        //overlay
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            TextRenderer renderer = mc.textRenderer;
            if (!mc.options.debugEnabled && getShowFps()) {
                if (!getShowCords())
                    drawContext.drawText(renderer, ((MinecraftAccessor) mc).getCurrentFps() + " FPS", 2, 2, 0xffffff, false);
                else
                    drawContext.drawText(renderer, ((MinecraftAccessor) mc).getCurrentFps() + " FPS", 2, 12, 0xffffff, false);
            }
        }); //FPS
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            TextRenderer renderer = mc.textRenderer;
            if (mc.player != null) {
                if (!mc.options.debugEnabled && getShowCords())
                    drawContext.drawText(renderer, "X: " + df.format(mc.player.getX()) +
                            ", Y: " + df.format(mc.player.getY()) + ", Z: " + df.format(mc.player.getZ()), 2, 2, 0xffffff, false);
            }
        }); //X, Y, Z
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            TextRenderer renderer = mc.textRenderer;
            if (Zoom.isZooming())
                if (!mc.options.debugEnabled)
                    if (!getShowFps() || !getShowCords()){
                        if (!getShowFps() && !getShowCords())
                            drawContext.drawText(renderer, "Zoom: " + df.format(Zoom.zoom_X)  + "x", 2, 2, 0xffffff, false);
                        else
                            drawContext.drawText(renderer, "Zoom: " + df.format(Zoom.zoom_X)  + "x", 2, 12, 0xffffff, false);
                    } else
                        drawContext.drawText(renderer, "Zoom: " + df.format(Zoom.zoom_X)  + "x", 2, 22, 0xffffff, false);
        });
        //Zoom
        settingsKeyBinding = new KeyBinding("loewe.key.settings", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_R, "loewe.category");
        KeyBindingHelper.registerKeyBinding(settingsKeyBinding);
        infoKeyBinding = new KeyBinding("loewe.key.info", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_I, "loewe.category");
        KeyBindingHelper.registerKeyBinding(infoKeyBinding);
    }

    public static void onTick(){
        if (isConnectedToServer)
            DamageRGB.onTickOnServer();
        if (settingsKeyBinding.isPressed() && ConfigScreen.openTimeout == 0) {
            mc.setScreen(new ConfigScreen(mc.currentScreen));
        }
        if (ConfigScreen.openTimeout > 0)
            ConfigScreen.openTimeout -= 1;
        if (HealthTimeout == HealthTimeoutBack)
            OpenRGB.loadMode(colorMap.get(Config.getStandardColor()), false);
        if (HealthTimeout > 0)
            HealthTimeout -= 1;
    }

    public static void doCustom(String key){
        if (key.equals(propertyKeys.standardColor())){
            if (!OpenRGB.loadMode(colorMap.get(Config.getStandardColor()), false))
                logger.warn("Could not load color " + colorMap.get(Config.getStandardColor()));
        }
        else if (key.equals(propertyKeys.hasCapeGlint()) || key.equals(propertyKeys.capeFromFile()) || key.equals(propertyKeys.reloadCape())){
            if (mc.player != null) {
                DownloadManager.prepareDownload(mc.player, true , false);
            }
        }
        else if (key.equals(propertyKeys.openAccountSwitcher())){
            mc.setScreen(new AccountListScreen(mc.currentScreen));
        }
        else if (key.equals(propertyKeys.requestServerAccess())){
            mc.setScreen(new MonopolyScreen(mc.currentScreen));
        }
        else if (key.equals(propertyKeys.startGame())) {
            HandleServerMessage.sendMessage("start_game");
        }
    }
}
