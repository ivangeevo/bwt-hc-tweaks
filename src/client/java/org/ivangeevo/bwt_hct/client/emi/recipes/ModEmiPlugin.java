package org.ivangeevo.bwt_hct.client.emi.recipes;

import com.bwt.blocks.BwtBlocks;
import com.bwt.recipes.BlockIngredient;
import com.bwt.recipes.BwtRecipes;
import com.bwt.recipes.IngredientWithCount;
import com.bwt.utils.Id;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiRenderable;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.util.Identifier;
import org.ivangeevo.bwt_hct.block.ModBlocks;
import org.ivangeevo.bwt_hct.recipes.mill_stone.ModernMillStoneRecipe;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class ModEmiPlugin implements EmiPlugin {
    public static final Identifier WIDGETS = Id.of("textures/gui/container/emiwidgets.png");

    public static EmiRecipeCategory MILL_STONE = category("mill_stone", EmiStack.of(ModBlocks.modernMillStoneBlock));

    public static EmiRenderable simplifiedEmiStack(EmiStack stack) {
        return stack::render;
    }

    public static EmiRecipeCategory category(String id, EmiStack icon) {
        return new EmiRecipeCategory(Id.of(id), icon, icon::render);
    }

    public static EmiRecipeCategory category(String id, EmiStack icon, Comparator<EmiRecipe> comp) {
        return new EmiRecipeCategory(Identifier.of("btw", id), icon,
                new EmiTexture(Identifier.of("emi", "textures/simple_icons/" + id + ".png"), 0, 0, 16, 16, 16, 16, 16, 16), comp);
    }


    private static <C extends RecipeInput, T extends Recipe<C>> List<RecipeEntry<T>> getRecipes(EmiRegistry registry, RecipeType<T> type) {
        return registry.getRecipeManager().listAllOfType(type);
    }

    private static <C extends RecipeInput, T extends CraftingRecipe> List<RecipeEntry<T>> getRecipes(EmiRegistry registry, RecipeType<T> type, Predicate<CraftingRecipeCategory> category) {
        return registry.getRecipeManager().listAllOfType(type).stream().filter(r -> category.test(r.value().getCategory())).toList();
    }


    @Override
    public void register(EmiRegistry reg) {
        reg.addCategory(MILL_STONE);

        reg.addWorkstation(MILL_STONE, EmiStack.of(BwtBlocks.millStoneBlock));

        getRecipes(reg, ModernMillStoneRecipe.Type.INSTANCE).stream()
                .map(recipeEntry -> new EmiModernMillstoneRecipe(MILL_STONE, recipeEntry))
                .forEach(reg::addRecipe);

    }

    public static EmiIngredient from(Ingredient ingredient) {
        return EmiIngredient.of(ingredient);
    }

    public static EmiIngredient from(BlockIngredient blockIngredient) {
        List<EmiIngredient> ingredientList = new ArrayList<>();
        blockIngredient.optionalBlock().map(EmiStack::of).ifPresent(ingredientList::add);
        blockIngredient.optionalBlockTagKey().map(EmiIngredient::of).ifPresent(ingredientList::add);
        return EmiIngredient.of(ingredientList);
    }


}

