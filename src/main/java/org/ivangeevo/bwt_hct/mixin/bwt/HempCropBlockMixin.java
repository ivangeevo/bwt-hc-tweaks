package org.ivangeevo.bwt_hct.mixin.bwt;

import com.bwt.blocks.HempCropBlock;
import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.ivangeevo.bwt_hct.blocks.HempCropBlockManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HempCropBlock.class)
public abstract class HempCropBlockMixin extends CropBlock {

    @Shadow @Final public static BooleanProperty CONNECTED_UP;

    public HempCropBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void initProperty(Settings settings, CallbackInfo ci) {
        this.setDefaultState(this.getDefaultState().with(CONNECTED_UP, false).with(HempCropBlockManager.IS_TOP, false));
    }

    @Inject(method = "appendProperties", at = @At("TAIL"))
    private void appendCustomProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(HempCropBlockManager.IS_TOP);
    }

    @Inject(method = "hasRandomTicks", at = @At("HEAD"), cancellable = true)
    private void setHasRandomTicks(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(!state.get(HempCropBlockManager.IS_TOP));
    }

    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void onRandomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        HempCropBlockManager.getInstance().onRandomTick(state, world, pos, random, this);
        ci.cancel();
    }

}