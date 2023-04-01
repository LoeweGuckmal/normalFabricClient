package ch.loewe.normal_use_client.fabricclient.cape;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class Deadmau5 extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    public Deadmau5(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> featureRendererContext) {
        super(featureRendererContext);
    }

    public void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn, AbstractClientPlayerEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        PlayerHandler playerHandler = PlayerHandler.getFromPlayer(entitylivingbaseIn);
        if (playerHandler.getEarLocation() != null && entitylivingbaseIn.hasSkinTexture() && !entitylivingbaseIn.isInvisible()) {
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderLayer.getEntitySolid(playerHandler.getEarLocation()));
            int i = LivingEntityRenderer.getOverlay(entitylivingbaseIn, 0.0F);
            matrixStackIn.push();
            if (entitylivingbaseIn.isInSneakingPose()) {
                matrixStackIn.translate(0.0F, 0.25F, 0.0F);
            }

            matrixStackIn.scale(1.3333334F, 1.3333334F, 1.3333334F);
            this.getContextModel().renderEars(matrixStackIn, ivertexbuilder, packedLightIn, i);
            matrixStackIn.pop();
        }

    }
}
