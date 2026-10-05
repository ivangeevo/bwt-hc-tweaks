package org.btwr.bwt_hct.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.UUID;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.VillagerDataHolder;

@Mixin(ZombieVillager.class)
public interface ZombieVillagerEntityAccessor extends VillagerDataHolder {
    @Accessor("conversionStarter") void setConverter(@Nullable UUID uuid);
    @Accessor("villagerConversionTime") void setConversionTimer(int time);
    @Accessor("villagerConversionTime") int getConversionTime();
    @Invoker("startConverting") void invokeSetConverting(@Nullable UUID uuid, int delay);
}