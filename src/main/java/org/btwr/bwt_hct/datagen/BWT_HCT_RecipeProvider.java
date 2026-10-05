package org.btwr.bwt_hct.datagen;

import com.bwt.blocks.BwtBlocks;
import com.bwt.items.BwtItems;
import com.bwt.recipes.cooking_pots.CauldronRecipe;
import com.bwt.recipes.cooking_pots.StokedCrucibleRecipe;
import com.bwt.recipes.soul_forge.SoulForgeShapedRecipe;
import com.bwt.tags.BwtItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import org.btwr.bwt_hct.items.ModItems;
import org.btwr.shared_library.util.utils.IdUtils;
import org.btwr.shared_library.util.utils.RecipeUtils;
import org.btwr.bwt_hct.BWT_HCTMod;
import org.btwr.bwt_hct.blocks.ModBlocks;

import java.util.concurrent.CompletableFuture;


public class BWT_HCT_RecipeProvider extends FabricRecipeProvider implements RecipeUtils {

    public BWT_HCT_RecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void buildRecipes(RecipeOutput exporter) {
        //this.generateDisabledRecipes(exporter);
        this.generateBwtRecipesOverride(exporter);
        this.generateModRecipes(exporter);

        // Override vanilla recipe for TNT
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.TNT)
                .define('f', ModItems.fuse)
                .define('g', Items.GUNPOWDER)
                .define('b', Items.BARREL)
                .pattern("gfg")
                .pattern("gbg")
                .pattern("ggg")
                .unlockedBy(getHasName(Items.GUNPOWDER), has(Items.GUNPOWDER))
                .save(exporter, IdUtils.ofMC("tnt"));
    }

    private void generateDisabledRecipes(RecipeOutput exporter) {
        disableRecipe(exporter, "bwt", "mill_stone");
    }

    private void generateBwtRecipesOverride(RecipeOutput exporter) {
        // Override soulforge recipe to require nether star
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, BwtBlocks.soulForgeBlock)
                .requires(ModBlocks.dormantSoulForge)
                .requires(Items.NETHER_STAR)
                .unlockedBy("has_dormant_soul_forge", has(ModBlocks.dormantSoulForge))
                .save(exporter, IdUtils.ofBWT("soul_forge"));

        // Override dynamite recipe to require fuse and blasting oil
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BwtItems.dynamiteItem, 2)
                .pattern("pf")
                .pattern("pb")
                .pattern("ps")
                .define('p', Items.PAPER)
                .define('f', ModItems.fuse)
                .define('b', ModItems.blastingOil)
                .define('s', BwtItemTags.SAW_DUSTS)
                .unlockedBy(getHasName(ModItems.blastingOil), has(ModItems.blastingOil))
                .save(exporter, IdUtils.ofBWT("dynamite"));
    }

    private void generateModRecipes(RecipeOutput exporter) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.fuse, 2)
                .requires(Items.GUNPOWDER)
                .requires(ConventionalItemTags.STRINGS)
                .unlockedBy(getHasName(Items.GUNPOWDER), has(Items.GUNPOWDER))
                .save(exporter);

        // Modern millstone is not implemented yet
        /**
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.modernMillStoneBlock)
                .input('G', BwtItems.gearItem)
                .input('S', Blocks.STONE)
                .pattern("SSS")
                .pattern("SSS")
                .pattern("SGS")
                .criterion("has_gear", conditionsFromItem(BwtItems.gearItem))
                .offerTo(exporter, Identifier.of("bwt_hct", "modern_mill_stone"));
         **/

        SoulForgeShapedRecipe.JsonBuilder.shaped(RecipeCategory.MISC, ModBlocks.dormantSoulForge)
                .define('g', Items.GOLD_INGOT)
                .pattern("gggg")
                .pattern(" g  ")
                .pattern(" g  ")
                .pattern("gggg")
                .unlockedBy("has_soul_forge", has(BwtBlocks.soulForgeBlock))
                .save(exporter, ResourceLocation.fromNamespaceAndPath(BWT_HCTMod.MOD_ID, "dormant_soul_forge"));

        StokedCrucibleRecipe.JsonBuilder.create().result(Items.GOLD_NUGGET, 60)
                .ingredient(ModBlocks.dormantSoulForge.asItem())
                .unlockedBy("has_dormant_soul_forge", has(ModBlocks.dormantSoulForge.asItem()))
                .save(exporter, ResourceLocation.fromNamespaceAndPath(BWT_HCTMod.MOD_ID,"dormant_soul_forge_recycling"));

        CauldronRecipe.JsonBuilder.create().result(ModItems.blastingOil, 2)
                .ingredient(BwtItems.hellfireDustItem)
                .ingredient(BwtItems.tallowItem)
                .unlockedBy(getHasName(BwtItems.tallowItem), has(BwtItems.tallowItem))
                .save(exporter);

        SoulForgeShapedRecipe.JsonBuilder.shaped(RecipeCategory.MISC, ModBlocks.choppingBlock)
                .define('s', ItemTags.STONE_CRAFTING_MATERIALS)
                .pattern("s  s")
                .pattern("s  s")
                .pattern("ssss")
                .unlockedBy("has_stone_crafting_material", has(ItemTags.STONE_CRAFTING_MATERIALS))
                .save(exporter);
    }

    @Override
    protected ResourceLocation getRecipeIdentifier(ResourceLocation identifier) {
        return identifier;
    }

}