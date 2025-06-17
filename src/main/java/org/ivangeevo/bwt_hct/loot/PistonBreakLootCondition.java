package org.ivangeevo.bwt_hct.loot;

import com.mojang.serialization.MapCodec;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.context.LootContext;

public class PistonBreakLootCondition implements LootCondition {


    public static final MapCodec<PistonBreakLootCondition> CODEC = MapCodec.unit(new PistonBreakLootCondition());

    private PistonBreakLootCondition() {

    }

    /**
    @Override
    public boolean test(LootContext context) {
        boolean pistonBreak = PistonBreakTracker.isPistonBreak();
        PistonBreakTracker.clear();
        if (!pistonBreak) return false;

        Random random = context.getRandom();
        return random.nextFloat() < chance;
    }
     **/

    @Override
    public boolean test(LootContext context) {
        return Boolean.TRUE.equals(context.get(ModLootContextParams.IS_PISTON_BREAK));
    }

    @Override
    public LootConditionType getType() {
        return ModLootConditionTypes.PISTON_BREAK;
    }

}
