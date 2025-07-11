package org.ivangeevo.bwt_hct.mixin;

import com.bwt.blocks.HempCropBlock;
import com.bwt.blocks.SoilPlanterBlock;
import com.bwt.items.BwtItems;
import net.minecraft.block.*;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.state.property.Properties.MOISTURE;

@Mixin(AbstractBlock.class)
public abstract class AbstractBlockMixin {

    // Exclusive loot table drop conditions
    @Inject(method = "onStateReplaced", at = @At("HEAD"))
    private void dropHempItemsAdditionally(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved, CallbackInfo ci) {
        if (world.isClient || !(state.getBlock() instanceof HempCropBlock) || state.isOf(newState.getBlock())) return;
        // Only max age hemp drop loot
        if (state.get(HempCropBlock.AGE) == HempCropBlock.MAX_AGE) {
            // Case 1: Piston movement
            if (moved) {
                Block.dropStack(world, pos, new ItemStack(BwtItems.hempItem, 1));
                if (world.random.nextFloat() < 0.5f) {
                    Block.dropStack(world, pos, new ItemStack(BwtItems.hempSeedsItem, 1));
                }
                return;
            }

            // Case 2: Breaking the bottom-most hemp block causes the top part to drop as well
            BlockState above = world.getBlockState(pos.up());
            //
            boolean hasTop = above.getBlock() instanceof HempCropBlock;

            if (hasTop) {
                Block.dropStack(world, pos.up(), new ItemStack(BwtItems.hempItem, 1));
                if (world.random.nextFloat() < 0.5f) {
                    Block.dropStack(world, pos.up(), new ItemStack(BwtItems.hempSeedsItem, 1));
                }
            }

        }

    }


}

