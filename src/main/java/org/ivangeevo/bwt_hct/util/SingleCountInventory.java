package org.ivangeevo.bwt_hct.util;

import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;

public class SingleCountInventory extends SimpleInventory {

    public SingleCountInventory(int size) {
        super(size);
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }

    @Override
    public int getMaxCount(ItemStack stack) {
        return super.getMaxCount(stack);
    }

    @Override
    public boolean isEmpty() {
        return this.getStack(0).getCount() < 1;
    }

    @Override
    public boolean canTransferTo(Inventory hopperInventory, int slot, ItemStack stack) {
        return this.isEmpty() && stack.getCount() == 1 && super.canInsert(stack);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return isEmpty();
    }

}