package org.btwr.bwt_hct.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.btwr.bwt_hct.data.ModDataAttachments;
import org.btwr.bwt_hct.data.RecentlyOnChoppingBlockCountdownData;
import org.btwr.bwt_hct.world.ModDamageTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(method = "damage", at = @At("TAIL"))
    private void trackChoppingBlockDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) return; // damage didn't apply

        LivingEntity self = (LivingEntity)(Object)this;

        if (source.isOf(ModDamageTypes.CHOPPING_BLOCK)) {
            self.setAttached(
                    ModDataAttachments.RECENTLY_ON_CHOPPING_BLOCK_COUNTDOWN,
                    new RecentlyOnChoppingBlockCountdownData(RecentlyOnChoppingBlockCountdownData.maxCountDown)
            );
        }
    }
}