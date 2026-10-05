package org.btwr.bwt_hct.datagen;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.HempCropBlock;
import com.bwt.items.BwtItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import org.btwr.bwt_hct.blocks.ModBlocks;

import java.util.concurrent.CompletableFuture;

public class BWT_HCT_LootTableProvider extends FabricBlockLootTableProvider {

    public BWT_HCT_LootTableProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    public static final LootItemCondition.Builder WITH_CONVENTIONAL_SHEARS = MatchTool.toolMatches(
            ItemPredicate.Builder.item().of(ConventionalItemTags.SHEAR_TOOLS)
    );

    public static final LootItemBlockStatePropertyCondition.Builder MAX_AGE_HEMP_CROP =
            LootItemBlockStatePropertyCondition.hasBlockStateProperties(BwtBlocks.hempCropBlock)
            .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(HempCropBlock.AGE, HempCropBlock.MAX_AGE));

    public static final LootItemBlockStatePropertyCondition.Builder IS_HEMP_CROP_TOP =
            LootItemBlockStatePropertyCondition.hasBlockStateProperties(BwtBlocks.hempCropBlock)
                    .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(BooleanProperty.create("is_top"), true));

    @Override
    public void generate() {
        add(ModBlocks.modernMillStoneBlock, createSingleItemTable(ModBlocks.modernMillStoneBlock));
        add(ModBlocks.dormantSoulForge, createSingleItemTable(ModBlocks.dormantSoulForge));
        add(ModBlocks.choppingBlock, createSingleItemTable(ModBlocks.choppingBlock));

        //this.addHempDrops();
    }

    private void addHempDrops() {
        LootPool.Builder shearsHempPool = LootPool.lootPool()
                .when(WITH_CONVENTIONAL_SHEARS)
                .add(LootItem.lootTableItem(BwtItems.hempItem).when(MAX_AGE_HEMP_CROP));

        LootPool.Builder shearsSeedPool = LootPool.lootPool()
                .when(WITH_CONVENTIONAL_SHEARS)
                .add(LootItem.lootTableItem(BwtItems.hempSeedsItem)
                        .when(MAX_AGE_HEMP_CROP)
                        .when(IS_HEMP_CROP_TOP)
                        .when(LootItemRandomChanceCondition.randomChance(0.5f))
                );

        add(BwtBlocks.hempCropBlock,
                applyExplosionDecay(BwtBlocks.hempCropBlock,
                        LootTable.lootTable()
                                .withPool(shearsHempPool)
                                .withPool(shearsSeedPool)
                )
        );
    }

    @Override
    public String getName() {
        return null;
    }

}