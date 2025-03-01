package org.ivangeevo.bwt_hct.generation;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.HempCropBlock;
import com.bwt.items.BwtItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import org.ivangeevo.bwt_hct.blocks.ModBlocks;

import java.util.concurrent.CompletableFuture;

public class BWT_HCT_LootTableProvider extends FabricBlockLootTableProvider {


    public BWT_HCT_LootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    public static final LootCondition.Builder WITH_CONVENTIONAL_SHEARS = MatchToolLootCondition.builder(ItemPredicate.Builder.create().tag(ConventionalItemTags.SHEAR_TOOLS));


    @Override
    public void generate() {
        addDrop(ModBlocks.modernMillStoneBlock, drops(ModBlocks.modernMillStoneBlock));
        addDrop(ModBlocks.dormantSoulForge, drops(ModBlocks.dormantSoulForge));

        this.addHempDropWithShears();
    }

    private void addHempDropWithShears() {
        RegistryWrapper.Impl<Enchantment> enchantmentRegistry = this.registryLookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT);
        addDrop(
                BwtBlocks.hempCropBlock,
                applyExplosionDecay(
                        BwtBlocks.hempCropBlock,
                        LootTable.builder()
                                .pool(LootPool.builder()
                                        // If fully grown, drop hemp item
                                        .conditionally(BlockStatePropertyLootCondition.builder(BwtBlocks.hempCropBlock)
                                                .properties(StatePredicate.Builder.create().exactMatch(HempCropBlock.AGE, HempCropBlock.MAX_AGE))
                                        ).with(ItemEntry.builder(BwtItems.hempItem).conditionally(WITH_CONVENTIONAL_SHEARS))
                                ).pool(LootPool.builder()
                                        // Regardless of growth, drop some seeds
                                        .with(ItemEntry.builder(BwtItems.hempSeedsItem).conditionally(WITH_CONVENTIONAL_SHEARS)
                                                .conditionally(RandomChanceLootCondition.builder(0.5f))
                                                .apply(ApplyBonusLootFunction.binomialWithBonusCount(enchantmentRegistry.getOrThrow(Enchantments.FORTUNE), 0.5f, 0))
                                        )
                                )
                )
        );
    }

    @Override
    public String getName() {
        return null;
    }
}
