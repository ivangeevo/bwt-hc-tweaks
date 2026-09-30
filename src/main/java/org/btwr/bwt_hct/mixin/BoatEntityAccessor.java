package org.btwr.bwt_hct.mixin;

import net.minecraft.entity.vehicle.BoatEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BoatEntity.class)
public interface BoatEntityAccessor {
    @Accessor("pressingForward")
    void setPressingForward(boolean pressingForward);
}