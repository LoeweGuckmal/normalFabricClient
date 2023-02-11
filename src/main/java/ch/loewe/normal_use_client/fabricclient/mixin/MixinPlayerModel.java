package ch.loewe.normal_use_client.fabricclient.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin({PlayerEntityModel.class})
public abstract class MixinPlayerModel extends BipedEntityModel<LivingEntity> {
    public MixinPlayerModel(ModelPart root) {
        super(root);
    }

    @Inject(
            method = {"getTexturedModelData(Lnet/minecraft/client/model/Dilation;Z)Lnet/minecraft/client/model/ModelData;"},
            at = {@At("RETURN")},
            cancellable = true
    )
    private static void getTexturedModelData(Dilation cubeDeformation, boolean slim, CallbackInfoReturnable<ModelData> cir) {
        ModelData meshDefinition = (ModelData)cir.getReturnValue();
        meshDefinition.getRoot().addChild("ear", ModelPartBuilder.create(), ModelTransform.NONE);
        ModelPartData partDefinition = meshDefinition.getRoot().getChild("ear");
        partDefinition.addChild("left_ear", ModelPartBuilder.create().uv(0, 0).cuboid(1.5F, -10.5F, -1.0F, 6.0F, 6.0F, 1.0F, cubeDeformation, 0.21875F, 0.109375F), ModelTransform.NONE);
        partDefinition.addChild("right_ear", ModelPartBuilder.create().uv(0, 0).cuboid(-7.5F, -10.5F, -1.0F, 6.0F, 6.0F, 1.0F, cubeDeformation, 0.21875F, 0.109375F), ModelTransform.NONE);
        cir.setReturnValue(meshDefinition);
    }
}

