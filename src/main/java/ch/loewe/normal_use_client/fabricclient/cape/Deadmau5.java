package ch.loewe.normal_use_client.fabricclient.cape;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;

public class Deadmau5 extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {
    public Deadmau5(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> featureRendererContext) {
        super(featureRendererContext);
    }

    /*public void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn, AbstractClientPlayerEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        PlayerHandler playerHandler = PlayerHandler.getFromPlayer(entitylivingbaseIn);
        if (playerHandler.getEarLocation() != null && entitylivingbaseIn.getSkinTextures() != null && !entitylivingbaseIn.isInvisible()) {
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderLayer.getEntitySolid(playerHandler.getEarLocation()));
            int i = LivingEntityRenderer.getOverlay(entitylivingbaseIn, 0.0F);
            matrixStackIn.push();
            if (entitylivingbaseIn.isInSneakingPose()) {
                matrixStackIn.translate(0.0F, 0.25F, 0.0F);
            }

            matrixStackIn.scale(1.3333334F, 1.3333334F, 1.3333334F);
            this.getContextModel().render(matrixStackIn, ivertexbuilder, packedLightIn, i);
            matrixStackIn.pop();
        }

    }*/

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, PlayerEntityRenderState state, float limbAngle, float limbDistance) {
        PlayerHandler playerHandler = PlayerHandler.getFromPlayer(state);
        if (playerHandler != null && playerHandler.getEarLocation() != null && state.skinTextures != null && !state.invisible) {
            VertexConsumer ivertexbuilder = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(playerHandler.getEarLocation()));
            int i = LivingEntityRenderer.getOverlay(state, 0.0F);
            matrices.push();
            if (state.isInSneakingPose) {
                matrices.translate(0.0F, 0.25F, 0.0F);
            }

            matrices.scale(1.3333334F, 1.3333334F, 1.3333334F);
            this.getContextModel().render(matrices, ivertexbuilder, light, i);
            matrices.pop();
        }
    }
}
