package org.btwr.bwt_hct.event.events;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface PistonBreakCallback {
    void onPistonBreak(Level world, BlockPos pos, BlockState state);
}