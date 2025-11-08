package org.ivangeevo.bwt_hct.mixin.bwt;

import com.bwt.blocks.BuddyBlock;
import com.bwt.blocks.SimpleFacingBlock;
import com.bwt.tags.BwtBlockTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.ivangeevo.bwt_hct.BWT_HCTMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BuddyBlock.class)
public abstract class BuddyBlockMixin extends SimpleFacingBlock {

    @Shadow public static BooleanProperty POWERED;

    protected BuddyBlockMixin(Settings settings) {
        super(settings);
    }

    // Overrides the original buddy block behavior from BTW to make it not apply
    @Inject(method = "getStateForNeighborUpdate", at = @At("HEAD"), cancellable = true)
    private void disableCurrentNeighborUpdateState(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> cir) {
        if (BWT_HCTMod.getInstance().settings.isOldSchoolBuddyBlockNeighborUpdate())  {
            cir.setReturnValue(super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos));
        }
    }

    // Overrides buddy block neighbor update behavior to make it work the same way as it does in BTW CE
    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos,
                               Block neighborBlock, BlockPos neighborPos, boolean moved) {

        if (!BWT_HCTMod.getInstance().settings.isOldSchoolBuddyBlockNeighborUpdate()) return;

        if (world.isClient) return;

        // ignore if we’re powered or already ticking
        if (state.get(POWERED) || world.getBlockTickScheduler().isTicking(pos, this))
            return;

        // ignore redstone power updates
        if (world.getReceivedRedstonePower(pos) > 0)
            return;

        BlockState neighborState = world.getBlockState(neighborPos);

        // ignore redstone-related components by tag
        if (neighborState.isIn(BwtBlockTags.DOES_NOT_TRIGGER_BUDDY)
                || neighborBlock.getDefaultState().emitsRedstonePower())
            return;

        // finally, schedule a 1-tick update
        world.scheduleBlockTick(pos, this, 1);
    }

}
