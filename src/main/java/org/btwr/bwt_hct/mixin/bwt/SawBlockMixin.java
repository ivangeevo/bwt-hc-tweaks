package org.btwr.bwt_hct.mixin.bwt;


import com.bwt.blocks.SawBlock;
import com.bwt.blocks.SimpleFacingBlock;
import org.btwr.bwt_hct.config.BWT_HCTConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(SawBlock.class)
public abstract class SawBlockMixin extends SimpleFacingBlock {

    protected SawBlockMixin(Settings settings) {
        super(settings);
    }

    // Modifying the tick rate argument to the retail BTW value
    @ModifyArg(method = "scheduleUpdateIfRequired",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;scheduleBlockTick(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;I)V", ordinal = 1), index = 2)
    private int modifySawBreakSpeed(int par3) {
        return BWT_HCTConfig.sawBlockBreakSpeed.get();
    }

}