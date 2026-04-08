package org.btwr.bwt_hct.util;

import com.bwt.items.BwtItems;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.MathHelper;
import org.btwr.bwt_hct.items.ModItems;
import org.btwr.bwt_hct.world.ModDamageTypes;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BlastingOilHelper {

    private static final float FALL_THRESHOLD = 3.0f; // Blocks
    private static final Map<UUID, Float> lastHealth = new ConcurrentHashMap<>();

    // Checks if player has any blasting oil
    public static boolean hasBlastingOil(PlayerEntity player) {
        return countBlastingOil(player) > 0;
    }

    // Counts total blasting oil stacks in inventory
    public static int countBlastingOil(PlayerEntity player) {
        int total = 0;
        for (ItemStack stack : player.getInventory().main) {
            if (stack.isOf(ModItems.blastingOil)) {
                total += stack.getCount();
            }
        }
        return total;
    }

    // Remove all blasting oil from player's inventory
    public static void removeAllBlastingOil(PlayerEntity player) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isOf(ModItems.blastingOil)) {
                player.getInventory().setStack(i, ItemStack.EMPTY);
            }
        }
    }

    // Perform explosion using BTW-style formula
    public static void explodeFromInventory(PlayerEntity player) {
        World world = player.getWorld();

        // Count ingredients
        int oilCount = countBlastingOil(player);
        int gunpowderCount = countItemInInventory(player, Items.GUNPOWDER);
        int hellfireCount = countItemInInventory(player, BwtItems.hellfireDustItem);
        int tntCount = countItemInInventory(player, Items.TNT);

        // Explosion scaling like BTW
        float explosionSize = (hellfireCount * 10f) / 64f
                + (gunpowderCount * 10f) / 64f
                + (oilCount * 10f) / 64f;

        if (tntCount > 0) {
            if (explosionSize < 4f) explosionSize = 4f;
            explosionSize += tntCount;
        }

        explosionSize = MathHelper.clamp(explosionSize, 1.5f, 10.0f);

        // Remove all blasting oil (you could also clear other ingredients if desired)
        removeAllBlastingOil(player);

        // Explosion
        world.createExplosion(player, player.getX(), player.getY(), player.getZ(), explosionSize, World.ExplosionSourceType.TRIGGER);
    }

    // Helper to count any item
    private static int countItemInInventory(PlayerEntity player, Item item) {
        int total = 0;
        for (ItemStack stack : player.getInventory().main) {
            if (stack.isOf(item)) total += stack.getCount();
        }
        return total;
    }

    public static void clearInventoryContents(PlayerEntity player) {
        PlayerInventory inventory = player.getInventory();
        for (int slot = 0; slot < inventory.size(); slot++) {
            ItemStack itemstack = inventory.getStack(slot);

            if (itemstack != null) {
                inventory.setStack(slot, ItemStack.EMPTY);
            }
        }
    }

    // Tick handler to detect fall, fire, and damage
    public static void registerTickEvents() {
        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamageTaken, damageTaken, blocked) -> {
            if (!(entity instanceof PlayerEntity player)) return;

            World world = player.getWorld();

            DamageSource blastingOilSource = new DamageSource(
                    world.getRegistryManager()
                            .getWrapperOrThrow(RegistryKeys.DAMAGE_TYPE)
                            .getOrThrow(ModDamageTypes.BLASTING_OIL)
            );

            // --- Fall damage ---
            if (entity.fallDistance > FALL_THRESHOLD && hasBlastingOil(player)) {
                explodeFromInventory(player);
                clearInventoryContents(player);
                player.damage(blastingOilSource, Float.MAX_VALUE);
            }

            // --- Fire or lava ---
            if ((player.isOnFire() || player.getBlockStateAtPos().isOf(Blocks.LAVA)) && hasBlastingOil(player)) {
                explodeFromInventory(player);
                clearInventoryContents(player);
                player.damage(blastingOilSource, Float.MAX_VALUE);
            }

            // --- Damage detection ---
            float prevHealth = lastHealth.getOrDefault(player.getUuid(), player.getHealth());
            if (player.getHealth() < prevHealth && hasBlastingOil(player)) {
                explodeFromInventory(player);
                clearInventoryContents(player);
                player.damage(blastingOilSource, Float.MAX_VALUE);
            }
            lastHealth.put(player.getUuid(), player.getHealth());
        });

        // Clear map on server tick to avoid memory leaks for offline players
        ServerTickEvents.END_SERVER_TICK.register(server -> lastHealth.keySet().removeIf(uuid -> server.getPlayerManager().getPlayer(uuid) == null));
    }
}