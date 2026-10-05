package org.btwr.bwt_hct.mixin.bwt;

import com.bwt.blocks.HempCropBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.btwr.bwt_hct.blocks.HempCropBlockManager;
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

    public HempCropBlockMixin(Properties settings) {
        super(settings);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void initProperty(Properties settings, CallbackInfo ci) {
        this.registerDefaultState(this.defaultBlockState().setValue(CONNECTED_UP, false).setValue(HempCropBlockManager.IS_TOP, false));
    }

    @Inject(method = "createBlockStateDefinition", at = @At("TAIL"))
    private void appendCustomProperties(StateDefinition.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(HempCropBlockManager.IS_TOP);
    }

    @Inject(method = "isRandomlyTicking", at = @At("HEAD"), cancellable = true)
    private void setHasRandomTicks(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(!state.getValue(HempCropBlockManager.IS_TOP));
    }

    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void onRandomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random, CallbackInfo ci) {
        HempCropBlockManager.getInstance().onRandomTick(state, world, pos, random, this);
        ci.cancel();
    }

}