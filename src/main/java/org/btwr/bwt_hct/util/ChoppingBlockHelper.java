package org.btwr.bwt_hct.util;

import com.bwt.blocks.BwtBlocks;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.btwr.bwt_hct.blocks.ModBlocks;
import org.btwr.bwt_hct.tag.ModTags;
import org.btwr.shared_library.api.registry.HeadDropRegistry;

public class ChoppingBlockHelper {

    @SuppressWarnings("SameReturnValue")
    public static boolean tryDroppingSkull(LivingEntity entity, DamageSource damageSource, float damageAmount) {
        if (!(entity.level() instanceof ServerLevel serverWorld)) return true;

        if (!entity.getType().is(ModTags.EntityTypes.INCREASED_SKULL_DROP_RATE_FROM_CHOPPING_BLOCK)) return true;

        BlockPos pos = entity.blockPosition();

        boolean hasSawAdjacent = isSawAdjacent(serverWorld, pos);
        boolean hasChoppingBlock = isChoppingBlockAdjacent(serverWorld, pos);

        if (hasSawAdjacent && hasChoppingBlock) {
            dropSkull(entity, serverWorld);
        }

        return true; // don't cancel death
    }

    private static boolean isSawAdjacent(Level world, BlockPos pos) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos neighbor = pos.relative(direction);

            if (world.getBlockState(neighbor).is(BwtBlocks.sawBlock)) {
                return true;
            }

            // head level
            if (world.getBlockState(neighbor.above()).is(BwtBlocks.sawBlock)) {
                return true;
            }

        }
        return false;
    }

    private static boolean isChoppingBlockAdjacent(Level world, BlockPos pos) {
        // Check the block itself and directly above (mob could be standing on it)
        return world.getBlockState(pos).is(ModBlocks.choppingBlock)
                || world.getBlockState(pos.above()).is(ModBlocks.choppingBlock)
                || world.getBlockState(pos.below()).is(ModBlocks.choppingBlock);
    }

    private static void dropSkull(LivingEntity entity, ServerLevel world) {
        boolean isBTWRCoreLoaded = FabricLoader.getInstance().isModLoaded("btwr");

        ItemStack skull = HeadDropRegistry.getHeadForEntity(entity);
        if (skull.isEmpty()) return;

        // ~25% chance when BTWR:Core is not loaded. If it is, then we use its own dropping logic
        if (!isBTWRCoreLoaded && world.getRandom().nextFloat() < 0.25f) {
            entity.spawnAtLocation(skull);
        }
    }

}