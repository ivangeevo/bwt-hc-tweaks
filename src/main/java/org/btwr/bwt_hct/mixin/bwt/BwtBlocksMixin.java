package org.btwr.bwt_hct.mixin.bwt;

import com.bwt.blocks.BwtBlocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(BwtBlocks.class)
public abstract class BwtBlocksMixin {

    // Add settings to hemp blocks - some strength and destroy on piston push to allow automation
    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/bwt/blocks/HempCropBlock;<init>(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V"))
    private static BlockBehaviour.Properties bwt_hct$init(BlockBehaviour.Properties settings) {
        return settings.strength(1);
    }

    // Add settings to unfired urn
    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/bwt/blocks/unfired_pottery/UnfiredUrnBlock;<init>(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V"))
    private static BlockBehaviour.Properties bwt_hct$init1(BlockBehaviour.Properties settings) {
        return settings.forceSolidOn();
    }

}