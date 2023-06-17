package ch.loewe.normal_use_client.fabricclient.mixin;

import com.mojang.authlib.exceptions.MinecraftClientException;
import com.mojang.authlib.minecraft.UserApiService;
import net.minecraft.SharedConstants;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.ProfileKeysImpl;
import net.minecraft.network.encryption.NetworkEncryptionException;
import net.minecraft.network.encryption.PlayerKeyPair;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.client.texture.TextureManager.MISSING_IDENTIFIER;

@Mixin({ProfileKeysImpl.class})
public abstract class Errors {
    @Mixin({ProfileKeysImpl.class})
    public abstract static class KeyPairMixin {
        @Shadow
        protected abstract void saveKeyPairToFile(@Nullable PlayerKeyPair keyPair);

        @Shadow
        @Final
        private UserApiService userApiService;

        @Shadow
        protected abstract PlayerKeyPair fetchKeyPair(UserApiService userApiService) throws NetworkEncryptionException, IOException;

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

    @Mixin(TextureManager.class)
    public abstract static class TextureManagerMixin {
        @Shadow @Final private ResourceManager resourceContainer;

        @Shadow @Final private static Logger LOGGER;

        @Inject(method = "loadTexture", at = @At("HEAD"), cancellable = true)
        private void inject(Identifier id, AbstractTexture texture, CallbackInfoReturnable<AbstractTexture> cir) {
            AbstractTexture abstractTexture = null;
            try {
                texture.load(resourceContainer);
                abstractTexture = texture;
            } catch (IOException var6) {
                if (id != MISSING_IDENTIFIER) {
                    LOGGER.warn("Failed to load texture: {}", id, var6);
                }

                abstractTexture = MissingSprite.getMissingSpriteTexture();
            } catch (Throwable ignored) {}
            cir.setReturnValue(abstractTexture);
            cir.cancel();
        }
    }
}
