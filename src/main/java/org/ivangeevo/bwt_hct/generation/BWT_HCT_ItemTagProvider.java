package org.ivangeevo.bwt_hct.generation;

import com.bwt.tags.BwtItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import org.ivangeevo.bwt_hct.items.ModItems;

import java.util.concurrent.CompletableFuture;

public class BWT_HCT_ItemTagProvider extends FabricTagProvider.ItemTagProvider
{


    public BWT_HCT_ItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
    }
}
