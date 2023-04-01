package ch.loewe.normal_use_client.fabricclient.mixin;

import ch.loewe.normal_use_client.fabricclient.client.FabricClientClient;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class DisconnectMixin {

    @Inject(method = "handleDisconnection", at = @At("HEAD"))
    private void handleDisconnection(CallbackInfo ci) {
        FabricClientClient.isOnMonopoly = false;
    }
}
