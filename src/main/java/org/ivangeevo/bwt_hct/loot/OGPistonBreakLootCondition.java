package org.ivangeevo.bwt_hct.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.math.random.Random;

public class OGPistonBreakLootCondition implements LootCondition {

    private final float chance;

    public static final MapCodec<OGPistonBreakLootCondition> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("chance").forGetter(c -> c.chance)
            ).apply(instance, OGPistonBreakLootCondition::new)
    );

    public OGPistonBreakLootCondition(float chance) {
        this.chance = chance;
    }

    @Override
    public boolean test(LootContext context) {
        boolean pistonBreak = PistonBreakTracker.isPistonBreak();
        PistonBreakTracker.clear();
        if (!pistonBreak) return false;

        Random random = context.getRandom();
        return random.nextFloat() < chance;
    }

    @Override
    public LootConditionType getType() {
        return ModLootConditionTypes.PISTON_BREAK;
    }

}
