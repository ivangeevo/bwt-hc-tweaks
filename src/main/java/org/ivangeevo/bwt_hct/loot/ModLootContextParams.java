package org.ivangeevo.bwt_hct.loot;

import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.util.Identifier;
import org.ivangeevo.bwt_hct.BWT_HCTMod;

public class ModLootContextParams {
    public static final LootContextParameter<Boolean> IS_PISTON_BREAK =
        new LootContextParameter<>(Identifier.of(BWT_HCTMod.MOD_ID, "is_piston_break"));
}
