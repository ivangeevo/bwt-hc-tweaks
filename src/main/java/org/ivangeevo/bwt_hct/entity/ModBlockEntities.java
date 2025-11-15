package org.ivangeevo.bwt_hct.entity;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.ivangeevo.bwt_hct.BWT_HCTMod;
import org.ivangeevo.bwt_hct.blocks.ModBlocks;
import org.ivangeevo.bwt_hct.entity.block.ArcaneVesselBE;
import org.ivangeevo.bwt_hct.entity.block.ModernMillStoneBE;

public class ModBlockEntities {

    public static BlockEntityType<ModernMillStoneBE> modernMillStoneEntity;
    public static BlockEntityType<ArcaneVesselBE> arcaneVesselEntity;

    public static void registerBlockEntities() {
        modernMillStoneEntity = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(BWT_HCTMod.MOD_ID, "modern_mill_stone"),
                BlockEntityType.Builder.create(ModernMillStoneBE::new,
                ModBlocks.modernMillStoneBlock).build(null)
        );

        arcaneVesselEntity = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(BWT_HCTMod.MOD_ID, "arcane_vessel"),
                BlockEntityType.Builder.create(ArcaneVesselBE::new,
                        ModBlocks.arcaneVesselBlock).build(null)
        );

    }



}
