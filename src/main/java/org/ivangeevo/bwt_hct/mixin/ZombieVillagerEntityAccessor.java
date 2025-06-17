package org.ivangeevo.bwt_hct.mixin;

import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.village.VillagerDataContainer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.UUID;

@Mixin(ZombieVillagerEntity.class)
public interface ZombieVillagerEntityAccessor extends VillagerDataContainer
{
    @Accessor("converter")
    void setConverter(@Nullable UUID uuid);

    @Accessor("conversionTimer")
    void setConversionTimer(int time);

    @Accessor("conversionTimer")
    int getConversionTime();

    @Invoker("setConverting")
    void setConverting(@Nullable UUID uuid, int delay);
}
