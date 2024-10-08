package ch.loewe.normal_use_client.fabricclient.mixin;

import ch.loewe.normal_use_client.fabricclient.client.FabricClientClient;
import ch.loewe.normal_use_client.fabricclient.loewe.DamageRGB;
import ch.loewe.normal_use_client.fabricclient.modmenu.Config;
import ch.loewe.normal_use_client.fabricclient.openrgb.OpenRGB;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static ch.loewe.normal_use_client.fabricclient.client.FabricClientClient.*;

@Mixin({ClientConnection.class})
public abstract class DisconnectMixin {

    @Inject(method = "handleDisconnection", at = @At("HEAD"), cancellable = true)
    private void handleDisconnection(CallbackInfo ci) {
        if (stopDisconnect) {
            //ci.cancel();
        } else {
            lastAddress = new ServerAddress("-", 25565);
            isOpOnMonopoly = false;
            DamageRGB.firstAfterJoined = true;
            DamageRGB.oldHealth = -1;
            OpenRGB.loadMode(colorMap.get(Config.getStandardColor()), true);
        }
    }
}
