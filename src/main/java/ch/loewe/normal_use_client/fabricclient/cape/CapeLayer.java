package ch.loewe.normal_use_client.fabricclient.cape;

import ch.loewe.normal_use_client.fabricclient.client.FabricClientClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
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
    public CapeLayer(EntityRenderer<?> p_i50950_1_) {
        super((FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>) p_i50950_1_);
    }

    public void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn, AbstractClientPlayerEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        PlayerHandler playerHandler = PlayerHandler.getFromPlayer(entitylivingbaseIn);
        if (playerHandler.getShowCape() && !entitylivingbaseIn.isInvisible() && (entitylivingbaseIn.getCapeTexture() != null || playerHandler.getCapeLocation() != null)) {
            ItemStack itemStack = entitylivingbaseIn.getEquippedStack(EquipmentSlot.CHEST);
            if (itemStack.getItem() != Items.ELYTRA || playerHandler.getForceHideElytra() && !playerHandler.getForceShowElytra()) {
                matrixStackIn.push();
                matrixStackIn.translate(0.0D, 0.0D, 0.125D);
                double d0 = MathHelper.lerp(partialTicks, entitylivingbaseIn.prevCapeX, entitylivingbaseIn.capeX) - MathHelper.lerp(partialTicks, entitylivingbaseIn.prevX, entitylivingbaseIn.getX());
                double d1 = MathHelper.lerp(partialTicks, entitylivingbaseIn.prevCapeY, entitylivingbaseIn.capeY) - MathHelper.lerp(partialTicks, entitylivingbaseIn.prevY, entitylivingbaseIn.getY());
                double d2 = MathHelper.lerp(partialTicks, entitylivingbaseIn.prevCapeZ, entitylivingbaseIn.capeZ) - MathHelper.lerp(partialTicks, entitylivingbaseIn.prevZ, entitylivingbaseIn.getZ());
                float f = entitylivingbaseIn.prevBodyYaw + (entitylivingbaseIn.bodyYaw - entitylivingbaseIn.prevBodyYaw);
                double d3 = MathHelper.sin(f * 0.017453292F);
                double d4 = -MathHelper.cos(f * 0.017453292F);
                float f1 = (float)d1 * 10.0F;
                f1 = MathHelper.clamp(f1, -6.0F, 32.0F);
                float f2 = (float)(d0 * d3 + d2 * d4) * 100.0F;
                f2 = MathHelper.clamp(f2, 0.0F, 150.0F);
                float f3 = (float)(d0 * d4 - d2 * d3) * 100.0F;
                f3 = MathHelper.clamp(f3, -20.0F, 20.0F);
                if (f2 < 0.0F) {
                    f2 = 0.0F;
                }

                float f4 = MathHelper.lerp(partialTicks, entitylivingbaseIn.prevStrideDistance, entitylivingbaseIn.strideDistance);
                f1 += MathHelper.sin(MathHelper.lerp(partialTicks, entitylivingbaseIn.prevHorizontalSpeed, entitylivingbaseIn.horizontalSpeed) * 6.0F) * 32.0F * f4;
                f1 -= 180.0;
                if (entitylivingbaseIn.isInSneakingPose()) {
                    f1 += 25.0F;
                }

                matrixStackIn.multiply(RotationAxis.POSITIVE_X.rotationDegrees(6.0F + f2 / 2.0F + f1));
                matrixStackIn.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(f3 / 2.0F));
                matrixStackIn.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F - f3 / 2.0F));
                VertexConsumer vertexConsumer;
                if (playerHandler.getCapeLocation() != null) {
                    vertexConsumer = ItemRenderer.getItemGlintConsumer(bufferIn, RenderLayer.getEntityTranslucent(playerHandler.getCapeLocation()), false, playerHandler.getHasCapeGlint());
                } else {
                    vertexConsumer = ItemRenderer.getItemGlintConsumer(bufferIn, RenderLayer.getEntityTranslucent(entitylivingbaseIn.getCapeTexture()), false, false);
                }

                this.getContextModel().renderCape(matrixStackIn, vertexConsumer, packedLightIn, OverlayTexture.DEFAULT_UV);
                matrixStackIn.pop();
            }
        }

    }
}

