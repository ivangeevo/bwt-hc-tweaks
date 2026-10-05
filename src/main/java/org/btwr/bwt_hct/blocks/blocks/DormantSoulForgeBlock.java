package org.btwr.bwt_hct.blocks.blocks;

import com.bwt.blocks.soul_forge.SoulForgeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class DormantSoulForgeBlock extends SoulForgeBlock {

    public DormantSoulForgeBlock(Properties settings) {
        super(settings);
    }

    // removes parent functionality
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        return InteractionResult.PASS;
    }

}