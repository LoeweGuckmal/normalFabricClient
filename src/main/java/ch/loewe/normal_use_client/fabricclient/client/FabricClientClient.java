package ch.loewe.normal_use_client.fabricclient.client;

import ch.loewe.normal_use_client.fabricclient.account.ias.IAS;
import ch.loewe.normal_use_client.fabricclient.account.ias.gui.AccountListScreen;
import ch.loewe.normal_use_client.fabricclient.cape.DownloadManager;
import ch.loewe.normal_use_client.fabricclient.commands.WayPointCommand;
import ch.loewe.normal_use_client.fabricclient.loewe.DamageRGB;
import ch.loewe.normal_use_client.fabricclient.loewe.DataFromUrl;
import ch.loewe.normal_use_client.fabricclient.loewe.HandleServerMessage;
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
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Objects;

import static ch.loewe.normal_use_client.fabricclient.loewe.WayPoints.indexMap;
import static ch.loewe.normal_use_client.fabricclient.loewe.WayPoints.wayPointsMap;
import static ch.loewe.normal_use_client.fabricclient.modmenu.Config.getShowCords;
import static ch.loewe.normal_use_client.fabricclient.modmenu.Config.getShowFps;
import static ch.loewe.normal_use_client.fabricclient.modmenu.MonopolyScreen.exeAfterClose;
import static ch.loewe.normal_use_client.fabricclient.openrgb.OpenRGB.getIp;

@Environment(EnvType.CLIENT)
public class FabricClientClient implements ClientModInitializer {
    public static boolean initializeDone = false;
    public static KeyBinding settingsKeyBinding;
    public static KeyBinding waypointKeyBinding;
    public static int HealthTimeout = 9;
    public static int HealthTimeoutBack = 10;
    public static MinecraftClient mc = MinecraftClient.getInstance();
    public static final DecimalFormat df = new DecimalFormat("0.00");
    public static Boolean wpToggled = null;
    public static Logger logger = LoggerFactory.getLogger("Loewe");
    public static HashMap<String, String> colorMap = new HashMap<>();
    public static boolean isConnectedToServer = false;
    public static ServerAddress lastAddress = new ServerAddress("-", 25565);
    public static boolean isOnMonopoly(){return lastAddress.getAddress().equals("loewe-monopoly.feathermc.gg");}
    public static boolean isOpOnMonopoly = false;
    public static boolean stopDisconnect = false;

    @Override
    public void onInitializeClient() {
        logger.info("Loaded client");
        Zoom.onInitializeClient();
        IAS.onInitializeClient();
        //rgb
        colorMap.put("yellow", "gelb");
        colorMap.put("bluegreen", "bg");
        colorMap.put("current", "current");
        PayloadTypeRegistry.playS2C().register(HandleServerMessage.StringPayload.ID, HandleServerMessage.StringPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(HandleServerMessage.StringPayload.ID, HandleServerMessage.StringPayload.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(HandleServerMessage.StringPayload.ID, HandleServerMessage::onReceiveMessage);
        //TODO: ClientPlayNetworking.registerGlobalReceiver(new CustomPayload.Id<>(Identifier.of("monopoly", "loewe")), HandleServerMessage::onReceiveMessage);
        //ClientPlayNetworking.registerGlobalReceiver(new CustomPayload.Id<>(Identifier.of("monopoly", "loewe")), (client, handler, buf, responseSender) ->
                //HandleServerMessage.onReceiveMessage(client, handler, new String(removeZerosFromEnd( buf.array())), responseSender));

        //Bindings
        settingsKeyBinding = new KeyBinding("loewe.key.settings", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_R, "loewe.category");
        KeyBindingHelper.registerKeyBinding(settingsKeyBinding);
        waypointKeyBinding = new KeyBinding("loewe.key.waypoints", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_I, "loewe.category");
        KeyBindingHelper.registerKeyBinding(waypointKeyBinding);

        //overlay
        ClientCommandRegistrationCallback.EVENT.register(WayPointCommand::register);
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            if (mc.currentScreen == null && !Objects.equals(exeAfterClose, "")) {
                HandleServerMessage.sendMessage(exeAfterClose);
                exeAfterClose = "";
            }
        }); //Screen executer
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            TextRenderer renderer = mc.textRenderer;
            if (!mc.getDebugHud().shouldShowDebugHud() && getShowFps()) {
                if (!getShowCords())
                    drawContext.drawText(renderer, MinecraftAccessor.getCurrentFps() + " FPS", 2, 2, 0xffffff, false);
                else
                    drawContext.drawText(renderer, MinecraftAccessor.getCurrentFps() + " FPS", 2, 12, 0xffffff, false);
            }
        }); //FPS
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            TextRenderer renderer = mc.textRenderer;
            if (mc.player != null) {
                if (!mc.getDebugHud().shouldShowDebugHud() && getShowCords())
                    drawContext.drawText(renderer, "X: " + df.format(mc.player.getX()) +
                            ", Y: " + df.format(mc.player.getY()) + ", Z: " + df.format(mc.player.getZ()), 2, 2, 0xffffff, false);
            }
        }); //X, Y, Z
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            TextRenderer renderer = mc.textRenderer;
            if (Zoom.isZooming())
                if (!mc.getDebugHud().shouldShowDebugHud())
                    if (!getShowFps() || !getShowCords()){
                        if (!getShowFps() && !getShowCords())
                            drawContext.drawText(renderer, "Zoom: " + df.format(Zoom.zoom_X)  + "x", 2, 2, 0xffffff, false);
                        else
                            drawContext.drawText(renderer, "Zoom: " + df.format(Zoom.zoom_X)  + "x", 2, 12, 0xffffff, false);
                    } else
                        drawContext.drawText(renderer, "Zoom: " + df.format(Zoom.zoom_X)  + "x", 2, 22, 0xffffff, false);
        }); //ZOOM
        HudRenderCallback.EVENT.register(((drawContext, tickDelta) -> {
            TextRenderer renderer = mc.textRenderer;
            if (!wayPointsMap.isEmpty())
                if (!mc.getDebugHud().shouldShowDebugHud())
                    if (waypointKeyBinding.isPressed() || wpToggled)
                        wayPointsMap.forEach((name, cords) -> drawContext.drawText(renderer, name + ": " + df.format(cords[0]) + ", " +
                               df.format(cords[1]) + ", " + df.format(cords[2]), 2, indexMap.get(name) * 10 + 5, 0xffffff, false));
        })); //WAYPOINTS
        initializeDone = true;
    }

    public static void onTick(){
        if (initializeDone && Objects.nonNull(settingsKeyBinding)) {
            if (isConnectedToServer)
                DamageRGB.onTickOnServer();
            if (settingsKeyBinding.isPressed() && ConfigScreen.openTimeout == 0)
                mc.setScreen(new ConfigScreen(mc.currentScreen));
            if (ConfigScreen.openTimeout > 0)
                ConfigScreen.openTimeout -= 1;
            if (HealthTimeout == HealthTimeoutBack)
                OpenRGB.loadMode(colorMap.get(Config.getStandardColor()), false);
            if (HealthTimeout > 0)
                HealthTimeout -= 1;
        }
    }

    public static void doCustom(String key){
        if (key.equals(propertyKeys.standardColor())){
            new Thread(() -> {
                String c = "";
                try {
                    c = DataFromUrl.getData("http://" + getIp() + ":8881/sdk?mode=getCurrentColor&uuid=" + Config.getRgbUuid()).subSequence(2, 9).toString();
                } catch (Exception ignored) {}
                if (c.contains("#") && !c.equals("#ffff00") && !c.equals("#00ffff"))
                    DamageRGB.currentColor = c;
                OpenRGB.loadMode(colorMap.get(Config.getStandardColor()), false);
            }).start();
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
            //HandleServerMessage.sendMessage("start_game");
            MonopolyScreen.exeAfterClose = "start_game";
        }
        else if (key.equals(propertyKeys.stopGame())) {
            //HandleServerMessage.sendMessage("stop_game");
            MonopolyScreen.exeAfterClose = "stop_game";
        }
    }
}
