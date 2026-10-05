package org.btwr.bwt_hct.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.level.Level;

@Mixin(ZombieVillager.class)
public abstract class ZombieVillagerEntityMixin extends Zombie implements VillagerDataHolder {

    @Shadow private @Nullable UUID conversionStarter;
    @Shadow private int villagerConversionTime;
    @Shadow @Final private static EntityDataAccessor<Boolean> DATA_CONVERTING_ID;

    public ZombieVillagerEntityMixin(EntityType<? extends Zombie> entityType, Level world) {
        super(entityType, world);
    }

    //@Inject(method = "setConverting", at = @At("HEAD"), cancellable = true)
    private void onSetConverting(UUID uuid, int delay, CallbackInfo ci) {
        this.conversionStarter = uuid;
        this.villagerConversionTime = delay;
        this.getEntityData().set(DATA_CONVERTING_ID, true);
        //this.removeStatusEffect(StatusEffects.WEAKNESS);
        //this.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, delay, Math.min(this.getWorld().getDifficulty().getId() - 1, 0)));
        this.level().broadcastEntityEvent(this, EntityEvent.ZOMBIE_CONVERTING);
        ci.cancel();
    }

}