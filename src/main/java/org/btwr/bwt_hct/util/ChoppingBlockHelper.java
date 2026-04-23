package org.btwr.bwt_hct.util;

import com.bwt.blocks.BwtBlocks;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.btwr.bwt_hct.blocks.ModBlocks;
import org.btwr.bwt_hct.tag.ModTags;
import org.btwr.shared_library.util.HeadDropRegistry;

public class ChoppingBlockHelper {

    public static void registerEntityEvents() {
        ServerLivingEntityEvents.ALLOW_DEATH.register(((entity, damageSource, damageAmount) -> {
            if (!(entity.getWorld() instanceof ServerWorld serverWorld)) return true;

            if (!entity.getType().isIn(ModTags.EntityTypes.INCREASED_SKULL_DROP_RATE_FROM_CHOPPING_BLOCK)) return true;

            BlockPos pos = entity.getBlockPos();

            boolean hasSawAdjacent = isSawAdjacent(serverWorld, pos);
            boolean hasChoppingBlock = isChoppingBlockAdjacent(serverWorld, pos);

            if (hasSawAdjacent && hasChoppingBlock) {
                dropSkull(entity, serverWorld);
            }

            return true; // don't cancel death
        }));
    }

    public static boolean isSawAdjacent(World world, BlockPos pos) {
        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos neighbor = pos.offset(direction);

            if (world.getBlockState(neighbor).isOf(BwtBlocks.sawBlock)) {
                return true;
            }

            // head level
            if (world.getBlockState(neighbor.up()).isOf(BwtBlocks.sawBlock)) {
                return true;
            }

        }
        return false;
    }

    public static boolean isChoppingBlockAdjacent(World world, BlockPos pos) {
        // Check the block itself and directly above (mob could be standing on it)
        return world.getBlockState(pos).isOf(ModBlocks.choppingBlock)
                || world.getBlockState(pos.up()).isOf(ModBlocks.choppingBlock)
                || world.getBlockState(pos.down()).isOf(ModBlocks.choppingBlock);
    }

    private static void dropSkull(LivingEntity entity, ServerWorld world) {
        boolean isBTWRCoreLoaded = FabricLoader.getInstance().isModLoaded("bwtr");

        ItemStack skull = HeadDropRegistry.getHeadForEntity(entity);
        if (skull.isEmpty()) return;

        // ~25% chance when BTWR:Core is not loaded. If it is, then we use its own dropping logic
        if (!isBTWRCoreLoaded && world.getRandom().nextFloat() < 0.25f) {
            entity.dropStack(skull);
        }
    }

}