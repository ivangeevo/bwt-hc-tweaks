package org.ivangeevo.bwt_hct.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.ivangeevo.bwt_hct.BWT_HCTMod;

public class ModEntities
{

    public static EntityType<VerticalWindmillEntity> verticalWindmillEntity;

    public static void registerEntities() {
        verticalWindmillEntity = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(BWT_HCTMod.MOD_ID, "vertical_windmill"),
                EntityType.Builder.create((EntityType.EntityFactory<VerticalWindmillEntity>)VerticalWindmillEntity::new, SpawnGroup.MISC)
                        .build(null));

    }





}
