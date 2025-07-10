package org.ivangeevo.bwt_hct.mixin;

import com.bwt.blocks.SoilPlanterBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.state.property.Properties.MOISTURE;

@Mixin(Block.class)
public abstract class BlockMixin {

    @Inject(method = "appendProperties", at = @At("HEAD"))
    private void onAppendProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        if (((Block)(Object)this) instanceof SoilPlanterBlock) {
            builder.add(MOISTURE);
        }
    }

}
