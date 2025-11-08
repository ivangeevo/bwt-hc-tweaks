package org.ivangeevo.bwt_hct.event;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.HempCropBlock;
import com.bwt.items.BwtItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static org.ivangeevo.bwt_hct.blocks.HempCropBlockManager.IS_TOP;

public class PistonBreakEventsHandler {

    public static void init() {
        PistonBreakEvents.register((world, pos, state) -> {
            if (state.isOf(BwtBlocks.hempCropBlock)) {
                if (state.get(IS_TOP)) {
                    getHempTopDrops(world, pos, state);
                } else {
                    getHempBottomDrops(world, pos, state);
                }
            }
        });
    }

    private static void getHempTopDrops(World world, BlockPos pos, BlockState state) {
        if (state.get(HempCropBlock.AGE) == HempCropBlock.MAX_AGE) {
            Block.dropStack(world, pos, new ItemStack(BwtItems.hempItem));
            if (world.getRandom().nextFloat() < 0.5f) {
                Block.dropStack(world, pos, new ItemStack(BwtItems.hempSeedsItem));
            }
        }
    }

    private static void getHempBottomDrops(World world, BlockPos pos, BlockState state) {
        if (state.get(HempCropBlock.AGE) == HempCropBlock.MAX_AGE) {
            Block.dropStack(world, pos, new ItemStack(BwtItems.hempItem));
        }

        BlockPos posUp = pos.up();
        BlockState stateAbove = world.getBlockState(posUp);

        if (stateAbove.isOf(BwtBlocks.hempCropBlock) && stateAbove.get(IS_TOP)) {
            getHempTopDrops(world, posUp, stateAbove);
        }
    }
}
