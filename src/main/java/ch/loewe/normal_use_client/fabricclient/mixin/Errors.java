package ch.loewe.normal_use_client.fabricclient.mixin;

import com.mojang.authlib.exceptions.*;
import com.mojang.authlib.minecraft.UserApiService;
import net.minecraft.SharedConstants;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.Request;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.session.ProfileKeysImpl;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.network.encryption.NetworkEncryptionException;
import net.minecraft.network.encryption.PlayerKeyPair;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
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

        @Inject(method = "loadTexture", at = @At("HEAD"), cancellable = true)
        private void inject(Identifier id, AbstractTexture texture, CallbackInfoReturnable<AbstractTexture> cir) {
            AbstractTexture abstractTexture = null;
            try {
                texture.load(resourceContainer);
                abstractTexture = texture;
            } catch (IOException var6) {
                if (id != MISSING_IDENTIFIER) {
                    //LOGGER.warn("Failed to load texture: {}", id, var6);
                }

                abstractTexture = MissingSprite.getMissingSpriteTexture();
            } catch (Throwable ignored) {}
            cir.setReturnValue(abstractTexture);
            cir.cancel();
        }
    }

    @Mixin(MinecraftClientHttpException.class)
    public abstract static class MinecraftClientHttpExceptionMixin {

        @Inject(method = "toAuthenticationException", at = @At("HEAD"), cancellable = true, remap = false)
        private void inject(CallbackInfoReturnable<AuthenticationException> cir) {
            cir.setReturnValue(new AuthenticationException());
            cir.cancel();
        }
    }

    @Mixin(RealmsClient.class)
    public abstract static class RealmsClientMixin {
        @Shadow protected abstract String url(String path);

        @Shadow protected abstract String execute(Request<?> r) throws RealmsServiceException;

        @Inject(method = "clientCompatible", at = @At("HEAD"), cancellable = true)
        private void inject(CallbackInfoReturnable<RealmsClient.CompatibleVersionResponse> cir) {
            String string = this.url("mco/client/compatible");

            try {
                String string2 = this.execute(Request.get(string));
                cir.setReturnValue(RealmsClient.CompatibleVersionResponse.valueOf(string2));
            } catch (IllegalArgumentException | RealmsServiceException ignored) {}
            cir.cancel();
        }
    }
}
