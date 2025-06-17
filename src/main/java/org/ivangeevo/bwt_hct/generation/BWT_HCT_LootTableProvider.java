package org.ivangeevo.bwt_hct.generation;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.HempCropBlock;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.state.property.BooleanProperty;
import org.ivangeevo.bwt_hct.blocks.ModBlocks;

import java.util.concurrent.CompletableFuture;

public class BWT_HCT_LootTableProvider extends FabricBlockLootTableProvider {

    public BWT_HCT_LootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    public static final LootCondition.Builder WITH_CONVENTIONAL_SHEARS = MatchToolLootCondition.builder(ItemPredicate.Builder.create().tag(ConventionalItemTags.SHEAR_TOOLS));

    public static final BlockStatePropertyLootCondition.Builder MAX_AGE_HEMP_CROP =
            BlockStatePropertyLootCondition.builder(BwtBlocks.hempCropBlock)
            .properties(StatePredicate.Builder.create().exactMatch(HempCropBlock.AGE, HempCropBlock.MAX_AGE));

    public static final BlockStatePropertyLootCondition.Builder IS_HEMP_CROP_TOP =
            BlockStatePropertyLootCondition.builder(BwtBlocks.hempCropBlock)
                    .properties(StatePredicate.Builder.create().exactMatch(BooleanProperty.of("is_top"), true));


    @Override
    public void generate() {
        addDrop(ModBlocks.modernMillStoneBlock, drops(ModBlocks.modernMillStoneBlock));
        addDrop(ModBlocks.dormantSoulForge, drops(ModBlocks.dormantSoulForge));

        //this.addHempDrops();
    }

    /**
    private void addHempDrops() {
        LootPool.Builder shearsHempPool = LootPool.builder()
                .conditionally(WITH_CONVENTIONAL_SHEARS)
                .with(ItemEntry.builder(BwtItems.hempItem).conditionally(MAX_AGE_HEMP_CROP));

        LootPool.Builder shearsSeedPool = LootPool.builder()
                .conditionally(WITH_CONVENTIONAL_SHEARS)
                .with(ItemEntry.builder(BwtItems.hempSeedsItem)
                        .conditionally(MAX_AGE_HEMP_CROP)
                        .conditionally(IS_HEMP_CROP_TOP)
                        .conditionally(RandomChanceLootCondition.builder(0.5f)));

        LootPool.Builder pistonHempPool = LootPool.builder()
                .conditionally(OGPistonBreakLootCondition.builder())
                .with(ItemEntry.builder(BwtItems.hempItem).conditionally(MAX_AGE_HEMP_CROP));

        LootPool.Builder pistonSeedPool = LootPool.builder()
                .conditionally(OGPistonBreakLootCondition.builder())
                .with(ItemEntry.builder(BwtItems.hempSeedsItem)
                        .conditionally(MAX_AGE_HEMP_CROP)
                        .conditionally(IS_HEMP_CROP_TOP)
                        .conditionally(RandomChanceLootCondition.builder(0.5f)));

        addDrop(BwtBlocks.hempCropBlock,
                applyExplosionDecay(BwtBlocks.hempCropBlock,
                        LootTable.builder()
                                .pool(shearsHempPool)
                                .pool(shearsSeedPool)
                                .pool(pistonHempPool)
                                .pool(pistonSeedPool)
                )
        );
    }
     **/

    @Override
    public String getName() {
        return null;
    }
}
