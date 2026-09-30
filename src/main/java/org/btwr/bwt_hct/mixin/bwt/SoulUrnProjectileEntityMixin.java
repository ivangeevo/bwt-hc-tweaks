package org.btwr.bwt_hct.mixin.bwt;

import com.bwt.entities.SoulUrnProjectileEntity;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.btwr.bwt_hct.mixin.ZombieVillagerEntityAccessor;
import org.btwr.ntwa.entity.possession.PossessionManager;
import org.btwr.ntwa.entity.possession.PossessionSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(SoulUrnProjectileEntity.class)
public abstract class SoulUrnProjectileEntityMixin extends ThrownItemEntity {

    public SoulUrnProjectileEntityMixin(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    // Targeted redirect to only remove ghast spawning
    @Redirect(method = "onCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityType;create(Lnet/minecraft/world/World;)Lnet/minecraft/entity/Entity;"))
    private Entity bwt_hct$preventGhastSpawn(EntityType<?> entityType, World world, HitResult hitResult) {
        // Try possession with the Nothing to Worry About mod
        if (!(hitResult instanceof EntityHitResult) && FabricLoader.getInstance().isModLoaded("ntwa")) {
            //PossessionManager.onSoulUrnMiss(world, hitResult.getPos(), new PossessionSource.SoulUrn());
        }
        return null;
    }

    // Make it so that zombie villagers hit with soul urn start converting
    // Also add optional possession when the Nothing to Worry About mod is present
    @Inject(method = "onEntityHit", at = @At("HEAD"), cancellable = true)
    private void onZombieVillagerHit(EntityHitResult entityHitResult, CallbackInfo ci) {
        super.onEntityHit(entityHitResult);
        Entity entityHit = entityHitResult.getEntity();
        Entity owner = this.getOwner();

        if (entityHit instanceof ZombieVillagerEntity zillager) {
            ZombieVillagerEntityAccessor accessor = (ZombieVillagerEntityAccessor) zillager;

            if (!this.getWorld().isClient) {
                if (owner instanceof PlayerEntity player) {
                    accessor.invokeSetConverting(player.getUuid(), this.random.nextInt(2401) + 3600);
                } else {
                    accessor.invokeSetConverting(null, this.random.nextInt(2401) + 3600);
                }
            }

            entityHitResult.getEntity().damage(this.getDamageSources().thrown(this, this.getOwner()), 0.0F);

            ci.cancel();
            // Try possession
        } else if (!this.getWorld().isClient) {
            if (entityHit instanceof LivingEntity livingEntity && FabricLoader.getInstance().isModLoaded("ntwa")) {
                //PossessionManager.onSoulUrnHit(livingEntity, new PossessionSource.SoulUrn());
            }
        }
    }

}