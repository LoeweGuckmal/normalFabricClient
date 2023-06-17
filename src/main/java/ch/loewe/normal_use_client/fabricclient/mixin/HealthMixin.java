package ch.loewe.normal_use_client.fabricclient.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static ch.loewe.normal_use_client.fabricclient.cape.DownloadManager.isLocalPlayer;
import static ch.loewe.normal_use_client.fabricclient.loewe.DamageRGB.damageRGB;

@Mixin(LivingEntity.class)
@Environment(EnvType.CLIENT)
public abstract class HealthMixin {
    private static int oldHealth = 10000;

    @Inject(method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", at = @At("RETURN"))
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if ((LivingEntity) (Object) this instanceof PlayerEntity player && isLocalPlayer(player)) {
            damageRGB(false);
            oldHealth = (int) Math.ceil(player.getHealth()) + (int) Math.ceil(player.getAbsorptionAmount());
        }
    }
    @Inject(method = "heal(F)V", at = @At("TAIL"))
    private void onHeal(float amount, CallbackInfo ci) {
        if ((LivingEntity) (Object) this instanceof PlayerEntity player && isLocalPlayer(player)) {
            if (((int) Math.ceil(player.getHealth()) + (int) Math.ceil(player.getAbsorptionAmount())) > oldHealth) {
                damageRGB(true);
            }
            oldHealth = (int) Math.ceil(player.getHealth()) + (int) Math.ceil(player.getAbsorptionAmount());
        }
    }
}
