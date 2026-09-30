package org.btwr.bwt_hct.mixin.client;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BoatEntityModel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BoatEntityModel.class)
public abstract class BoatEntityModelMixin {

    @Shadow @Final
    private ModelPart leftPaddle;

    @Shadow @Final
    private ModelPart rightPaddle;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void hidePaddles(ModelPart root, CallbackInfo ci) {
        this.leftPaddle.visible = false;
        this.rightPaddle.visible = false;
    }
}
