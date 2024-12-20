package org.ivangeevo.bwt_hct.entity;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.ivangeevo.bwt_hct.BWT_HCTMod;
import org.ivangeevo.bwt_hct.block.ModBlocks;
import org.ivangeevo.bwt_hct.entity.block.ModernMillStoneBE;

public class ModBlockEntities
{

    public static BlockEntityType<ModernMillStoneBE> MODERN_MILLSTONE;

    public static void registerBlockEntities() {
        MODERN_MILLSTONE = Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(BWT_HCTMod.MOD_ID,
                "modern_mill_stone"), BlockEntityType.Builder.create(ModernMillStoneBE::new,
                ModBlocks.modernMillStoneBlock).build(null));

    }



}
