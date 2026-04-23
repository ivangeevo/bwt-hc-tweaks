package org.btwr.bwt_hct.mixin.bwt;


import com.bwt.blocks.SawBlock;
import com.bwt.blocks.SimpleFacingBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
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

    // Special collision check for applying chopping block damage and making the chopping block dirty
    @Inject(method = "onEntityCollision", at = @At("HEAD"), cancellable = true)
    private void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (world.isClient()) return;
        if (!(entity instanceof LivingEntity livingEntity)) return;

        if (!state.get(SawBlock.MECH_POWERED)) return;

        // Get the block on in front of the saw's facing
        Direction facing = state.get(SawBlock.FACING);
        BlockPos inFrontPos = pos.offset(facing);
        BlockState inFrontState = world.getBlockState(inFrontPos);

        boolean hasChoppingBlock = inFrontState.isOf(ModBlocks.choppingBlock);
        if (!hasChoppingBlock) return;

        if (!BLADE_SHAPES.get(facing.getId())
                .getBoundingBox().offset(pos)
                .intersects(livingEntity.getBoundingBox(entity.getPose())
                        .offset(livingEntity.getPos()))
        ) return;

        // Apply chopping block damage (3x = 12.0f instead of 4.0f)
        livingEntity.damage(ModDamageTypes.of(world, ModDamageTypes.CHOPPING_BLOCK), 12.0f);

        // Make the chopping block dirty
        if (!inFrontState.get(ChoppingBlock.DIRTY)) {
            world.setBlockState(inFrontPos, inFrontState.with(ChoppingBlock.DIRTY, true));
        }

        // Cancel the original collision so we don't double-apply damage
        ci.cancel();
    }

}