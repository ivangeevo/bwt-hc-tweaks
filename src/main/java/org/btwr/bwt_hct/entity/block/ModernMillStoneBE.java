package org.btwr.bwt_hct.entity.block;

import com.bwt.blocks.mill_stone.MillStoneBlock;
import com.bwt.utils.OrderedRecipeMatcher;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.btwr.bwt_hct.blocks.ModBlocks;
import org.btwr.bwt_hct.entity.ModBlockEntities;
import org.btwr.bwt_hct.recipes.mill_stone.ModernMillStoneRecipe;
import org.btwr.bwt_hct.recipes.mill_stone.SingleCountMillStoneRecipeInput;
import org.btwr.bwt_hct.util.SingleCountInventory;
import org.btwr.bwt_hct.util.SingleCountStorage;

import java.util.*;

import static org.btwr.bwt_hct.blocks.blocks.ModernMillStoneBlock.FULL;

public class ModernMillStoneBE extends BlockEntity implements Container {

    protected int grindProgressTime;
    public static final int timeToGrind = 200;

    public final ModernMillStoneBE.Inventory inventory = new Inventory();
    //public final InventoryStorage inventoryWrapper = InventoryStorage.of(inventory, Direction.UP);
    public final Storage<ItemVariant> inventoryWrapper = new SingleCountStorage(inventory);

    final RecipeManager.CachedCheck<SingleCountMillStoneRecipeInput, ModernMillStoneRecipe> matchGetter =
            RecipeManager.createCheck(ModernMillStoneRecipe.Type.INSTANCE);

    public ModernMillStoneBE(BlockPos pos, BlockState state) {
        super(ModBlockEntities.modernMillStoneEntity, pos, state);
    }

    public boolean onUseByPlayer(Player player) {
        ItemStack held = player.getMainHandItem();

        // Trying to retrieve item
        if (!inventory.isEmpty()) {
            retrieveItem(level, player);
            return true;
        }

        // Try inserting if inventory is empty and player is holding something or recipe item
        if (inventory.isEmpty() && !held.isEmpty() && getRecipeFor(held).isPresent()) {
            ItemStack inserted = held.copyWithCount(1);
            setItem(0, inserted);
            held.shrink(1);
            assert level != null;
            this.setFull(level, true);
            return true;
        }

        return false; // Nothing happened
    }

    public static void tick(Level world, BlockPos pos, BlockState state, ModernMillStoneBE blockEntity) {
        if (!state.is(ModBlocks.modernMillStoneBlock) || !state.getValue(MillStoneBlock.MECH_POWERED)) {
            return;
        }
        SingleCountMillStoneRecipeInput recipeInput = new SingleCountMillStoneRecipeInput(blockEntity.inventory.getItems());
        List<RecipeHolder<ModernMillStoneRecipe>> matches = world.getRecipeManager().getRecipesFor(ModernMillStoneRecipe.Type.INSTANCE, recipeInput, world);
        if (matches.isEmpty()) {
            if (blockEntity.grindProgressTime != 0) {
                blockEntity.grindProgressTime = 0;
                blockEntity.setChanged();
            }
            return;
        }

        blockEntity.grindProgressTime += 1;

        world.setBlockAndUpdate(pos, state.setValue(FULL, true));

        if (blockEntity.grindProgressTime >= timeToGrind) {
            blockEntity.grindProgressTime = 0;
            world.setBlockAndUpdate(pos, state.setValue(FULL, false));
            blockEntity.setChanged();
        }
        else {
            return;
        }

        // Get the first recipe and grind it
        OrderedRecipeMatcher.getFirstRecipe(matches, blockEntity.inventory.getItems(), match -> blockEntity.completeRecipe(match, world, pos));
    }

    public Optional<RecipeHolder<ModernMillStoneRecipe>> getRecipeFor(ItemStack stack) {
        if (stack.isEmpty()) {
            return Optional.empty();
        }

        return this.matchGetter.getRecipeFor(new SingleCountMillStoneRecipeInput(Collections.singletonList(stack)), this.level);
    }

    public boolean completeRecipe(ModernMillStoneRecipe recipe, Level world, BlockPos pos) {
        try (Transaction transaction = Transaction.openOuter()) {
            // Spend ingredients
                ItemVariant itemVariant = StorageUtil.findStoredResource(inventoryWrapper, input -> recipe.getIngredients().getFirst().test(input.toStack()));
                long taken = inventoryWrapper.extract(itemVariant, 1, transaction);

                if (taken == 0) {
                    transaction.abort();
                    return false;
                }

            // Eject results
            for (ItemStack result : recipe.getResults()) {
                ejectItem(world, result, pos);
            }
            transaction.commit();
            return true;
        }
    }

    public boolean addItem(ItemStack stack) {
        // Insert a single item (already validated)
        try (Transaction tx = Transaction.openOuter()) {
            if (inventoryWrapper.insert(ItemVariant.of(stack), 1L, tx) == stack.getCount()) {
                tx.commit();
                return true;
            }
        }
        return false;
    }

    public void retrieveItem(Level world, Player player) {
        try (Transaction tx = Transaction.openOuter()) {
            ItemVariant variant = ItemVariant.of(inventory.getItem(0));
            long extracted = inventoryWrapper.extract(variant, 1, tx);
            if (extracted != 0L) {
                player.getInventory().placeItemBackInInventory(variant.toStack());
                this.setFull(world, false);
                tx.commit();
            }
        }
    }

    public static void ejectItem(Level world, ItemStack stack, BlockPos pos) {
        // Start at the center of the block
        Vec3 centerPos = pos.getCenter();
        Vec3 horizontalUnitVector = new Vec3(1, 0, 1);

        // Pick a random direction
        double angle = Math.toRadians(world.random.nextIntBetweenInclusive(0, 359));
        // Get distance from the center to the edge of a square, using the angle
        double distToEdge = Math.min(0.5 / Math.abs(Math.cos(angle)), 0.5 / Math.abs(Math.sin(angle)));
        // Apply that distance to get our item spawn position
        Vec3 itemPos = horizontalUnitVector
                .yRot((float) angle)
                .scale(distToEdge + 0.01)
                .add(centerPos);
        // Velocity is in the same X/Z direction as position, but with random strength and y offset
        Vec3 itemVelocity = horizontalUnitVector
                .yRot((float) angle)
                .scale(world.random.nextFloat() * 0.0125D + 0.1F)
                .add(0, world.random.nextGaussian() * 0.0125D + 0.05F, 0);

        ItemEntity itemEntity = new ItemEntity(world, itemPos.x(), itemPos.y(), itemPos.z(), stack);
        itemEntity.setDeltaMovement(itemVelocity);
        world.addFreshEntity(itemEntity);
    }

    private void setFull(Level world, boolean value) {
        world.setBlockAndUpdate(worldPosition, world.getBlockState(worldPosition).setValue(FULL, value));
        this.updateListeners();
    }

    private void updateListeners() {
        this.setChanged();
        this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.loadAdditional(nbt, registryLookup);
        this.inventory.fromTag(nbt.getList("Inventory", Tag.TAG_COMPOUND), registryLookup);
        this.grindProgressTime = nbt.getInt("grindProgressTime");
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.saveAdditional(nbt, registryLookup);
        nbt.put("Inventory", this.inventory.createTag(registryLookup));
        nbt.putInt("grindProgressTime", this.grindProgressTime);
    }

    @Override
    public int getContainerSize() {
        return inventory.getContainerSize();
    }

    @Override
    public boolean isEmpty() {
        return inventory.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return inventory.getItem(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return inventory.removeItem(slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return inventory.removeItemNoUpdate(slot);
    }

    @Override
    public int getMaxStackSize() {
        return inventory.getMaxStackSize();
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        inventory.setItem(slot, stack);
    }

    @Override
    public boolean stillValid(Player player) {
        return inventory.stillValid(player);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return inventory.getMaxStackSize(stack);
    }

    @Override
    public void clearContent() {
        inventory.clearContent();
    }

    public class Inventory extends SingleCountInventory {
        public Inventory() {
            super();
        }

        public void setChanged() {
            ModernMillStoneBE.this.setChanged();
        }
    }

}