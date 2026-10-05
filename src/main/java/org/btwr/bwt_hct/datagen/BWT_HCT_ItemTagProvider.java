package org.btwr.bwt_hct.datagen;

import com.bwt.tags.BwtItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import org.btwr.bwt_hct.items.ModItems;

import java.util.concurrent.CompletableFuture;

public class BWT_HCT_ItemTagProvider extends FabricTagProvider.ItemTagProvider {

    public BWT_HCT_ItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        getOrCreateTagBuilder(BwtItemTags.STOKED_EXPLOSIVES)
                .add(ModItems.blastingOil);
    }

}