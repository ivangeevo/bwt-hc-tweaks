package org.btwr.bwt_hct.util;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class SingleCountInventory extends SimpleContainer {

    public SingleCountInventory() {
        super(1);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return this.getItem(0).getCount() < 1;
    }

    @Override
    public boolean canTakeItem(Container hopperInventory, int slot, ItemStack stack) {
        return this.isEmpty() && stack.getCount() == 1 && canAddItem(stack);
    }

    @Override
    public boolean canAddItem(ItemStack stack) {
        return isEmpty();
    }

}