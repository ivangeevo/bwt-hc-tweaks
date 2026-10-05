package org.btwr.bwt_hct.mixin.bwt;

import com.bwt.entities.SoulUrnProjectileEntity;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.btwr.bwt_hct.mixin.ZombieVillagerEntityAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(SoulUrnProjectileEntity.class)
public abstract class SoulUrnProjectileEntityMixin extends ThrowableItemProjectile {

    public SoulUrnProjectileEntityMixin(EntityType<? extends ThrowableItemProjectile> entityType, Level world) {
        super(entityType, world);
    }

    // Targeted redirect to only remove ghast spawning
    @Redirect(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityType;create(Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/entity/Entity;"))
    private Entity bwt_hct$preventGhastSpawn(EntityType<?> entityType, Level world, HitResult hitResult) {
        // Try possession with the Nothing to Worry About mod
        if (!(hitResult instanceof EntityHitResult) && FabricLoader.getInstance().isModLoaded("ntwa")) {
            //PossessionManager.onSoulUrnMiss(world, hitResult.getPos(), new PossessionSource.SoulUrn());
        }
        return null;
    }

    // Make it so that zombie villagers hit with soul urn start converting
    // Also add optional possession when the Nothing to Worry About mod is present
    @Inject(method = "onHitEntity", at = @At("HEAD"), cancellable = true)
    private void onZombieVillagerHit(EntityHitResult entityHitResult, CallbackInfo ci) {
        super.onHitEntity(entityHitResult);
        Entity entityHit = entityHitResult.getEntity();
        Entity owner = this.getOwner();

        if (entityHit instanceof ZombieVillager zillager) {
            ZombieVillagerEntityAccessor accessor = (ZombieVillagerEntityAccessor) zillager;

            if (!this.level().isClientSide) {
                if (owner instanceof Player player) {
                    accessor.invokeSetConverting(player.getUUID(), this.random.nextInt(2401) + 3600);
                } else {
                    accessor.invokeSetConverting(null, this.random.nextInt(2401) + 3600);
                }
            }

            entityHitResult.getEntity().hurt(this.damageSources().thrown(this, this.getOwner()), 0.0F);

            ci.cancel();
            // Try possession
        } else if (!this.level().isClientSide) {
            if (entityHit instanceof LivingEntity livingEntity && FabricLoader.getInstance().isModLoaded("ntwa")) {
                //PossessionManager.onSoulUrnHit(livingEntity, new PossessionSource.SoulUrn());
            }
        }
    }

}