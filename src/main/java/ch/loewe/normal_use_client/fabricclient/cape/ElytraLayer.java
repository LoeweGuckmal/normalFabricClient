package ch.loewe.normal_use_client.fabricclient.cape;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class ElytraLayer extends FeatureRenderer<PlayerEntityRenderState, ElytraEntityModel> {
    private static final Identifier WINGS_LOCATION = Identifier.of("textures/entity/elytra.png");
    private final ElytraEntityModel elytraModel;

    public ElytraLayer(FeatureRendererContext<PlayerEntityRenderState, ElytraEntityModel> featureRendererContext, EntityModelLoader entityModelSet) {
        super(featureRendererContext);
        this.elytraModel = new ElytraEntityModel(entityModelSet.getModelPart(EntityModelLayers.ELYTRA));
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, PlayerEntityRenderState state, float limbAngle, float limbDistance) {
        PlayerHandler playerHandler = PlayerHandler.getFromPlayer(state);
        ItemStack itemStack = state.equippedChestStack;
        if (itemStack.getItem() == Items.ELYTRA) {
            Identifier resourcelocation;
            if (playerHandler != null && playerHandler.getCapeLocation() != null) {
                resourcelocation = playerHandler.getCapeLocation();
            } else if (state.skinTextures.capeTexture() != null && state.capeVisible) {
                resourcelocation = state.skinTextures.capeTexture();
            } else {
                resourcelocation = WINGS_LOCATION;
            }

            matrices.push();
            matrices.translate(0.0D, 0.0D, 0.125D);
            //this.getContextModel().copyStateTo(this.elytraModel);
            this.elytraModel.setAngles(state);
            VertexConsumer vertexConsumer = ItemRenderer.getItemGlintConsumer(vertexConsumers, this.elytraModel.getLayer(resourcelocation), false, itemStack.hasGlint());
            this.elytraModel.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
            matrices.pop();
        }
    }

    /*public void render(MatrixStack poseStack, VertexConsumerProvider bufferIn, int packedLightIn, AbstractClientPlayerEntity livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        PlayerHandler playerHandler = PlayerHandler.getFromPlayer(livingEntity);
        ItemStack itemStack = livingEntity.getEquippedStack(EquipmentSlot.CHEST);
        if (itemStack.getItem() == Items.ELYTRA) {
            Identifier resourcelocation;
            if (playerHandler.getCapeLocation() != null) {
                resourcelocation = playerHandler.getCapeLocation();
            } else if (livingEntity.getSkinTextures().capeTexture() != null && livingEntity.isPartVisible(PlayerModelPart.CAPE)) {
                resourcelocation = livingEntity.getSkinTextures().capeTexture();
            } else {
                resourcelocation = WINGS_LOCATION;
            }

            poseStack.push();
            poseStack.translate(0.0D, 0.0D, 0.125D);
            this.getContextModel().copyStateTo(this.elytraModel);
            this.elytraModel.setAngles(livingEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            VertexConsumer vertexConsumer = ItemRenderer.getItemGlintConsumer(bufferIn, this.elytraModel.getLayer(resourcelocation), false, itemStack.hasGlint());
            this.elytraModel.render(poseStack, vertexConsumer, packedLightIn, OverlayTexture.DEFAULT_UV);
            poseStack.pop();
        }

    }*/
}
