package org.ivangeevo.bwt_hct.recipes;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;

public class SingleStackCountInventory extends SimpleInventory
{

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
}
