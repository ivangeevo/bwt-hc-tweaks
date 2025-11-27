package org.ivangeevo.bwt_hct.mixin.bwt;

import com.bwt.entities.SoulUrnProjectileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.ivangeevo.bwt_hct.mixin.ZombieVillagerEntityAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(SoulUrnProjectileEntity.class)
public abstract class SoulUrnProjectileEntityMixin extends ThrownItemEntity {

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