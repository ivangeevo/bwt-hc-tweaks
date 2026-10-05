package org.btwr.bwt_hct.mixin.client;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.vehicle.Boat;
import org.btwr.bwt_hct.config.BWT_HCTConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {
    @Shadow private boolean handsBusy;

    @Inject(method = "rideTick", at = @At("TAIL"))
    private void dontHidePaddleHands(CallbackInfo ci) {
        if (!BWT_HCTConfig.hcBoatRework.get()) return;

        LocalPlayer self = (LocalPlayer) (Object) this;
        if (self.getControlledVehicle() instanceof Boat) {
            this.handsBusy = false;
        }
    }
}