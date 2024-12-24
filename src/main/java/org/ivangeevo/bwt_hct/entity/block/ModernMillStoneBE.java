package org.ivangeevo.bwt_hct.entity.block;

import com.bwt.blocks.mill_stone.MillStoneBlock;
import com.bwt.utils.OrderedRecipeMatcher;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.ivangeevo.bwt_hct.block.ModBlocks;
import org.ivangeevo.bwt_hct.entity.ModBlockEntities;
import org.ivangeevo.bwt_hct.recipes.mill_stone.ModernMillStoneRecipe;
import org.ivangeevo.bwt_hct.recipes.mill_stone.SingleCountMillStoneRecipeInput;

import java.util.*;

import static org.ivangeevo.bwt_hct.block.blocks.ModernMillStoneBlock.FULL;

public class ModernMillStoneBE extends BlockEntity implements Inventory {
    protected int grindProgressTime;
    public static final int timeToGrind = 200;

    public final ModernMillStoneBE.Inventory inventory = new ModernMillStoneBE.Inventory(1);

    public final InventoryStorage inventoryWrapper = InventoryStorage.of(inventory, Direction.UP);

    final RecipeManager.MatchGetter<SingleCountMillStoneRecipeInput, ModernMillStoneRecipe> matchGetter =
            RecipeManager.createCachedMatchGetter(ModernMillStoneRecipe.Type.INSTANCE);

    public ModernMillStoneBE(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MODERN_MILLSTONE, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, ModernMillStoneBE blockEntity) {
        if (!state.isOf(ModBlocks.modernMillStoneBlock) || !state.get(MillStoneBlock.MECH_POWERED)) {
            return;
        }
        SingleCountMillStoneRecipeInput recipeInput = new SingleCountMillStoneRecipeInput(blockEntity.inventory.getHeldStacks());
        List<RecipeEntry<ModernMillStoneRecipe>> matches = world.getRecipeManager().getAllMatches(ModernMillStoneRecipe.Type.INSTANCE, recipeInput, world);
        if (matches.isEmpty()) {
            if (blockEntity.grindProgressTime != 0) {
                blockEntity.grindProgressTime = 0;
                blockEntity.markDirty();
            }
            return;
        }

        // Get the item currently in the millstone inventory
        ItemStack currentStack = blockEntity.inventory.getStack(0);

        // If the inventory is full (contains 1 item), don't transfer anything
        if (currentStack.getCount() == 1) {
            // Optionally emit a redstone signal to indicate the inventory is full
            world.updateListeners(pos, state, state.with(FULL, true), Block.NOTIFY_LISTENERS);
            return; // Early exit if the inventory is full
        }

        // If the inventory is empty (room for 1 item), we should try to transfer an item from the hopper
        if (currentStack.isEmpty()) {
            net.minecraft.inventory.Inventory inventoryUp = HopperBlockEntity.getInventoryAt(world, pos.up());
            if (inventoryUp != null) {
                ItemStack itemStackToTransfer = inventoryUp.getStack(0).copy();
                if (!itemStackToTransfer.isEmpty()) {
                    // Attempt to transfer the item from the hopper to the millstone
                    ItemStack remainingStack = HopperBlockEntity.transfer(inventoryUp, blockEntity.inventory, itemStackToTransfer, Direction.UP);
                    if (remainingStack.isEmpty()) {
                        blockEntity.setStack(0, itemStackToTransfer);
                        blockEntity.markDirty();
                        world.updateListeners(pos, state, state.with(FULL, true), Block.NOTIFY_LISTENERS); // Inventory is now full
                    }
                }
            }
        }


        blockEntity.grindProgressTime += 1;

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

    public Optional<RecipeEntry<ModernMillStoneRecipe>> getRecipeFor(ItemStack stack)
    {
        if (stack.isEmpty()) {
            return Optional.empty();
        }

        return this.matchGetter.getFirstMatch(new SingleCountMillStoneRecipeInput(Collections.singletonList(stack)), this.world);
    }

    public boolean completeRecipe(ModernMillStoneRecipe recipe, World world, BlockPos pos) {
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

    public boolean addItem(Entity user, ItemStack stack) {
        this.setStack(0, stack);
        this.getWorld().emitGameEvent(GameEvent.BLOCK_CHANGE, this.getPos(), GameEvent.Emitter.of(user, this.getCachedState()));
        world.setBlockState(pos, world.getBlockState(pos).with(FULL, true));
        this.updateListeners();
        stack.decrement(1); // Decrement the original stack by 1

        return true;
    }


    public void retrieveItem(World world, PlayerEntity player) {

        if (!inventory.isEmpty() && !world.isClient()) {
            boolean addedToInventory = player.giveItemStack(inventory.getStack(0));
            if (!addedToInventory) {
                player.dropItem(inventory.getStack(0), false);
            }
            // set the inventory slot to empty
            this.setStack(0, ItemStack.EMPTY);
            // make the blockstate full ( maybe we can move this logic all together to a separate method?)
            world.setBlockState(pos, world.getBlockState(pos).with(FULL, false));
            this.updateListeners();
            this.getWorld().updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), Block.NOTIFY_ALL);
        }
    }

    private void updateListeners() {
        this.markDirty();
        this.getWorld().updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
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
    public int getMaxCountPerStack() {
        return inventory.getMaxCountPerStack();
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
    public int getMaxCount(ItemStack stack) {
        return inventory.getMaxCount(stack);
    }

    @Override
    public void clear() {
        inventory.clear();
    }

    public class Inventory extends SimpleInventory {
        public Inventory(int size) {
            super(size);
        }

        @Override
        public boolean isEmpty() {
            return inventory.getStack(0).getCount() < 1;
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return isEmpty();
        }

        @Override
        public ItemStack addStack(ItemStack stack) {
            return isEmpty() ? stack : ItemStack.EMPTY;
        }

        @Override
        public int size() {
            return 1;
        }

        @Override
        public int getMaxCountPerStack() {
            return size();
        }

        @Override
        public int getMaxCount(ItemStack stack) {
            return getMaxCountPerStack();
        }

        @Override
        public void markDirty() {
            ModernMillStoneBE.this.markDirty();
        }
    }

}
