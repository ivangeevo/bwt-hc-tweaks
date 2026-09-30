package org.btwr.bwt_hct.mixin;

import com.bwt.items.BwtItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.VehicleEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BoatEntity.class)
public abstract class BoatEntityMixin extends VehicleEntity {
    public BoatEntityMixin(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    // Modifies the boat speed to be slower if the player isn't holding a sail
    @ModifyConstant(method = "updatePaddles", constant = @Constant(floatValue = 0.04F))
    private float scaleBoatSpeed(float original) {
        BoatEntity self = (BoatEntity) (Object) this;
        LivingEntity controller = self.getControllingPassenger();

        boolean hasSail = controller instanceof PlayerEntity player && player.isHolding(BwtItems.sailItem);

        return hasSail ? original : original * 0.35F;
    }

    @Inject(method = "updateVelocity", at = @At("HEAD"))
    private void forceForwardWithSail(CallbackInfo ci) {
        BoatEntity self = (BoatEntity)(Object)this;
        LivingEntity controller = self.getControllingPassenger();

        if (controller instanceof PlayerEntity player && player.isHolding(BwtItems.sailItem)) {
            ((BoatEntityAccessor)self).setPressingForward(true);
        }
    }

}