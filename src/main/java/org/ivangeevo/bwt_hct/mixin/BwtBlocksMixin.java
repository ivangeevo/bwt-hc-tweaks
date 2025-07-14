package org.ivangeevo.bwt_hct.mixin;

import com.bwt.blocks.BwtBlocks;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.piston.PistonBehavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(BwtBlocks.class)
public abstract class BwtBlocksMixin {

    // Add settings to hemp blocks - some strength and destroy on piston push to allow automation
    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/bwt/blocks/HempCropBlock;<init>(Lnet/minecraft/block/AbstractBlock$Settings;)V"))
    private static AbstractBlock.Settings bwt_hct$init(AbstractBlock.Settings settings) {
        return settings.strength(1);
    }

    // Add settings to unfired urn
    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/bwt/blocks/UnfiredUrnBlock;<init>(Lnet/minecraft/block/AbstractBlock$Settings;)V"))
    private static AbstractBlock.Settings bwt_hct$init1(AbstractBlock.Settings settings) {
        return settings.solid();
    }

    // Add settings to mould
    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/bwt/blocks/UnfiredMouldBlock;<init>(Lnet/minecraft/block/AbstractBlock$Settings;)V"))
    private static AbstractBlock.Settings bwt_hct$init2(AbstractBlock.Settings settings) {
        return settings.solid();
    }

}
