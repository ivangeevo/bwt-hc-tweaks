package org.ivangeevo.bwt_hct.generation;

import btwr.btwr_sl.lib.util.utils.RecipeProviderUtils;
import com.bwt.blocks.BwtBlocks;
import com.bwt.items.BwtItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import org.ivangeevo.bwt_hct.blocks.ModBlocks;

import java.util.concurrent.CompletableFuture;


public class BWT_HCT_RecipeProvider extends FabricRecipeProvider implements RecipeProviderUtils
{

    public BWT_HCT_RecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        //this.generateDisabledRecipes(exporter);
        this.generateModRecipes(exporter);
        this.generateBwtRecipes(exporter);
    }


    private void generateDisabledRecipes(RecipeExporter exporter) {
        disableRecipe(exporter, "bwt", "mill_stone");
    }

    private void generateBwtRecipes(RecipeExporter exporter) {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, BwtBlocks.soulForgeBlock)
                .input(ModBlocks.dormantSoulForge)
                .input(Items.NETHER_STAR)
                .criterion("has_dormant_soul_forge", conditionsFromItem(ModBlocks.dormantSoulForge))
                .offerTo(exporter, ID.ofBWT("soul_forge"));
    }

    private void generateModRecipes(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.modernMillStoneBlock)
                .input('G', BwtItems.gearItem)
                .input('S', Blocks.STONE)
                .pattern("SSS")
                .pattern("SSS")
                .pattern("SGS")
                .criterion("has_gear", conditionsFromItem(BwtItems.gearItem))
                .offerTo(exporter, Identifier.of("bwt_hct", "modern_mill_stone"));
    }

    @Override
    protected Identifier getRecipeIdentifier(Identifier identifier) {
        return identifier;
    }


}
