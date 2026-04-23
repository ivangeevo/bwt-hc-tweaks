package org.btwr.bwt_hct.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryWrapper;
import org.btwr.bwt_hct.tag.ModTags;

import java.util.concurrent.CompletableFuture;

public class BWT_HCT_EntityTypeTagProvider extends FabricTagProvider.EntityTypeTagProvider {

    public BWT_HCT_EntityTypeTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(ModTags.EntityTypes.INCREASED_SKULL_DROP_RATE_FROM_CHOPPING_BLOCK)
                .add(EntityType.CREEPER)
                .add(EntityType.SKELETON)
                .add(EntityType.ZOMBIE)
                .add(EntityType.WITHER_SKELETON);
    }

}