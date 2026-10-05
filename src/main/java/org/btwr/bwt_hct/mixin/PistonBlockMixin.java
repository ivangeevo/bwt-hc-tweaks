package org.btwr.bwt_hct.mixin;

import org.btwr.bwt_hct.event.events.PistonBreakEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(PistonBaseBlock.class)
public abstract class PistonBlockMixin {

    @Inject(method = "moveBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private void onMove(Level world, BlockPos pos, Direction dir, boolean retract, CallbackInfoReturnable<Boolean> cir) {
        // Create a new PistonHandler with the same args as the original method
        PistonStructureResolver pistonHandler = new PistonStructureResolver(world, pos, dir, retract);
        if (!pistonHandler.resolve()) {
            return;
        }

        // This is a slight hack: the original method created the handler before,
        // but here we just need the broken blocks for the event.

        List<BlockPos> brokenBlocks = pistonHandler.getToDestroy();
        for (BlockPos brokenPos : brokenBlocks) {
            BlockState brokenState = world.getBlockState(brokenPos);
            PistonBreakEvents.fire(world, brokenPos, brokenState);
        }
    }

}