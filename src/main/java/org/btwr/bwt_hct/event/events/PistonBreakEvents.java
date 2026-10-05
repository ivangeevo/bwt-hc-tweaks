package org.btwr.bwt_hct.event.events;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class PistonBreakEvents {

    private static final List<PistonBreakCallback> CALLBACKS = new ArrayList<>();

    public static void register(PistonBreakCallback callback) {
        CALLBACKS.add(callback);
    }

    public static void fire(Level world, BlockPos pos, BlockState state) {
        for (var cb : CALLBACKS) cb.onPistonBreak(world, pos, state);
    }

}