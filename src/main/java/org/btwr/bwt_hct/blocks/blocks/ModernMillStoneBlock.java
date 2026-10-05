package org.btwr.bwt_hct.blocks.blocks;

import com.bwt.blocks.mill_stone.MillStoneBlock;
import com.bwt.sounds.BwtSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.btwr.bwt_hct.entity.ModBlockEntities;
import org.btwr.bwt_hct.entity.block.ModernMillStoneBE;
import org.jetbrains.annotations.Nullable;

public class ModernMillStoneBlock extends MillStoneBlock {

    public static final BooleanProperty FULL = BooleanProperty.create("full");

    public ModernMillStoneBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.defaultBlockState().setValue(FULL, false));
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FULL);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ModernMillStoneBE(pos, state);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Block.box(0.0,0.0,0.0,16.0,15.0,16.0);
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        if (this.isMechPowered(state)) {
            this.emitGearBoxParticles(world, pos, random);
            if (random.nextInt(4) == 0) {
                this.playMechSound(world, pos);
            }
        }
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (!world.isClientSide) {
            if (world.getBlockEntity(pos) instanceof ModernMillStoneBE millStoneBE) {
                if (millStoneBE.onUseByPlayer(player)) {
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> givenType) {
        return validateTicker(world, givenType);
    }

    @Nullable
    protected static <A extends BlockEntity> BlockEntityTicker<A> validateTicker(Level world, BlockEntityType<A> givenType) {
        return world.isClientSide ? null : BaseEntityBlock.createTickerHelper(givenType, ModBlockEntities.modernMillStoneEntity, ModernMillStoneBE::tick);
    }

    private void playMechSound(Level world, BlockPos pos) {
        world.playLocalSound(pos, BwtSoundEvents.MILL_STONE_GRIND, SoundSource.BLOCKS,
                1.5F + ( world.random.nextFloat() * 0.1F ),
                0.5F + ( world.random.nextFloat() * 0.1F ),
                false);
    }

    private void emitGearBoxParticles(Level world, BlockPos pos, RandomSource random) {
        for(int iTempCount = 0; iTempCount < 5; ++iTempCount) {
            float smokeX = (float)pos.getX() + random.nextFloat();
            float smokeY = (float)pos.getY() + random.nextFloat() * 0.5F + 1.0F;
            float smokeZ = (float)pos.getZ() + random.nextFloat();
            world.addParticle(ParticleTypes.SMOKE, smokeX, smokeY, smokeZ, 0.0, 0.0, 0.0);
        }
    }

}