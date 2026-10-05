package org.btwr.bwt_hct.blocks;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.HempCropBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.btwr.shared_library.api.tag.BTWRConventionalTags;

public class HempCropBlockManager {

    private static final float BASE_GROWTH_CHANCE = 0.1F;
    private static final IntegerProperty AGE = HempCropBlock.AGE;
    public static final BooleanProperty IS_TOP = BooleanProperty.create("is_top");

    private static final HempCropBlockManager INSTANCE = new HempCropBlockManager();
    private HempCropBlockManager() {}
    public static HempCropBlockManager getInstance() {
        return INSTANCE;
    }

    public void onRandomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random, Block hemp) {
        if (!world.canSeeSky(pos) && world.getMaxLocalRawBrightness(pos) < 15 && !isValidAlternateLightSourceAbove(world, pos)) return;

        // The block that the crop is planted on
        Block soilBlock = world.getBlockState(pos.below()).getBlock();
        if (soilBlock == null) return;

        if (soilBlock.btwr$isBlockHydratedForPlantGrowthOn(world, pos.below()) || soilBlock.defaultBlockState().is(BTWRConventionalTags.Blocks.ALWAYS_FERTILE_SOIL)) {
            if (state.getValue(AGE) < 7) {
                attemptGrowth(world, pos, state, random, soilBlock, hemp);
            } else if (world.isEmptyBlock(pos.above())) {
                attemptTopGrowth(world, pos, state, random, soilBlock, hemp);
            }
        }
    }

    private void attemptGrowth(Level world, BlockPos pos, BlockState state, RandomSource random, Block soilBlock, Block hemp) {
        float chance = BASE_GROWTH_CHANCE * soilBlock.btwr$getPlantGrowthOnMultiplier(world, pos.below(), hemp);
        if (random.nextFloat() <= chance) {
            incrementGrowthLevel(world, pos, state, hemp);
        }
    }

    private void attemptTopGrowth(Level world, BlockPos pos, BlockState state, RandomSource random, Block soilBlock, Block hemp) {
        float topGrowthChance = (BASE_GROWTH_CHANCE / 4F) * soilBlock.btwr$getPlantGrowthOnMultiplier(world, pos.below(), hemp);
        if (random.nextFloat() <= topGrowthChance) {
            world.setBlock(pos.above(), state.setValue(IS_TOP, true).setValue(AGE, 7), Block.UPDATE_CLIENTS);
            soilBlock.btwr$notifyOfFullStagePlantGrowthOn(world, pos.below(), hemp);
        }
    }

    private void incrementGrowthLevel(Level world, BlockPos pos, BlockState state, Block hemp) {
        int newAge = state.getValue(AGE) + 1;
        world.setBlock(pos, state.setValue(AGE, newAge), Block.UPDATE_CLIENTS);
        if (newAge == 7) {
            Block blockBelow = world.getBlockState(pos.below()).getBlock();
            if (blockBelow != null) {
                blockBelow.btwr$notifyOfFullStagePlantGrowthOn(world, pos.below(), hemp);
            }
        }
    }

    private boolean isValidAlternateLightSourceAbove(Level world, BlockPos pos) {
        return isLitLightBlock(world, pos.above()) || isLitLightBlock(world, pos.above(2));
    }

    private boolean isLitLightBlock(Level world, BlockPos pos) {
        return world.getBlockState(pos).equals(BwtBlocks.lightBlockBlock.defaultBlockState().setValue(BlockStateProperties.LIT, true));
    }

}