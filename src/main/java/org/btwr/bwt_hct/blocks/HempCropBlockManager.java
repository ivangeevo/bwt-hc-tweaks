package org.btwr.bwt_hct.blocks;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.HempCropBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.btwr.shared_library.tag.BTWRConventionalTags;

public class HempCropBlockManager {

    private static final float BASE_GROWTH_CHANCE = 0.1F;
    private static final IntProperty AGE = HempCropBlock.AGE;
    public static final BooleanProperty IS_TOP = BooleanProperty.of("is_top");

    private static final HempCropBlockManager INSTANCE = new HempCropBlockManager();
    private HempCropBlockManager() {}
    public static HempCropBlockManager getInstance() {
        return INSTANCE;
    }

    public void onRandomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, Block hemp) {
        if (!world.isSkyVisible(pos) && world.getLightLevel(pos) < 15 && !isValidAlternateLightSourceAbove(world, pos)) return;

        // The block that the crop is planted on
        Block soilBlock = world.getBlockState(pos.down()).getBlock();
        if (soilBlock == null) return;

        if (soilBlock.btwr$isBlockHydratedForPlantGrowthOn(world, pos.down()) || soilBlock.getDefaultState().isIn(BTWRConventionalTags.Blocks.ALWAYS_FERTILE_SOIL)) {
            if (state.get(AGE) < 7) {
                attemptGrowth(world, pos, state, random, soilBlock, hemp);
            } else if (world.isAir(pos.up())) {
                attemptTopGrowth(world, pos, state, random, soilBlock, hemp);
            }
        }
    }

    private void attemptGrowth(World world, BlockPos pos, BlockState state, Random random, Block soilBlock, Block hemp) {
        float chance = BASE_GROWTH_CHANCE * soilBlock.btwr$getPlantGrowthOnMultiplier(world, pos.down(), hemp);
        if (random.nextFloat() <= chance) {
            incrementGrowthLevel(world, pos, state, hemp);
        }
    }

    private void attemptTopGrowth(World world, BlockPos pos, BlockState state, Random random, Block soilBlock, Block hemp) {
        float topGrowthChance = (BASE_GROWTH_CHANCE / 4F) * soilBlock.btwr$getPlantGrowthOnMultiplier(world, pos.down(), hemp);
        if (random.nextFloat() <= topGrowthChance) {
            world.setBlockState(pos.up(), state.with(IS_TOP, true).with(AGE, 7), Block.NOTIFY_LISTENERS);
            soilBlock.btwr$notifyOfFullStagePlantGrowthOn(world, pos.down(), hemp);
        }
    }

    private void incrementGrowthLevel(World world, BlockPos pos, BlockState state, Block hemp) {
        int newAge = state.get(AGE) + 1;
        world.setBlockState(pos, state.with(AGE, newAge), Block.NOTIFY_LISTENERS);
        if (newAge == 7) {
            Block blockBelow = world.getBlockState(pos.down()).getBlock();
            if (blockBelow != null) {
                blockBelow.btwr$notifyOfFullStagePlantGrowthOn(world, pos.down(), hemp);
            }
        }
    }

    private boolean isValidAlternateLightSourceAbove(World world, BlockPos pos) {
        return isLitLightBlock(world, pos.up()) || isLitLightBlock(world, pos.up(2));
    }

    private boolean isLitLightBlock(World world, BlockPos pos) {
        return world.getBlockState(pos).equals(BwtBlocks.lightBlockBlock.getDefaultState().with(Properties.LIT, true));
    }

}