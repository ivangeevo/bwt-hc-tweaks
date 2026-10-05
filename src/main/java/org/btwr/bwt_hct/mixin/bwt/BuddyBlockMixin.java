package org.btwr.bwt_hct.mixin.bwt;

import com.bwt.blocks.BuddyBlock;
import com.bwt.blocks.SimpleFacingBlock;
import com.bwt.tags.BwtBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.btwr.bwt_hct.config.BWT_HCTConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BuddyBlock.class)
public abstract class BuddyBlockMixin extends SimpleFacingBlock {

    @Shadow public static BooleanProperty POWERED;

    protected BuddyBlockMixin(Properties settings) {
        super(settings);
    }

    // Overrides buddy block neighbor update behavior to make it work the same way as it does in BTW CE
    @Inject(method = "neighborChanged", at = @At("HEAD"), cancellable = true)
    private void onNeighborUpdate(BlockState state, Level world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify, CallbackInfo ci) {
        if (BWT_HCTConfig.oldSchoolBuddyBlockNeighborUpdate.get()) {
            if (world.isClientSide) return;

            // ignore if we’re powered or already ticking
            if (state.getValue(POWERED) || world.getBlockTicks().willTickThisTick(pos, this))
                return;

            // ignore redstone power updates
            if (world.getBestNeighborSignal(pos) > 0)
                return;

            BlockState neighborState = world.getBlockState(sourcePos);

            // ignore redstone-related components by tag
            if (neighborState.is(BwtBlockTags.DOES_NOT_TRIGGER_BUDDY)
                    || sourceBlock.defaultBlockState().isSignalSource())
                return;

            // finally, schedule a 1-tick update
            world.scheduleTick(pos, this, 1);

            ci.cancel();
        }
    }

}