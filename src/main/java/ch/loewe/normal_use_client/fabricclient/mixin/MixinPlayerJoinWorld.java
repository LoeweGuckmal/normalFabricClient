package ch.loewe.normal_use_client.fabricclient.mixin;

import ch.loewe.normal_use_client.fabricclient.cape.DownloadManager;
import com.mojang.authlib.GameProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin({AbstractClientPlayerEntity.class})
public abstract class MixinPlayerJoinWorld extends PlayerEntity {

    public MixinPlayerJoinWorld(World level, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(level, blockPos, f, gameProfile);
    }

    @Inject(
            method = {"<init>*"},
            at = {@At("RETURN")}
    )
    private void construct(ClientWorld clientLevel, GameProfile gameProfile, CallbackInfo ci) {
        if (this.getWorld().isClient()) {
            DownloadManager.prepareDownload(this, false, true);
        }
    }

}
