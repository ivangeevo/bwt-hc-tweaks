package org.ivangeevo.bwt_hct.datagen;

import com.bwt.tags.BwtBlockTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import org.ivangeevo.bwt_hct.block.ModBlocks;

import java.util.concurrent.CompletableFuture;

public class BWT_HCT_BlockTagProvider extends FabricTagProvider.BlockTagProvider
{


    public BWT_HCT_BlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(ModBlocks.modernMillStoneBlock);

        getOrCreateTagBuilder(BwtBlockTags.MATTOCK_MINEABLE)
                .add(ModBlocks.modernMillStoneBlock);

    }
}
