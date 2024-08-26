package ch.loewe.normal_use_client.fabricclient.mixin;

import ch.loewe.normal_use_client.fabricclient.loewe.DamageRGB;
import ch.loewe.normal_use_client.fabricclient.loewe.WayPoints;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.network.CookieStorage;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static ch.loewe.normal_use_client.fabricclient.client.FabricClientClient.lastAddress;

@Mixin(ConnectScreen.class)
public abstract class ConnectMixin {

    @Inject(method = "connect(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/network/ServerAddress;Lnet/minecraft/client/network/ServerInfo;Lnet/minecraft/client/network/CookieStorage;)V", at = @At("HEAD"))
    private void connect(MinecraftClient client, ServerAddress address, ServerInfo info, CookieStorage cookieStorage, CallbackInfo ci) {
        DamageRGB.firstAfterJoined = true;
        lastAddress = address;
        WayPoints.getWayPoints();
    }
}
