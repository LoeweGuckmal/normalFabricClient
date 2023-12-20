package ch.loewe.normal_use_client.fabricclient.mixin;

import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({MinecraftClient.class})
public class MinecraftAccessorClass {
@Inject(
            method = "createUserApiService(Lcom/mojang/authlib/yggdrasil/YggdrasilAuthenticationService;Lnet/minecraft/client/RunArgs;)Lcom/mojang/authlib/minecraft/UserApiService;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void inject(YggdrasilAuthenticationService authService, RunArgs runArgs, CallbackInfoReturnable<UserApiService> cir) {
    cir.setReturnValue(authService.createUserApiService(runArgs.network.session.getAccessToken()));
    cir.cancel();
    }
}
