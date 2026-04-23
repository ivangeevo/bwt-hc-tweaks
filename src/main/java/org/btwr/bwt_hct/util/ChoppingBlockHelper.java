package org.btwr.bwt_hct.util;

import com.bwt.blocks.BwtBlocks;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.btwr.bwt_hct.blocks.ModBlocks;
import org.btwr.bwt_hct.tag.ModTags;

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

    private static boolean isSawAdjacent(World world, BlockPos pos) {
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

    private static boolean isChoppingBlockAdjacent(World world, BlockPos pos) {
        // Check the block itself and directly above (mob could be standing on it)
        return world.getBlockState(pos).isOf(ModBlocks.choppingBlock)
                || world.getBlockState(pos.up()).isOf(ModBlocks.choppingBlock)
                || world.getBlockState(pos.down()).isOf(ModBlocks.choppingBlock);
    }

    private static void dropSkull(LivingEntity entity, ServerWorld world) {
        ItemStack skull = getSkullForEntity(entity);
        if (skull == null) return;

        // ~25% chance, same as BTW behavior
        if (world.getRandom().nextFloat() < 0.25f) {
            entity.dropStack(skull);
        }
    }

    private static ItemStack getSkullForEntity(LivingEntity entity) {
        if (!entity.getType().isIn(ModTags.EntityTypes.INCREASED_SKULL_DROP_RATE_FROM_CHOPPING_BLOCK)) {
            return ItemStack.EMPTY;
        }

        if (entity.getType() == EntityType.CREEPER) return new ItemStack(Items.CREEPER_HEAD);
        if (entity.getType() == EntityType.SKELETON) return new ItemStack(Items.SKELETON_SKULL);
        if (entity.getType() == EntityType.ZOMBIE) return new ItemStack(Items.ZOMBIE_HEAD);
        if (entity.getType() == EntityType.WITHER_SKELETON) return new ItemStack(Items.WITHER_SKELETON_SKULL);

        return null;
    }

}