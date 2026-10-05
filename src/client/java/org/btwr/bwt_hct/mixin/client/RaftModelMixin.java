package org.btwr.bwt_hct.mixin.client;

import net.minecraft.client.model.RaftModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.vehicle.Boat;
import org.btwr.bwt_hct.config.BWT_HCTConfig;
import org.btwr.bwt_hct.util.PaddlePoseHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RaftModel.class)
public abstract class RaftModelMixin {
    @Shadow @Final private ModelPart leftPaddle;
    @Shadow @Final private ModelPart rightPaddle;

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/vehicle/Boat;FFFFF)V", at = @At("HEAD"))
    private void bwt_hct$resetPaddles(Boat boat, float f, float g, float h, float i, float j, CallbackInfo ci) {
        if (!BWT_HCTConfig.hcBoatRework.get()) return;
        this.leftPaddle.resetPose();
        this.rightPaddle.resetPose();
    }

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/vehicle/Boat;FFFFF)V", at = @At("TAIL"))
    private void stowPaddles(Boat boat, float f, float g, float h, float i, float j, CallbackInfo ci) {
        if (!BWT_HCTConfig.hcBoatRework.get()) return;

        if (PaddlePoseHelper.shouldTuckPaddles(boat)) {
            PaddlePoseHelper.restOnSide(leftPaddle, rightPaddle);
        }
    }
}