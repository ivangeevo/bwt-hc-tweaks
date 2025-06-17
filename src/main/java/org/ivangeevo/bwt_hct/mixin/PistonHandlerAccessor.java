package org.ivangeevo.bwt_hct.mixin;

import net.minecraft.block.piston.PistonHandler;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PistonHandler.class)
public interface PistonHandlerAccessor {
    @Accessor("world")
    World getWorld();
}
