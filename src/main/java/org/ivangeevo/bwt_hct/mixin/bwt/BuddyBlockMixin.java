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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BuddyBlock.class)
public abstract class BuddyBlockMixin extends SimpleFacingBlock {

    @Shadow public static BooleanProperty POWERED;

    protected BuddyBlockMixin(Settings settings) {
        super(settings);
    }

    // Overrides buddy block neighbor update behavior to make it work the same way as it does in BTW CE
    @Inject(method = "neighborUpdate", at = @At("HEAD"), cancellable = true)
    private void onNeighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify, CallbackInfo ci) {
        if (!BWT_HCTMod.getInstance().settings.isOldSchoolBuddyBlockNeighborUpdate()) return;

        if (world.isClient) return;

        // ignore if we’re powered or already ticking
        if (state.get(POWERED) || world.getBlockTickScheduler().isTicking(pos, this))
            return;

        // ignore redstone power updates
        if (world.getReceivedRedstonePower(pos) > 0)
            return;

        BlockState neighborState = world.getBlockState(sourcePos);

        // ignore redstone-related components by tag
        if (neighborState.isIn(BwtBlockTags.DOES_NOT_TRIGGER_BUDDY)
                || sourceBlock.getDefaultState().emitsRedstonePower())
            return;

        // finally, schedule a 1-tick update
        world.scheduleBlockTick(pos, this, 1);

        ci.cancel();
    }

}