package org.ivangeevo.bwt_hct.loot;

import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.ivangeevo.bwt_hct.BWT_HCTMod;

public class ModLootConditionTypes
{

    public static final LootConditionType PISTON_BREAK =
            Registry.register(
                    Registries.LOOT_CONDITION_TYPE,
                    Identifier.of(BWT_HCTMod.MOD_ID, "piston_break"),
                    new LootConditionType(PistonBreakLootCondition.CODEC)
            );


    public static void init() {
        // Called during mod init
    }

}
