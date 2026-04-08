package org.btwr.bwt_hct.util;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleItemStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.item.ItemStack;

public class SingleCountStorage extends SingleItemStorage {

    private final SingleCountInventory inv;

    public SingleCountStorage(SingleCountInventory inv) {
        this.inv = inv;
    }

    @Override
    protected long getCapacity(ItemVariant variant) {
        return 1;
    }

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext tx) {
        if (maxAmount <= 0) return 0;
        ItemStack stack = inv.getStack(0);

        if (!stack.isEmpty()) return 0;

        long accepted = 1;
        inv.setStack(0, resource.toStack((int) accepted));
        return accepted;
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext tx) {
        ItemStack stack = inv.getStack(0);
        if (stack.isEmpty() || !ItemStack.areItemsAndComponentsEqual(stack, resource.toStack())) return 0;
        long extracted = Math.min(maxAmount, stack.getCount());
        if (extracted <= 0) return 0;

        ItemStack copy = stack.copy();
        copy.decrement((int) extracted);
        inv.setStack(0, copy.isEmpty() ? ItemStack.EMPTY : copy);
        return extracted;
    }

    @Override
    protected boolean canInsert(ItemVariant variant) {
        return this.inv.canInsert(variant.toStack());
    }
}