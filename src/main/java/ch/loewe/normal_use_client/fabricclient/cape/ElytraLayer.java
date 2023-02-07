package ch.loewe.normal_use_client.fabricclient.cape;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class ElytraLayer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
    private static final Identifier WINGS_LOCATION = new Identifier("textures/entity/elytra.png");
    private final ElytraEntityModel<T> elytraModel;

    public ElytraLayer(EntityRenderer<? extends PlayerEntity> p_174493_, EntityModelLoader entityModelSet) {
        super((FeatureRendererContext<T, M>) p_174493_);
        this.elytraModel = new ElytraEntityModel(entityModelSet.getModelPart(EntityModelLayers.ELYTRA));
    }

    public void render(MatrixStack poseStack, VertexConsumerProvider bufferIn, int packedLightIn, T livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        AbstractClientPlayerEntity abstractClientPlayer = (AbstractClientPlayerEntity)livingEntity;
        PlayerHandler playerHandler = PlayerHandler.getFromPlayer(abstractClientPlayer);
        ItemStack itemStack = abstractClientPlayer.getEquippedStack(EquipmentSlot.CHEST);
        if ((itemStack.getItem() == Items.ELYTRA || playerHandler.getForceShowElytra()) && !playerHandler.getForceHideElytra()) {
            Identifier resourcelocation;
            if (playerHandler.getCapeLocation() != null) {
                resourcelocation = playerHandler.getCapeLocation();
            } else if (abstractClientPlayer.getCapeTexture() != null && abstractClientPlayer.isPartVisible(PlayerModelPart.CAPE)) {
                resourcelocation = abstractClientPlayer.getCapeTexture();
            } else {
                resourcelocation = WINGS_LOCATION;
            }

            poseStack.push();
            poseStack.translate(0.0D, 0.0D, 0.125D);
            this.getContextModel().copyStateTo(this.elytraModel);
            this.elytraModel.setAngles(livingEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            VertexConsumer vertexConsumer = ItemRenderer.getItemGlintConsumer(bufferIn, this.elytraModel.getLayer(resourcelocation), false, itemStack.hasGlint());
            this.elytraModel.render(poseStack, vertexConsumer, packedLightIn, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
            poseStack.pop();
        }

    }
}
