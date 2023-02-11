package ch.loewe.normal_use_client.fabricclient.mixin;

import ch.loewe.normal_use_client.fabricclient.modmenu.Config;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Perspective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin({GameOptions.class})
public abstract class skipThirdPersonMixin {
    @Shadow public abstract void setPerspective(Perspective perspective);

    @Inject(
            method = {"setPerspective"},
            at = {@At("TAIL")}
    )
    public void onIsThirdPerson(Perspective perspective, CallbackInfo ci) {
        if (perspective == Perspective.THIRD_PERSON_FRONT && Config.getSkipFrontView()) {
            setPerspective(Perspective.FIRST_PERSON);
        }
    }
}
