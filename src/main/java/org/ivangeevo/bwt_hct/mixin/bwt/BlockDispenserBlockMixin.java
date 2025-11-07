package org.ivangeevo.bwt_hct.mixin.bwt;

import com.bwt.blocks.block_dispenser.BlockDispenserBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.ivangeevo.bwt_hct.BWT_HCTMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockDispenserBlock.class)
public abstract class BlockDispenserBlockMixin extends DispenserBlock {

    public BlockDispenserBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "isReceivingPower", at = @At("HEAD"), cancellable = true)
    private void setRequringStrongPower(World world, BlockPos pos, Direction facing, CallbackInfoReturnable<Boolean> cir) {
        if (!BWT_HCTMod.getInstance().settings.isBlockDispenserRequiringStrongPower()) return;
        cir.setReturnValue(world.getReceivedStrongRedstonePower(pos) > 0 || world.getReceivedStrongRedstonePower(pos.up()) > 0);
    }


}
