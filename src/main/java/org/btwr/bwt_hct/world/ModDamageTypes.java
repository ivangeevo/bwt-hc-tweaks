package org.btwr.bwt_hct.world;

import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.btwr.bwt_hct.BWT_HCTMod;

public class ModDamageTypes {
    public static final RegistryKey<DamageType> BLASTING_OIL = RegistryKey.of(
            RegistryKeys.DAMAGE_TYPE,
            Identifier.of(BWT_HCTMod.MOD_ID, "blasting_oil")
    );
}