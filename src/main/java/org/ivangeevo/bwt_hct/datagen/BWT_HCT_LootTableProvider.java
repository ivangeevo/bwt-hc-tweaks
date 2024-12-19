package org.ivangeevo.bwt_hct.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;
import org.ivangeevo.bwt_hct.block.ModBlocks;

import java.util.concurrent.CompletableFuture;

public class BWT_HCT_LootTableProvider extends FabricBlockLootTableProvider {


    public BWT_HCT_LootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        addDrop(ModBlocks.modernMillStoneBlock, drops(ModBlocks.modernMillStoneBlock));
    }

    @Override
    public String getName() {
        return null;
    }
}
