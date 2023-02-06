package ch.loewe.normal_use_client.fabricclient.mixin;


import ch.loewe.normal_use_client.fabricclient.modmenu.Config;
import ch.loewe.normal_use_client.fabricclient.zoom.Zoom;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static ch.loewe.normal_use_client.fabricclient.zoom.Zoom.getZoomX;
import static ch.loewe.normal_use_client.fabricclient.zoom.Zoom.zoomLevel;

@Mixin({Mouse.class})
public class MouseMixin {

    @Shadow private double eventDeltaWheel;

    public MouseMixin() {
    }

    @Inject(
            method = {"onMouseScroll"},
            at = {@At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/Mouse;eventDeltaWheel:D",
                    ordinal = 7
            )},
            cancellable = true
    )
    private void scrollStepCounter(CallbackInfo ci) {
        if (Zoom.isZooming()) {
            if (!(1 / zoomLevel > getZoomX()) || eventDeltaWheel < 0D) {
                double amount = zoomLevel / 10;
                if (zoomLevel > 0.3D)
                    amount = 0.03D;
                zoomLevel = eventDeltaWheel > 0D ? zoomLevel - amount : zoomLevel + amount;
            }
            ci.cancel();
        }
    }
}

