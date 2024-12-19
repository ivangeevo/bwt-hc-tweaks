package org.ivangeevo.bwt_hct.recipes;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.ivangeevo.bwt_hct.BWT_HCTMod;
import org.ivangeevo.bwt_hct.recipes.mill_stone.ModernMillStoneRecipe;

public class ModRecipes
{

    public static void registerRecipes() {

        // Mill Stone (modern)
        Registry.register(Registries.RECIPE_SERIALIZER, Identifier.of(BWT_HCTMod.MOD_ID, ModernMillStoneRecipe.Serializer.ID),
                ModernMillStoneRecipe.Serializer.INSTANCE);
        Registry.register(Registries.RECIPE_TYPE, Identifier.of(BWT_HCTMod.MOD_ID, ModernMillStoneRecipe.Type.ID),
                ModernMillStoneRecipe.Type.INSTANCE);

    }
}
