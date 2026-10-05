package org.btwr.bwt_hct.util;

import com.bwt.items.BwtItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.btwr.bwt_hct.items.ModItems;
import org.btwr.bwt_hct.world.ModDamageTypes;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BlastingOilHelper {

    private static final float FALL_THRESHOLD = 3.0f; // Blocks
    private static final Map<UUID, Float> lastHealth = new ConcurrentHashMap<>();

    // Tick handler to detect fall, fire, and damage
    public static void tickBlastingOil(LivingEntity entity, DamageSource damageSource, float baseDamageTaken, float damageTaken, boolean blocked) {
        if (!(entity instanceof Player player)) return;

        Level world = player.level();

        DamageSource blastingOilSource = new DamageSource(
                world.registryAccess()
                        .lookupOrThrow(Registries.DAMAGE_TYPE)
                        .getOrThrow(ModDamageTypes.BLASTING_OIL)
        );

        // --- Fall damage ---
        if (entity.fallDistance > FALL_THRESHOLD && hasBlastingOil(player)) {
            explodeFromInventory(player);
            clearInventoryContents(world, player);
            player.hurt(blastingOilSource, Float.MAX_VALUE);
        }

        // --- Fire or lava ---
        if ((player.isOnFire() || player.getInBlockState().is(Blocks.LAVA)) && hasBlastingOil(player)) {
            explodeFromInventory(player);
            clearInventoryContents(world, player);
            player.hurt(blastingOilSource, Float.MAX_VALUE);
        }

        // --- Damage detection ---
        float prevHealth = lastHealth.getOrDefault(player.getUUID(), player.getHealth());
        if (player.getHealth() < prevHealth && hasBlastingOil(player)) {
            explodeFromInventory(player);
            clearInventoryContents(world, player);
            player.hurt(blastingOilSource, Float.MAX_VALUE);
        }
        lastHealth.put(player.getUUID(), player.getHealth());
    }

    // Clear map on server tick to avoid memory leaks for offline players
    public static void clearBlastingOilMap(MinecraftServer server) {
        lastHealth.keySet().removeIf(
                uuid -> server.getPlayerList().getPlayer(uuid) == null
        );
    }

    // Checks if player has any blasting oil
    private static boolean hasBlastingOil(Player player) {
        return countBlastingOil(player) > 0;
    }

    // Counts total blasting oil stacks in inventory
    private static int countBlastingOil(Player player) {
        int total = 0;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(ModItems.blastingOil)) {
                total += stack.getCount();
            }
        }
        return total;
    }

    // Remove all blasting oil from player's inventory
    private static void removeAllBlastingOil(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(ModItems.blastingOil)) {
                player.getInventory().setItem(i, ItemStack.EMPTY);
            }
        }
    }

    // Perform explosion using BTW-style formula
    private static void explodeFromInventory(Player player) {
        Level world = player.level();

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

        explosionSize = Mth.clamp(explosionSize, 1.5f, 10.0f);

        // Remove all blasting oil (you could also clear other ingredients if desired)
        removeAllBlastingOil(player);

        // Explosion
        world.explode(player, player.getX(), player.getY(), player.getZ(), explosionSize, Level.ExplosionInteraction.TRIGGER);
    }

    // Helper to count any item
    private static int countItemInInventory(Player player, Item item) {
        int total = 0;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(item)) total += stack.getCount();
        }
        return total;
    }

    private static void clearInventoryContents(Level world, Player player) {
        Inventory inventory = player.getInventory();

        // Don't clear if keep inventory is enabled
        if (world.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) return;

        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack itemstack = inventory.getItem(slot);

            if (itemstack != null) {
                inventory.setItem(slot, ItemStack.EMPTY);
            }
        }
    }

}