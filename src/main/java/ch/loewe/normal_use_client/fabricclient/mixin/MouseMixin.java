package ch.loewe.normal_use_client.fabricclient.mixin;


import ch.loewe.normal_use_client.fabricclient.zoom.Zoom;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static ch.loewe.normal_use_client.fabricclient.zoom.Zoom.zoomLevel;

@Environment(EnvType.CLIENT)
@Mixin({Mouse.class})
public class MouseMixin {

    //@Shadow private double eventDeltaVerticalWheel;

    public MouseMixin() {
    }

    @Inject(
            method = {"onMouseScroll"},
            at = {@At("HEAD")},
            cancellable = true
    )
    private void scrollStepCounter(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (Zoom.isZooming()) {
            double amount = zoomLevel / 10D;
            if (zoomLevel > 0.3D)
                amount = 0.03D;
            zoomLevel = vertical > 0D ? zoomLevel - amount : zoomLevel + amount;
            ci.cancel();
        }
    }
}

