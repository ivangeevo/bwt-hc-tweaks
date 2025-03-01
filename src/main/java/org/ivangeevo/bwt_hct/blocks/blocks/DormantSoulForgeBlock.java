package org.ivangeevo.bwt_hct.blocks.blocks;

import com.bwt.blocks.soul_forge.SoulForgeBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DormantSoulForgeBlock extends SoulForgeBlock {

    public DormantSoulForgeBlock(Settings settings) {
        super(settings);
    }

    // removes parent functionality
    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        return ActionResult.PASS;
    }
}
