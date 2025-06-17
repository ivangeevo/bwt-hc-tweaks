package org.ivangeevo.bwt_hct.mixin;

import com.bwt.entities.SoulUrnProjectileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.UUID;



@Mixin(SoulUrnProjectileEntity.class)
public abstract class SoulUrnProjectileEntityMixin extends ThrownItemEntity
{


    public SoulUrnProjectileEntityMixin(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    // Reworked to remove ghast spawning
    //@Inject(method = "onCollision", at = @At("HEAD"), cancellable = true)
    private void modifyOnCollision(HitResult hitResult, CallbackInfo ci) {
        super.onCollision(hitResult);
        this.getWorld().sendEntityStatus(this, (byte)3);
        this.discard();
        ci.cancel();
    }

    //@Inject(method = "onEntityHit", at = @At("HEAD"), cancellable = true)
    private void convertOnZillagerHit(EntityHitResult entityHitResult, CallbackInfo ci) {
        System.out.println("convertOnZillagerHit");
        super.onEntityHit(entityHitResult);
        Entity entityHit = entityHitResult.getEntity();
        Entity owner = this.getOwner();

        if (entityHit instanceof ZombieVillagerEntity zillager && this.getOwner() != null) {
            if (!(owner instanceof PlayerEntity player)) return;
            if (!this.getWorld().isClient) {
                ZombieVillagerEntityAccessor accessor = (ZombieVillagerEntityAccessor) zillager;
                accessor.setConverting(player.getUuid(), this.random.nextInt(2401) + 3600);
            }
        }

        ci.cancel();
    }

}
