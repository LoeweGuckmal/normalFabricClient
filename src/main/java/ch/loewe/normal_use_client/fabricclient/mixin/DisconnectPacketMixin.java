package ch.loewe.normal_use_client.fabricclient.mixin;

import ch.loewe.normal_use_client.fabricclient.client.FabricClientClient;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientCommonNetworkHandler.class})
public abstract class DisconnectPacketMixin {

    @Inject(method = "onDisconnected", at = @At("HEAD"), cancellable = true)
    private void handleDisconnection(CallbackInfo ci) {
        FabricClientClient.stopDisconnect = true;
        //ci.cancel();
    }
}
