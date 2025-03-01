package org.ivangeevo.bwt_hct.mixin;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.HempCropBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HempCropBlock.class)
public abstract class HempCropMixin extends CropBlock {

    @Shadow @Final public static BooleanProperty CONNECTED_UP;

    @Unique private static final float BASE_GROWTH_CHANCE = 0.1F;

    public HempCropMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "randomTick", at = @At("HEAD"))
    private void onRandomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        if (state.get(CONNECTED_UP)) return;

        if (world.getLightLevel(pos) < 15 && !isValidAlternateLightSourceAbove(world, pos)) return;

        // The block that the crop is planted on
        Block soilBlock = world.getBlockState(pos.down()).getBlock();
        if (soilBlock == null || !soilBlock.isBlockHydratedForPlantGrowthOn(world, pos.down())) return;

        if (state.get(AGE) < 7) {
            attemptGrowth(world, pos, state, random, soilBlock);
        } else if (world.isAir(pos.up())) {
            attemptTopGrowth(world, pos, state, random, soilBlock);
        }
    }

    @Unique
    private void attemptGrowth(World world, BlockPos pos, BlockState state, Random random, Block soilBlock) {
        float chance = BASE_GROWTH_CHANCE * soilBlock.getPlantGrowthOnMultiplier(world, pos.down(), this);
        if (random.nextFloat() <= chance) {
            incrementGrowthLevel(world, pos, state);
        }
    }

    @Unique
    private void attemptTopGrowth(World world, BlockPos pos, BlockState state, Random random, Block soilBlock) {
        float topGrowthChance = (BASE_GROWTH_CHANCE / 4F) * soilBlock.getPlantGrowthOnMultiplier(world, pos.down(), this);
        if (random.nextFloat() <= topGrowthChance) {
            world.setBlockState(pos.up(), state.with(CONNECTED_UP, true).with(AGE, 7), Block.NOTIFY_LISTENERS);
            soilBlock.notifyOfFullStagePlantGrowthOn(world, pos.down(), this);
        }
    }

    @Unique
    private void incrementGrowthLevel(World world, BlockPos pos, BlockState state) {
        int newAge = state.get(AGE) + 1;
        world.setBlockState(pos, state.with(AGE, newAge), Block.NOTIFY_LISTENERS);
        if (newAge == 7) {
            Block blockBelow = world.getBlockState(pos.down()).getBlock();
            if (blockBelow != null) {
                blockBelow.notifyOfFullStagePlantGrowthOn(world, pos.down(), this);
            }
        }
    }

    @Unique
    private boolean isValidAlternateLightSourceAbove(World world, BlockPos pos) {
        return isLitLightBlock(world, pos.up()) || isLitLightBlock(world, pos.up(2));
    }

    @Unique
    private boolean isLitLightBlock(World world, BlockPos pos) {
        return world.getBlockState(pos).equals(BwtBlocks.lightBlockBlock.getDefaultState().with(Properties.LIT, true));
    }

}
