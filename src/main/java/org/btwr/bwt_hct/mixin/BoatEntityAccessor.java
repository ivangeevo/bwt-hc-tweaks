package org.btwr.bwt_hct.mixin;

import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Boat.class)
public interface BoatEntityAccessor {
    @Accessor("inputUp") void setPressingForward(boolean pressingForward);
}