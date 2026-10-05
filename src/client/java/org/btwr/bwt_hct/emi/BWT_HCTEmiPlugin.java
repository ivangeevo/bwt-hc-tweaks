package org.btwr.bwt_hct.emi;

import com.bwt.blocks.BwtBlocks;
import com.bwt.recipes.BlockIngredient;
import com.bwt.utils.Id;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiRenderable;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.*;
import org.btwr.bwt_hct.blocks.ModBlocks;
import org.btwr.bwt_hct.emi.recipes.EmiModernMillstoneRecipe;
import org.btwr.bwt_hct.recipes.mill_stone.ModernMillStoneRecipe;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class BWT_HCTEmiPlugin implements EmiPlugin {
    public static final ResourceLocation WIDGETS = Id.of("textures/gui/container/emiwidgets.png");

    public static EmiRecipeCategory MILL_STONE = category("mill_stone", EmiStack.of(ModBlocks.modernMillStoneBlock));

    public static EmiRenderable simplifiedEmiStack(EmiStack stack) {
        return stack::render;
    }

    public static EmiRecipeCategory category(String id, EmiStack icon) {
        return new EmiRecipeCategory(Id.of(id), icon, icon::render);
    }

    public static EmiRecipeCategory category(String id, EmiStack icon, Comparator<EmiRecipe> comp) {
        return new EmiRecipeCategory(ResourceLocation.fromNamespaceAndPath("btw", id), icon,
                new EmiTexture(ResourceLocation.fromNamespaceAndPath("emi", "textures/simple_icons/" + id + ".png"), 0, 0, 16, 16, 16, 16, 16, 16), comp);
    }


    private static <C extends RecipeInput, T extends Recipe<C>> List<RecipeHolder<T>> getRecipes(EmiRegistry registry, RecipeType<T> type) {
        return registry.getRecipeManager().getAllRecipesFor(type);
    }

    private static <C extends RecipeInput, T extends CraftingRecipe> List<RecipeHolder<T>> getRecipes(EmiRegistry registry, RecipeType<T> type, Predicate<CraftingBookCategory> category) {
        return registry.getRecipeManager().getAllRecipesFor(type).stream().filter(r -> category.test(r.value().category())).toList();
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

