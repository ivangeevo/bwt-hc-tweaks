package org.btwr.bwt_hct.recipes.mill_stone;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record SingleCountMillStoneRecipeInput(List<ItemStack> items) implements RecipeInput {

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public int size() {
        return 1;
    }

}