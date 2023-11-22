package ch.loewe.normal_use_client.fabricclient.cape;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class CapeLayer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    public CapeLayer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> featureRendererContext) {
        super(featureRendererContext);
    }

    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, float h, float j, float k, float l) {
        PlayerHandler playerHandler = PlayerHandler.getFromPlayer(abstractClientPlayerEntity);
        if (!abstractClientPlayerEntity.isInvisible()
                && abstractClientPlayerEntity.isPartVisible(PlayerModelPart.CAPE) && (abstractClientPlayerEntity.getSkinTextures().capeTexture() != null
                || playerHandler.getCapeLocation() != null)) {
            ItemStack itemStack = abstractClientPlayerEntity.getEquippedStack(EquipmentSlot.CHEST);
            if (!itemStack.isOf(Items.ELYTRA)) {
                matrixStack.push();
                matrixStack.translate(0.0F, 0.0F, 0.125F);
                double d = MathHelper.lerp(h, abstractClientPlayerEntity.prevCapeX, abstractClientPlayerEntity.capeX) - MathHelper.lerp(h, abstractClientPlayerEntity.prevX, abstractClientPlayerEntity.getX());
                double e = MathHelper.lerp(h, abstractClientPlayerEntity.prevCapeY, abstractClientPlayerEntity.capeY) - MathHelper.lerp(h, abstractClientPlayerEntity.prevY, abstractClientPlayerEntity.getY());
                double m = MathHelper.lerp(h, abstractClientPlayerEntity.prevCapeZ, abstractClientPlayerEntity.capeZ) - MathHelper.lerp(h, abstractClientPlayerEntity.prevZ, abstractClientPlayerEntity.getZ());
                float n = MathHelper.lerpAngleDegrees(h, abstractClientPlayerEntity.prevBodyYaw, abstractClientPlayerEntity.bodyYaw);
                double o = MathHelper.sin(n * 0.017453292F);
                double p = -MathHelper.cos(n * 0.017453292F);
                float q = (float)e * 10.0F;
                q = MathHelper.clamp(q, -6.0F, 32.0F);
                float r = (float)(d * o + m * p) * 100.0F;
                r = MathHelper.clamp(r, 0.0F, 150.0F);
                float s = (float)(d * p - m * o) * 100.0F;
                s = MathHelper.clamp(s, -20.0F, 20.0F);
                if (r < 0.0F) {
                    r = 0.0F;
                }

                float t = MathHelper.lerp(h, abstractClientPlayerEntity.prevStrideDistance, abstractClientPlayerEntity.strideDistance);
                q += MathHelper.sin(MathHelper.lerp(h, abstractClientPlayerEntity.prevHorizontalSpeed, abstractClientPlayerEntity.horizontalSpeed) * 6.0F) * 32.0F * t;
                if (abstractClientPlayerEntity.isInSneakingPose()) {
                    q += 25.0F;
                }

                matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(6.0F + r / 2.0F + q));
                matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(s / 2.0F));
                matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - s / 2.0F));
                VertexConsumer vertexConsumer;
                if (playerHandler.getCapeLocation() != null) {
                    vertexConsumer = ItemRenderer.getItemGlintConsumer(vertexConsumerProvider, RenderLayer.getEntitySolid(playerHandler.getCapeLocation()), false, playerHandler.getHasCapeGlint());
                } else {
                    vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntitySolid(abstractClientPlayerEntity.getSkinTextures().capeTexture()));
                }
                this.getContextModel().renderCape(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
                matrixStack.pop();
            }
        }

    }
}

