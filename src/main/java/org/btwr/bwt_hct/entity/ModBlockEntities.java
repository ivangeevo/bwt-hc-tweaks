package org.btwr.bwt_hct.entity;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.btwr.bwt_hct.BWT_HCTMod;
import org.btwr.bwt_hct.blocks.ModBlocks;
import org.btwr.bwt_hct.entity.block.ModernMillStoneBE;

public class ModBlockEntities {

    public static BlockEntityType<ModernMillStoneBE> modernMillStoneEntity;

    public static void register() {
        modernMillStoneEntity = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(BWT_HCTMod.MOD_ID, "modern_mill_stone"),
                BlockEntityType.Builder.of(ModernMillStoneBE::new,
                ModBlocks.modernMillStoneBlock).build(null)
        );
    }

}