package org.ivangeevo.bwt_hct.recipes.mill_stone;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;

import java.util.List;

public record SingleCountMillStoneRecipeInput(List<ItemStack> items) implements RecipeInput {
    @Override
    public ItemStack getStackInSlot(int slot) {
        return items.get(slot);
    }

    @Override
    public int getSize() {
        return 1;
    }


}
