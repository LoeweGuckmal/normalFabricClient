package ch.loewe.normal_use_client.fabricclient.mixin;

import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.network.SocialInteractionsManager;
import net.minecraft.client.report.AbuseReportContext;
import net.minecraft.client.util.ProfileKeys;
import net.minecraft.client.util.Session;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({MinecraftClient.class})
public interface MinecraftAccessor {

    @Accessor("session")
    @Mutable
    void ias$user(Session var1);

    @Accessor("socialInteractionsManager")
    @Mutable
    void ias$playerSocialManager(SocialInteractionsManager var1);

    @Accessor("profileKeys")
    @Mutable
    void ias$profileKeyPairManager(ProfileKeys var1);

    @Accessor("abuseReportContext")
    @Mutable
    void ias$reportingContext(AbuseReportContext var1);

    @Accessor("userApiService")
    @Mutable
    void ias$userApiService(UserApiService var1);

    @Invoker("createUserApiService")
    UserApiService ias$createUserApiService(YggdrasilAuthenticationService var1, RunArgs var2);

    @Accessor("authenticationService")
    YggdrasilAuthenticationService ias$authenticationService();
}
