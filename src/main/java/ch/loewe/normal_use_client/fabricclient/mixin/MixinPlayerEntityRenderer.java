package ch.loewe.normal_use_client.fabricclient.mixin;

import ch.loewe.normal_use_client.fabricclient.cape.CapeLayer;
import ch.loewe.normal_use_client.fabricclient.cape.CompatHooks;
import ch.loewe.normal_use_client.fabricclient.cape.Deadmau5;
import ch.loewe.normal_use_client.fabricclient.cape.ElytraLayer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin({PlayerEntityRenderer.class})
public abstract class MixinPlayerEntityRenderer extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    public MixinPlayerEntityRenderer(Context context, PlayerEntityModel<AbstractClientPlayerEntity> entityModel, float f) {
        super(context, entityModel, f);
    }

    @Inject(
            method = {"<init>*"},
            at = {@At("RETURN")}
    )
    private void construct(Context ctm, boolean alex, CallbackInfo info) {
        this.addFeature(new CapeLayer(this));
        this.addFeature(new Deadmau5(this));
        this.addFeature(new ElytraLayer(this, ctm.getModelLoader()));
        this.features.removeIf((modelFeature) -> modelFeature instanceof ElytraFeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>);
        this.features.removeIf((modelFeature) -> modelFeature instanceof CapeFeatureRenderer);
    }

    @Inject(
            method = {"render"},
            at = {@At("RETURN")}
    )
    private void render(AbstractClientPlayerEntity abstractClientPlayer, float f, float g, MatrixStack poseStack, VertexConsumerProvider multiBufferSource, int i, CallbackInfo ci) {
        CompatHooks.getHooks().forEach((hook) -> hook.onPlayerRender(abstractClientPlayer));
    }
}