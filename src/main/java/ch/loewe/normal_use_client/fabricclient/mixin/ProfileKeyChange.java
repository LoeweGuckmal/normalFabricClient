package ch.loewe.normal_use_client.fabricclient.mixin;

import ch.loewe.normal_use_client.fabricclient.client.FabricClientClient;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.MinecraftClientException;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.util.ProfileKeysImpl;
import net.minecraft.network.encryption.NetworkEncryptionException;
import net.minecraft.network.encryption.PlayerKeyPair;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Mixin({ProfileKeysImpl.class})
public abstract class ProfileKeyChange {
    @Shadow protected abstract void saveKeyPairToFile(@Nullable PlayerKeyPair keyPair);

    @Shadow @Final private UserApiService userApiService;

    @Shadow protected abstract PlayerKeyPair fetchKeyPair(UserApiService userApiService) throws NetworkEncryptionException, IOException;

    @Inject(
            method = "getKeyPair",
            at = @At("HEAD"),
            cancellable = true
    )
    private void inject(Optional<PlayerKeyPair> currentKey, CallbackInfoReturnable<CompletableFuture<Optional<PlayerKeyPair>>> cir) {
    cir.setReturnValue(CompletableFuture.supplyAsync(() -> {
        if (currentKey.isPresent() && !currentKey.get().isExpired()) {
            if (!SharedConstants.isDevelopment) {
                saveKeyPairToFile(null);
            }

            return currentKey;
        } else {
            try {
                PlayerKeyPair playerKeyPair = this.fetchKeyPair(this.userApiService);
                saveKeyPairToFile(playerKeyPair);
                return Optional.of(playerKeyPair);
            } catch (NetworkEncryptionException | MinecraftClientException | IOException ignored) {
                saveKeyPairToFile(null);
                return currentKey;
            }
        }
    }, Util.getMainWorkerExecutor()));
    cir.cancel();
    }
}
