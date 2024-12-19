package org.ivangeevo.bwt_hct.entity;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.ivangeevo.bwt_hct.BWT_HCTMod;
import org.ivangeevo.bwt_hct.block.ModBlocks;
import org.ivangeevo.bwt_hct.entity.block.ModernMillstoneBE;

public class ModBlockEntities
{

    public static BlockEntityType<ModernMillstoneBE> MODERN_MILLSTONE;

    public static void registerBlockEntities() {
        MODERN_MILLSTONE = Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(BWT_HCTMod.MOD_ID,
                "modern_mill_stone.json"), BlockEntityType.Builder.create(ModernMillstoneBE::new,
                ModBlocks.modernMillStoneBlock).build(null));

    }



}
