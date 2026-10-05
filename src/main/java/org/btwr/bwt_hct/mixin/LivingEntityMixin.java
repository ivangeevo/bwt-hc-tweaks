package org.btwr.bwt_hct.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.btwr.bwt_hct.data.ModDataAttachments;
import org.btwr.bwt_hct.data.RecentlyOnChoppingBlockCountdownData;
import org.btwr.bwt_hct.world.ModDamageTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(method = "hurt", at = @At("TAIL"))
    private void trackChoppingBlockDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) return; // damage didn't apply

        LivingEntity self = (LivingEntity)(Object)this;

        if (source.is(ModDamageTypes.CHOPPING_BLOCK)) {
            self.setAttached(
                    ModDataAttachments.RECENTLY_ON_CHOPPING_BLOCK_COUNTDOWN,
                    new RecentlyOnChoppingBlockCountdownData(RecentlyOnChoppingBlockCountdownData.maxCountDown)
            );
        }
    }
}