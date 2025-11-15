package org.ivangeevo.bwt_hct.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.ivangeevo.bwt_hct.util.DragonOrbHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.entity.ExperienceOrbEntity.roundToOrbSize;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity
{
    @Shadow public abstract boolean isExperienceDroppingDisabled();

    @Shadow protected abstract boolean shouldAlwaysDropXp();

    @Shadow protected int playerHitTimer;

    @Shadow public abstract boolean shouldDropXp();

    @Shadow public abstract int getXpToDrop(ServerWorld world, @Nullable Entity attacker);

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "dropXp", at = @At("HEAD"), cancellable = true)
    private void onDropXp(Entity attacker, CallbackInfo ci) {
        if (this.getWorld() instanceof ServerWorld serverWorld
                && !this.isExperienceDroppingDisabled()
                && (this.shouldAlwaysDropXp() || this.shouldDropXp() && this.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_LOOT)))
        {

            // Separated the player hit timer check from the above check
            if (this.playerHitTimer > 0) {
                ExperienceOrbEntity.spawn(serverWorld, this.getPos(), this.getXpToDrop(serverWorld, attacker));
            } else {
                DragonOrbHelper.spawn(serverWorld, this.getPos(), this.getXpToDrop(serverWorld, attacker));
            }
        }

        ci.cancel();
    }

}
