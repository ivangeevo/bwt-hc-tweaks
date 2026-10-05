package org.btwr.bwt_hct.world;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.Level;
import org.btwr.bwt_hct.BWT_HCTMod;

public class ModDamageTypes {
    public static final ResourceKey<DamageType> BLASTING_OIL = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(BWT_HCTMod.MOD_ID, "blasting_oil")
    );

    public static final ResourceKey<DamageType> CHOPPING_BLOCK = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(BWT_HCTMod.MOD_ID, "chopping_block")
    );

    public static DamageSource of(Level world, ResourceKey<DamageType> key) {
        return new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(key));
    }
}