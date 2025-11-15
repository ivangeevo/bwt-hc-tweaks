package org.ivangeevo.bwt_hct.blocks.blocks;

import com.bwt.blocks.MechPowerBlockBase;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.ivangeevo.bwt_hct.entity.ModBlockEntities;
import org.ivangeevo.bwt_hct.entity.block.ArcaneVesselBE;
import org.ivangeevo.bwt_hct.entity.interfaces.ExperienceOrbEntityAdded;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class ArcaneVesselBlock extends BlockWithEntity implements MechPowerBlockBase
{

    public static final MapCodec<ArcaneVesselBlock> CODEC = createCodec(ArcaneVesselBlock::new);
    public static final DirectionProperty FACING = Properties.FACING;

    public ArcaneVesselBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.UP).with(MECH_POWERED, false));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        MechPowerBlockBase.super.appendProperties(builder);
        builder.add(FACING);
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        if (blockEntity instanceof ArcaneVesselBE vesselBE) {
            vesselBE.ejectContentsOnBlockBreak();
        }

        super.afterBreak(world, player, pos, state, blockEntity, tool);
    }

    @Override
    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!world.isClient) {
            if (entity instanceof ExperienceOrbEntity) {
                onEntityXPOrbCollidedWithBlock(world, pos, (ExperienceOrbEntity) entity);
            }
        }
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(MECH_POWERED)) {
            for (Direction dir : Direction.Type.HORIZONTAL) {
                BlockPos offsetPos = pos.offset(dir);
                BlockState targetState = world.getBlockState(offsetPos);
                if (targetState.isAir() || targetState.isReplaceable()) {
                    return state.with(FACING, dir);
                }
            }
            // Fallback if no horizontal direction is air/replaceable
            return state.with(FACING, Direction.UP);

        }
        return state.with(FACING, Direction.UP);
    }


    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        this.schedulePowerUpdate(state, world, pos);
    }

    public void schedulePowerUpdate(BlockState state, World world, BlockPos pos) {
        boolean isMechPowered = this.isReceivingMechPower(world, state, pos);
        if (isMechPowered && !this.isMechPowered(state)) {
            world.setBlockState(pos, state.with(MECH_POWERED, true));
        } else if (!isMechPowered && this.isMechPowered(state)) {
            world.setBlockState(pos, state.with(MECH_POWERED, false));
        }

    }
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ArcaneVesselBE(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (!world.isClient) {
            return validateTicker(type, ModBlockEntities.arcaneVesselEntity, ArcaneVesselBE::serverTick);
        }
        return validateTicker(type, ModBlockEntities.arcaneVesselEntity, ArcaneVesselBE::clientTick);
    }

    @Override
    public Predicate<Direction> getValidHandCrankFaces(BlockState blockState, BlockPos pos) {
        return (direction) -> true;
    }

    private void onEntityXPOrbCollidedWithBlock(World world, BlockPos pos, ExperienceOrbEntity entityXPOrb) {
        if (((ExperienceOrbEntityAdded)entityXPOrb).isDragon()) {
            return;
        }

        BlockState state = world.getBlockState(pos);

        if (state.get(MECH_POWERED)) {
            // tilted blocks can't collect
            return;
        }

        // check if item is within the collection zone

        final float fVesselHeight = 1F;

        Box collectionZone = new Box(pos.getX(), pos.getY() + fVesselHeight, pos.getZ(),
                pos.getX() + 1, pos.getY() + fVesselHeight + 0.05F, pos.getZ() + 1
        );


        if (entityXPOrb.getBoundingBox().intersects(collectionZone)) {
            boolean bSwallowed = false;

            BlockEntity be = world.getBlockEntity(pos);

            if (be instanceof ArcaneVesselBE arcaneVesselBE) {
                if (arcaneVesselBE.attemptToSwallowXPOrb(world, pos, entityXPOrb)) {
                    world.playSound(
                            null,
                            pos,
                            SoundEvents.BLOCK_LAVA_POP,
                            SoundCategory.BLOCKS,
                            0.25F,
                            ((world.getRandom().nextFloat() - world.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                }
            }
        }
    }

}
