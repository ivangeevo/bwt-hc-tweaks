package org.ivangeevo.bwt_hct.recipes.mill_stone;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;

public record SingleItemMillStoneRecipeInput(ItemStack item) implements RecipeInput {
    @Override
    public ItemStack getStackInSlot(int slot) {
        return item;
    }

    @Override
    public int getSize() {
        return 1;
    }

}
