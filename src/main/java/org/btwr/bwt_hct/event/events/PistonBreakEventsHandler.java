package org.btwr.bwt_hct.event.events;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.HempCropBlock;
import com.bwt.items.BwtItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.btwr.bwt_hct.util.PistonBreakHempHelper;

import static org.btwr.bwt_hct.blocks.HempCropBlockManager.IS_TOP;

public class PistonBreakEventsHandler {

    public static void init() {
        PistonBreakEvents.register((world, pos, state) -> {
            if (state.is(BwtBlocks.hempCropBlock)) {
                if (state.getValue(IS_TOP)) {
                    getHempTopDrops(world, pos, state);
                } else {
                    getHempBottomDrops(world, pos, state);
                }
            }
        });
    }

    private static void getHempTopDrops(Level world, BlockPos pos, BlockState state) {
        if (state.getValue(HempCropBlock.AGE) == HempCropBlock.MAX_AGE) {
            Block.popResource(world, pos, new ItemStack(BwtItems.hempItem));
            if (world.getRandom().nextFloat() < 0.5f) {
                Block.popResource(world, pos, new ItemStack(BwtItems.hempSeedsItem));
            }
        }
    }

    private static void getHempBottomDrops(Level world, BlockPos pos, BlockState state) {
        if (state.getValue(HempCropBlock.AGE) == HempCropBlock.MAX_AGE) {
            Block.popResource(world, pos, new ItemStack(BwtItems.hempItem));
        }

        BlockPos posUp = pos.above();
        BlockState stateAbove = world.getBlockState(posUp);

        if (stateAbove.is(BwtBlocks.hempCropBlock) && stateAbove.getValue(IS_TOP)) {
            getHempTopDrops(world, posUp, stateAbove);
        }
    }

}