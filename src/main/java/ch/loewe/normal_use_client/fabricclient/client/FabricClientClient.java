package ch.loewe.normal_use_client.fabricclient.client;

import ch.loewe.normal_use_client.fabricclient.commands.RgbCommand;
import ch.loewe.normal_use_client.fabricclient.mixin.MinecraftClientAccessor;
import ch.loewe.normal_use_client.fabricclient.openrgb.OpenRGB;
import ch.loewe.normal_use_client.fabricclient.zoom.Zoom;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;

import java.text.DecimalFormat;

@Environment(EnvType.CLIENT)
public class FabricClientClient implements ClientModInitializer {
    private static int oldHealth = 0;
    public static MinecraftClient mc = MinecraftClient.getInstance();
    private static int timeout = 9;
    private static int timeoutBack = 10;
    public static String mode = "gelb";
    public static final DecimalFormat df = new DecimalFormat("#.00");

    @Override
    public void onInitializeClient() {
        System.out.println("Loaded client");
        ClientCommandRegistrationCallback.EVENT.register(RgbCommand::register);
        Zoom.onInitializeClient();
        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            TextRenderer renderer = mc.textRenderer;
            if (!mc.options.debugEnabled)
                renderer.draw(matrixStack, ((MinecraftClientAccessor) mc).getCurrentFps() + " FPS", 2, 12, 0xffffff);
        }); //FPS
        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            TextRenderer renderer = mc.textRenderer;
            if (mc.player != null) {
                if (!mc.options.debugEnabled)
                    renderer.draw(matrixStack, "X: " + df.format(mc.player.getX()) +
                            ", Y: " + df.format(mc.player.getY()) + ", Z: " + df.format(mc.player.getZ()), 2, 2, 0xffffff);
            }
        }); //X, Y, Z
        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            TextRenderer renderer = mc.textRenderer;
            if (Zoom.isZooming())
                if (!mc.options.debugEnabled)
                    renderer.draw(matrixStack, "Zoom: " + df.format(Zoom.zoom_X)  + "x", 2, 22, 0xffffff);
        }); //Zoom
    }

    public static void onTick(){
        damageRGB();
    }

    private static void damageRGB(){
        ClientPlayerEntity player = mc.player;
        int health = 0;
        if (player != null)
            health = (int) player.getHealth() + (int) player.getAbsorptionAmount();

        if (timeout == timeoutBack){
            timeout -= 1;
            OpenRGB.loadMode(mode);
        }
        else if (timeout > 0)
            timeout -= 1;
        else if (player != null) {
            if (!RgbCommand.toggled) {
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
}
