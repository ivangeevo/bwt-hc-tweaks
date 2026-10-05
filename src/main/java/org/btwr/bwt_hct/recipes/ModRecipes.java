package org.btwr.bwt_hct.recipes;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.btwr.bwt_hct.BWT_HCTMod;
import org.btwr.bwt_hct.recipes.mill_stone.ModernMillStoneRecipe;

public class ModRecipes {

    public static void register() {
        // Mill Stone (modern)
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, ResourceLocation.fromNamespaceAndPath(BWT_HCTMod.MOD_ID, ModernMillStoneRecipe.Serializer.ID),
                ModernMillStoneRecipe.Serializer.INSTANCE);
        Registry.register(BuiltInRegistries.RECIPE_TYPE, ResourceLocation.fromNamespaceAndPath(BWT_HCTMod.MOD_ID, ModernMillStoneRecipe.Type.ID),
                ModernMillStoneRecipe.Type.INSTANCE);
    }

}