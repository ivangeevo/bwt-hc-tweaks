package org.btwr.bwt_hct.event;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class PistonBreakEvents {

    private static final List<PistonBreakCallback> CALLBACKS = new ArrayList<>();

    public static void register(PistonBreakCallback callback) {
        CALLBACKS.add(callback);
    }

    public static void fire(World world, BlockPos pos, BlockState state) {
        for (var cb : CALLBACKS) cb.onPistonBreak(world, pos, state);
    }

}