package org.ivangeevo.bwt_hct.mixin;

import com.bwt.blocks.BwtBlocks;
import net.minecraft.block.AbstractBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(BwtBlocks.class)
public abstract class BwtBlocksMixin {

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/bwt/blocks/HempCropBlock;<init>(Lnet/minecraft/block/AbstractBlock$Settings;)V"))
    private static AbstractBlock.Settings bwt_hct$init1(AbstractBlock.Settings settings) {
        return settings.strength(1);
    }
}
