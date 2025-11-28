package org.btwr.bwt_hct.entity;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.btwr.bwt_hct.BWT_HCTMod;
import org.btwr.bwt_hct.blocks.ModBlocks;
import org.btwr.bwt_hct.entity.block.ModernMillStoneBE;

public class ModBlockEntities {

    public static BlockEntityType<ModernMillStoneBE> modernMillStoneEntity;

    public static void registerBlockEntities() {
        modernMillStoneEntity = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(BWT_HCTMod.MOD_ID, "modern_mill_stone"),
                BlockEntityType.Builder.create(ModernMillStoneBE::new,
                ModBlocks.modernMillStoneBlock).build(null)
        );
    }

}