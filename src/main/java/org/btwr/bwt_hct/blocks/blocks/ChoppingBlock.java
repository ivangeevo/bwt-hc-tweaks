package org.btwr.bwt_hct.blocks.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;

public class ChoppingBlock extends Block {

    public static final BooleanProperty DIRTY = BooleanProperty.of("dirty");

    public ChoppingBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(DIRTY, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(DIRTY);
    }

}