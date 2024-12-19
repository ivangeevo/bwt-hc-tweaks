package org.ivangeevo.bwt_hct.entity.block;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.mill_stone.MillStoneBlock;
import com.bwt.recipes.BwtRecipes;
import com.bwt.recipes.IngredientWithCount;
import com.bwt.recipes.mill_stone.MillStoneRecipe;
import com.bwt.recipes.mill_stone.MillStoneRecipeInput;
import com.bwt.utils.OrderedRecipeMatcher;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.ivangeevo.bwt_hct.block.ModBlocks;
import org.ivangeevo.bwt_hct.block.blocks.ModernMillStoneBlock;
import org.ivangeevo.bwt_hct.entity.ModBlockEntities;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.ivangeevo.bwt_hct.block.blocks.ModernMillStoneBlock.FULL;

public class ModernMillstoneBE extends BlockEntity implements NamedScreenHandlerFactory, Inventory
{
    protected int grindProgressTime;
    public static final int timeToGrind = 200;
    protected static final int INVENTORY_SIZE = 1;

    public final ModernMillstoneBE.Inventory inventory = new Inventory(INVENTORY_SIZE);
    public final InventoryStorage inventoryWrapper = InventoryStorage.of(inventory, null);
    final RecipeManager.MatchGetter<MillStoneRecipeInput, MillStoneRecipe> matchGetter = RecipeManager.createCachedMatchGetter(BwtRecipes.MILL_STONE_RECIPE_TYPE);

    public ModernMillstoneBE(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MODERN_MILLSTONE, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, ModernMillstoneBE blockEntity) {
        if (!state.isOf(ModBlocks.modernMillStoneBlock) || !state.get(MillStoneBlock.MECH_POWERED)) {
            return;
        }
        MillStoneRecipeInput recipeInput = new MillStoneRecipeInput(blockEntity.inventory.getHeldStacks());
        List<RecipeEntry<MillStoneRecipe>> matches = world.getRecipeManager().getAllMatches(BwtRecipes.MILL_STONE_RECIPE_TYPE, recipeInput, world);
        if (matches.isEmpty()) {
            if (blockEntity.grindProgressTime != 0) {
                blockEntity.grindProgressTime = 0;
                blockEntity.markDirty();
            }
            return;
        }

        blockEntity.grindProgressTime += 1;
        // TODO: Setting the FULL state like this might not be ideal.
        // theres slight flicker when states change on item insertion from hopper
        world.setBlockState(pos, state.with(FULL, true));
        if (blockEntity.grindProgressTime >= timeToGrind) {
            blockEntity.grindProgressTime = 0;
            world.setBlockState(pos, state.with(FULL, false));
            blockEntity.markDirty();
        }
        else {
            return;
        }

        // Get the first recipe and grind it
        OrderedRecipeMatcher.getFirstRecipe(matches, blockEntity.inventory.getHeldStacks(), match -> blockEntity.completeRecipe(match, world, pos));
    }

    public boolean completeRecipe(MillStoneRecipe recipe, World world, BlockPos pos) {
        try (Transaction transaction = Transaction.openOuter()) {
            // Spend ingredients
            for (IngredientWithCount ingredientWithCount : recipe.getIngredientsWithCount()) {
                long countToSpend = ingredientWithCount.count();
                while (countToSpend > 0) {
                    ItemVariant itemVariant = StorageUtil.findStoredResource(inventoryWrapper, ingredientWithCount::test);
                    if (itemVariant == null) {
                        continue;
                    }
                    long taken = inventoryWrapper.extract(itemVariant, countToSpend, transaction);
                    countToSpend -= taken;
                    if (taken == 0) {
                        transaction.abort();
                        return false;
                    }
                }
            }
            // Eject results
            for (ItemStack result : recipe.getResults()) {
                ejectItem(world, result, pos);
            }
            transaction.commit();
            return true;
        }
    }

    public static void ejectItem(World world, ItemStack stack, BlockPos pos) {
        // Start at the center of the block
        Vec3d centerPos = pos.toCenterPos();
        Vec3d horizontalUnitVector = new Vec3d(1, 0, 1);

        // Pick a random direction
        double angle = Math.toRadians(world.random.nextBetween(0, 359));
        // Get distance from the center to the edge of a square, using the angle
        double distToEdge = Math.min(0.5 / Math.abs(Math.cos(angle)), 0.5 / Math.abs(Math.sin(angle)));
        // Apply that distance to get our item spawn position
        Vec3d itemPos = horizontalUnitVector
                .rotateY((float) angle)
                .multiply(distToEdge + 0.01)
                .add(centerPos);
        // Velocity is in the same X/Z direction as position, but with random strength and y offset
        Vec3d itemVelocity = horizontalUnitVector
                .rotateY((float) angle)
                .multiply(world.random.nextFloat() * 0.0125D + 0.1F)
                .add(0, world.random.nextGaussian() * 0.0125D + 0.05F, 0);

        ItemEntity itemEntity = new ItemEntity(world, itemPos.getX(), itemPos.getY(), itemPos.getZ(), stack);
        itemEntity.setVelocity(itemVelocity);
        world.spawnEntity(itemEntity);
    }

    public Optional<RecipeEntry<MillStoneRecipe>> getRecipeFor(ItemStack stack)
    {
        if (stack.isEmpty()) {
            return Optional.empty();
        }

        return this.matchGetter.getFirstMatch(new MillStoneRecipeInput(Collections.singletonList(stack)), this.world);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.inventory.readNbtList(nbt.getList("Inventory", NbtElement.COMPOUND_TYPE), registryLookup);
        this.grindProgressTime = nbt.getInt("grindProgressTime");
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.put("Inventory", this.inventory.toNbtList(registryLookup));
        nbt.putInt("grindProgressTime", this.grindProgressTime);
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return null;
    }

    public boolean addItem(Entity user, ItemStack stack) {
        this.inventory.setStack(0, stack.copyWithCount(1));
        this.getWorld().emitGameEvent(GameEvent.BLOCK_CHANGE, this.getPos(), GameEvent.Emitter.of(user, this.getCachedState()));
        world.setBlockState(pos, world.getBlockState(pos).with(FULL, true));
        this.updateListeners();
        stack.decrement(1);

        return true;
    }

    public void retrieveItem(World world, PlayerEntity player) {
        ItemStack inputStack = inventory.getStack(0);

        if (!inputStack.isEmpty() && !world.isClient()) {
            boolean addedToInventory = player.giveItemStack(inputStack);
            if (!addedToInventory) {
                player.dropItem(inputStack, false);
            }
            setStack(0, ItemStack.EMPTY);
            world.setBlockState(pos, world.getBlockState(pos).with(FULL, false));
            markDirty();
            Objects.requireNonNull(this.getWorld()).updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), Block.NOTIFY_ALL);
        }
    }

    private void updateListeners() {
        this.markDirty();
        this.getWorld().updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
    }


    @Override
    public Text getDisplayName() {
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
    }

    @Override
    public int size() {
        return inventory.size();
    }

    @Override
    public boolean isEmpty() {
        return inventory.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return inventory.getStack(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return inventory.removeStack(slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return inventory.removeStack(slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        inventory.setStack(slot, stack);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return inventory.canPlayerUse(player);
    }

    @Override
    public void clear() {
        inventory.clear();
    }

    public class Inventory extends SimpleInventory
    {
        public Inventory(int size) {
            super(size);
        }
        @Override
        public void markDirty() {
            ModernMillstoneBE.this.markDirty();
        }
    }
}
