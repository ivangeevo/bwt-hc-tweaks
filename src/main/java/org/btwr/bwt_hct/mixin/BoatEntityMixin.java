package org.btwr.bwt_hct.mixin;

import com.bwt.items.BwtItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.level.Level;
import org.btwr.bwt_hct.config.BWT_HCTConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Boat.class)
public abstract class BoatEntityMixin extends VehicleEntity {
    public BoatEntityMixin(EntityType<?> entityType, Level world) {
        super(entityType, world);
    }

    // Modifies the boat speed to be slower if the player isn't holding a sail
    @ModifyConstant(method = "controlBoat", constant = @Constant(floatValue = 0.04F))
    private float scaleBoatSpeed(float original) {
        if (!BWT_HCTConfig.hcBoatRework.get()) return original;

        Boat self = (Boat) (Object) this;
        LivingEntity controller = self.getControllingPassenger();

        boolean hasSail = controller instanceof Player player && player.isHolding(BwtItems.sailItem);

        return hasSail ? original : original * 0.35F;
    }

    // Makes the boat move forward automatically if holding a sail
    @Inject(method = "floatBoat", at = @At("HEAD"))
    private void forceForwardWithSail(CallbackInfo ci) {
        if (!BWT_HCTConfig.hcBoatRework.get()) return;

        Boat self = (Boat)(Object)this;
        LivingEntity controller = self.getControllingPassenger();

        if (controller instanceof Player player && player.isHolding(BwtItems.sailItem)) {
            ((BoatEntityAccessor)self).setPressingForward(true);
        }
    }

}