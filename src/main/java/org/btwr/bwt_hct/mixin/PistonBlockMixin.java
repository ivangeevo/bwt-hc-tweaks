package org.btwr.bwt_hct.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.btwr.bwt_hct.event.events.PistonBreakEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(PistonBlock.class)
public abstract class PistonBlockMixin {

    @Inject(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
    private void onMove(World world, BlockPos pos, Direction dir, boolean retract, CallbackInfoReturnable<Boolean> cir) {
        // Create a new PistonHandler with the same args as the original method
        PistonHandler pistonHandler = new PistonHandler(world, pos, dir, retract);
        if (!pistonHandler.calculatePush()) {
            return;
        }

        // This is a slight hack: the original method created the handler before,
        // but here we just need the broken blocks for the event.

        List<BlockPos> brokenBlocks = pistonHandler.getBrokenBlocks();
        for (BlockPos brokenPos : brokenBlocks) {
            BlockState brokenState = world.getBlockState(brokenPos);
            PistonBreakEvents.fire(world, brokenPos, brokenState);
        }
    }

}