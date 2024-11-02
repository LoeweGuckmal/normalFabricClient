package ch.loewe.normal_use_client.fabricclient.mixin;

import ch.loewe.normal_use_client.fabricclient.zoom.Zoom;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static ch.loewe.normal_use_client.fabricclient.zoom.Zoom.*;

@Environment(EnvType.CLIENT)
@Mixin({GameRenderer.class})
public class ZoomMixin {
    private static float oldZoomLevel = 0;

    public ZoomMixin() {
    }

    @Inject(
            method = {"getFov(Lnet/minecraft/client/render/Camera;FZ)F"},
            at = {@At("RETURN")},
            cancellable = true
    )
    public void getZoomLevel(CallbackInfoReturnable<Float> callbackInfo) {
        if (Zoom.isZooming()) {
            float fov = callbackInfo.getReturnValue();
            if (zoomLevel > 1)
                zoomLevel = 1;
            if (zoomLevel < 9.12575328614815E-5D)
                zoomLevel = 9.12575328614815E-5D;
            if (!(zoomLevel == oldZoomLevel)) {
                zoom_X = 1 / zoomLevel;
                if (zoom_X > 10000)
                    zoom_X = 10000;
            }
            oldZoomLevel = (float) zoomLevel;
            callbackInfo.setReturnValue((float) (fov * zoomLevel));
        }

        Zoom.manageSmoothCamera();
    }
}

