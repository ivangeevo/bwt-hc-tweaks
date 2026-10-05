package org.btwr.bwt_hct.mixin.bwt;


import com.bwt.blocks.SawBlock;
import com.bwt.blocks.SimpleFacingBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.btwr.bwt_hct.blocks.ModBlocks;
import org.btwr.bwt_hct.blocks.blocks.ChoppingBlock;
import org.btwr.bwt_hct.config.BWT_HCTConfig;
import org.btwr.bwt_hct.world.ModDamageTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.btwr.bwt_hct.util.SawLikeBlockConstants.BLADE_SHAPES;

@Mixin(SawBlock.class)
public abstract class SawBlockMixin extends SimpleFacingBlock {

    protected SawBlockMixin(Properties settings) {
        super(settings);
    }

    // Modifying the tick rate argument to the retail BTW value
    @ModifyArg(method = "scheduleUpdateIfRequired",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;I)V", ordinal = 1), index = 2)
    private int modifySawBreakSpeed(int par3) {
        return BWT_HCTConfig.sawBlockBreakSpeed.get();
    }

    // Special collision check for applying chopping block damage and making the chopping block dirty
    @Inject(method = "entityInside", at = @At("HEAD"), cancellable = true)
    private void onEntityCollision(BlockState state, Level world, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (world.isClientSide()) return;
        if (!(entity instanceof LivingEntity livingEntity)) return;

        if (!state.getValue(SawBlock.MECH_POWERED)) return;

        // Get the block on in front of the saw's facing
        Direction facing = state.getValue(SawBlock.FACING);
        BlockPos inFrontPos = pos.relative(facing);
        BlockState inFrontState = world.getBlockState(inFrontPos);

        boolean hasChoppingBlock = inFrontState.is(ModBlocks.choppingBlock);
        if (!hasChoppingBlock) return;

        if (!BLADE_SHAPES.get(facing.get3DDataValue())
                .bounds().move(pos)
                .intersects(livingEntity.getLocalBoundsForPose(entity.getPose())
                        .move(livingEntity.position()))
        ) return;

        // Apply chopping block damage (3x = 12.0f instead of 4.0f)
        livingEntity.hurt(ModDamageTypes.of(world, ModDamageTypes.CHOPPING_BLOCK), 12.0f);

        // Make the chopping block dirty
        if (!inFrontState.getValue(ChoppingBlock.DIRTY)) {
            world.setBlockAndUpdate(inFrontPos, inFrontState.setValue(ChoppingBlock.DIRTY, true));
        }

        // Cancel the original collision so we don't double-apply damage
        ci.cancel();
    }

}