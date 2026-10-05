package org.btwr.bwt_hct.mixin.bwt;

import com.bwt.blocks.block_dispenser.BlockDispenserBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import org.btwr.bwt_hct.config.BWT_HCTConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockDispenserBlock.class)
public abstract class BlockDispenserBlockMixin extends DispenserBlock {

    public BlockDispenserBlockMixin(Properties settings) {
        super(settings);
    }

    @Inject(method = "isReceivingPower", at = @At("HEAD"), cancellable = true)
    private void setRequringStrongPower(Level world, BlockPos pos, Direction facing, CallbackInfoReturnable<Boolean> cir) {
        if (BWT_HCTConfig.blockDispenserRequiringStrongPower.get()) {
            cir.setReturnValue(world.getDirectSignalTo(pos) > 0 || world.getDirectSignalTo(pos.above()) > 0);
        }
    }

}