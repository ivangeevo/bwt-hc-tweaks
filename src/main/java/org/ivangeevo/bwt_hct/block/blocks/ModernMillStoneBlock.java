package org.ivangeevo.bwt_hct.block.blocks;

import com.bwt.block_entities.BwtBlockEntities;
import com.bwt.blocks.mill_stone.MillStoneBlock;
import com.bwt.blocks.mill_stone.MillStoneBlockEntity;
import com.bwt.sounds.BwtSoundEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.ivangeevo.bwt_hct.entity.ModBlockEntities;
import org.ivangeevo.bwt_hct.entity.block.ModernMillstoneBE;
import org.jetbrains.annotations.Nullable;

public class ModernMillStoneBlock extends MillStoneBlock {

    public static final BooleanProperty FULL = BooleanProperty.of("full");

    public ModernMillStoneBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(FULL, false));
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FULL);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ModernMillstoneBE(pos, state);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return Block.createCuboidShape(0.0,0.0,0.0,16.0,15.0,16.0);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        ItemStack heldStack = player.getStackInHand(player.getActiveHand());
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof ModernMillstoneBE millStoneBE) {
            if (handleItemRetrieval(world, pos, player, millStoneBE)) {
                return ActionResult.SUCCESS;
            } else if (handleItemInsertion(world, player, heldStack, millStoneBE)) {
                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (this.isMechPowered(state)) {
            this.emitGearBoxParticles(world, pos, random);
            if (random.nextInt(4) == 0) {
                this.playMechSound(world, pos);
            }

        }
    }

    private boolean handleItemRetrieval(World world, BlockPos pos, PlayerEntity player, ModernMillstoneBE millStoneBE) {
        if (millStoneBE.inventory.isEmpty()) {
            return false;
        }
        millStoneBE.retrieveItem(world, player);
        world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS);
        return true;
    }

    private boolean handleItemInsertion(World world, PlayerEntity player, ItemStack heldStack, ModernMillstoneBE millStoneBE) {
        if (heldStack.isEmpty() || millStoneBE.getRecipeFor(heldStack).isEmpty()) {
            return false;
        }

        return !world.isClient() && millStoneBE.inventory.isEmpty() && millStoneBE.addItem(player,
                player.getAbilities().creativeMode ? heldStack.copy() : heldStack);
    }

    private void playMechSound(World world, BlockPos pos) {
        world.playSoundAtBlockCenter(pos, BwtSoundEvents.MILL_STONE_GRIND, SoundCategory.BLOCKS,
                1.5F + ( world.random.nextFloat() * 0.1F ),
                0.5F + ( world.random.nextFloat() * 0.1F ),
                false);
    }

    private void emitGearBoxParticles(World world, BlockPos pos, Random random) {
        for(int iTempCount = 0; iTempCount < 5; ++iTempCount) {
            float smokeX = (float)pos.getX() + random.nextFloat();
            float smokeY = (float)pos.getY() + random.nextFloat() * 0.5F + 1.0F;
            float smokeZ = (float)pos.getZ() + random.nextFloat();
            world.addParticle(ParticleTypes.SMOKE, smokeX, smokeY, smokeZ, 0.0, 0.0, 0.0);
        }

    }


    @Nullable
    protected static <A extends BlockEntity> BlockEntityTicker<A> validateTicker(World world, BlockEntityType<A> givenType) {
        return world.isClient ? null : BlockWithEntity.validateTicker(givenType, ModBlockEntities.MODERN_MILLSTONE, ModernMillstoneBE::tick);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> givenType) {
        return ModernMillStoneBlock.validateTicker(world, givenType);
    }
}
