package org.btwr.bwt_hct.mixin.bwt;

import com.bwt.blocks.mech_hopper.MechHopperBlockEntity;
import com.bwt.recipes.soul_bottling.SoulBottlingRecipe;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(MechHopperBlockEntity.class)
public abstract class MechHopperBlockEntityMixin {
    // Try possession for filtering if the Nothing to Worry About mod is present
    @Redirect(method = "bottleSouls", at = @At(value = "INVOKE", target = "Ljava/util/Optional;isEmpty()Z"))
    private static boolean onSoulRecipientCheck(Optional<SoulBottlingRecipe> optionalRecipe, Level world, MechHopperBlockEntity hopperBE) {
        boolean empty = optionalRecipe.isEmpty();
        if (empty && FabricLoader.getInstance().isModLoaded("ntwa")) {
            //PossessionManager.onHopperFilteringFailure(world, hopperBE.getPos(), new PossessionSource.HopperFiltering());
        }
        return empty;
    }

    // Try possession for hopper explosion if the Nothing to Worry About mod is present
    // Removed ghast spawning if possession happens
    @Redirect(method = "soulOverloadExplode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private static boolean onHopperExplosion(Level world, Entity entity, @Local(argsOnly = true) MechHopperBlockEntity hopperBE) {
        if (FabricLoader.getInstance().isModLoaded("ntwa")) {
            //PossessionManager.onHopperExplosion(world, hopperBE.getPos(), new PossessionSource.HopperExplosion());
            return false;
        }
        return world.addFreshEntity(entity);
    }
}