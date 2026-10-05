package org.btwr.bwt_hct.mixin;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.MechPowerBlockBase;
import com.bwt.items.BwtItems;
import com.bwt.recipes.BlockIngredient;
import com.bwt.recipes.BwtRecipes;
import com.bwt.recipes.saw.SawRecipe;
import com.bwt.recipes.saw.SawRecipeInput;
import com.bwt.sounds.BwtSoundEvents;
import com.bwt.tags.BwtBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StonecutterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.btwr.bwt_hct.util.SawLikeBlockConstants;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(StonecutterBlock.class)
public abstract class StonecutterBlockMixin extends Block implements MechPowerBlockBase, SawLikeBlockConstants {

    @Unique private static DirectionProperty FACING = BlockStateProperties.FACING;

    public StonecutterBlockMixin(Properties settings) {
        super(settings);
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;setValue(Lnet/minecraft/world/level/block/state/properties/Property;Ljava/lang/Comparable;)Ljava/lang/Object;"), index = 0)
    private Property<Direction> injected(Property<Direction> par1) {
        return FACING;
    }

    @Inject(method = "createBlockStateDefinition", at = @At("HEAD"), cancellable = true)
    private void onAppendProperties(StateDefinition.Builder<Block, BlockState> builder, CallbackInfo ci) {
        MechPowerBlockBase.super.appendProperties(builder);
        builder.add(FACING);
        ci.cancel();
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {}

    @Inject(method = "getStateForPlacement", at = @At("HEAD"), cancellable = true)
    private void onGetPlacementState(BlockPlaceContext ctx, CallbackInfoReturnable<BlockState> cir) {
        cir.setReturnValue(defaultBlockState().setValue(FACING, ctx.getNearestLookingDirection().getOpposite()).setValue(MECH_POWERED, false));
    }

    @Inject(method = "rotate", at = @At("HEAD"), cancellable = true)
    private void onRotate(BlockState state, Rotation rotation, CallbackInfoReturnable<BlockState> cir) {
        cir.setReturnValue(state.setValue(FACING, rotation.rotate(state.getValue(FACING))));
    }

    @Inject(method = "mirror", at = @At("HEAD"), cancellable = true)
    private void onMirror(BlockState state, Mirror mirror, CallbackInfoReturnable<BlockState> cir) {
        cir.setReturnValue(state.rotate(mirror.getRotation(state.getValue(FACING))));
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(world, pos, state, placer, itemStack);

        // note that we can't validate if the update is required here as the block will have
        // its facing set after being added
        world.scheduleTick(pos, this, powerChangeTickRate);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return COLLISION_SHAPES.get(state.getValue(FACING).get3DDataValue());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return OUTLINE_SHAPES.get(state.getValue(FACING).get3DDataValue());
    }

    @Override
    protected void neighborChanged(BlockState state, Level world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborChanged(state, world, pos, sourceBlock, sourcePos, notify);
        scheduleUpdateIfRequired(world, state, pos);
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, net.minecraft.util.RandomSource random) {
        super.tick(state, world, pos, random);

        boolean bReceivingPower = isReceivingMechPower(world, state, pos);
        boolean bOn = isMechPowered(state);

        if (bOn != bReceivingPower) {
            emitSawParticles(world, state, pos);

            world.setBlockAndUpdate(pos, state.setValue(MECH_POWERED, bReceivingPower));

            if (bReceivingPower) {
                playBangSound(world, pos);
                // the saw doesn't cut on the update in which it is powered, so check if another
                // update is required
                scheduleUpdateIfRequired(world, state, pos);
            }
        }
        else if (bOn) {
            sawBlockToFront(world, state, pos);
        }
    }

    void emitSawParticles(Level world, BlockState state, BlockPos pos) {
        // compute position of saw blade
        Direction facing = state.getValue(FACING);
        VoxelShape bladeFace = BLADE_SHAPES.get(facing.get3DDataValue()).singleEncompassing().move(pos.getX(), pos.getY(), pos.getZ());
        double bladeMaxX = bladeFace.max(Direction.Axis.X);
        double bladeMaxY = bladeFace.max(Direction.Axis.Y);
        double bladeMaxZ = bladeFace.max(Direction.Axis.Z);
        double bladeMinX = bladeFace.min(Direction.Axis.X);
        double bladeMinY = bladeFace.min(Direction.Axis.Y);
        double bladeMinZ = bladeFace.min(Direction.Axis.Z);
        double fBladeXPos = (bladeMaxX + bladeMinX) / 2;
        double fBladeYPos = (bladeMaxY + bladeMinY) / 2;
        double fBladeZPos = (bladeMaxZ + bladeMinZ) / 2;

        for (int counter = 0; counter < 5; counter++) {
            double smokeX = fBladeXPos + ((world.random.nextFloat() - 0.5f) * (bladeMaxX - bladeMinX));
            double smokeY = fBladeYPos + ((world.random.nextFloat() * 0.10f) * (bladeMaxY - bladeMinY));
            double smokeZ = fBladeZPos + ((world.random.nextFloat() - 0.5f) * (bladeMaxZ - bladeMinZ));
            world.addParticle(ParticleTypes.SMOKE, smokeX, smokeY, smokeZ, 0d, 0d, 0d);
        }
    }

    protected void sawBlockToFront(Level world, BlockState state, BlockPos pos) {
        BlockPos targetPos = pos.relative(state.getValue(FACING));
        BlockState targetState = world.getBlockState(targetPos);

        if (targetState.is(BlockTags.AIR)) {
            return;
        }

        SawRecipeInput recipeInput = new SawRecipeInput(targetState.getBlock());
        Optional<SawRecipe> recipe = world.getRecipeManager().getRecipeFor(
                BwtRecipes.SAW_RECIPE_TYPE,
                recipeInput,
                world
        ).map(RecipeHolder::value);
        // Cutting
        if (recipe.isEmpty()) {
            if (targetState.is(BwtBlockTags.SAW_BREAKS_NO_DROPS)) {
                world.destroyBlock(targetPos, false);
                playBangSound(world, pos);
                return;
            }
            if (targetState.is(BwtBlockTags.SAW_BREAKS_DROPS_LOOT)) {
                world.destroyBlock(targetPos, true);
                playBangSound(world, pos);
                return;
            }
            if (!targetState.is(BwtBlockTags.SURVIVES_SAW_BLOCK)) {
                breakSaw(world, pos);
            }
            return;
        }

        List<ItemStack> results = recipe.get().getResults();
        if (targetState.getBlock() instanceof SlabBlock && targetState.getValue(SlabBlock.TYPE) == SlabType.DOUBLE) {
            results.forEach(result -> result.setCount(result.getCount() * 2));
        }
        BlockIngredient blockIngredient = recipe.get().getIngredient();

        // The companion slab is the only partial block that doesn't just get cut regardless of collision
        if (blockIngredient.test(BwtBlocks.companionSlabBlock) && state.getValue(FACING).getAxis().isHorizontal()) {
            return;
        }

        if (blockIngredient.test(BwtBlocks.companionCubeBlock)) {
            world.playSound(null, pos, BwtSoundEvents.COMPANION_CUBE_DEATH, SoundSource.BLOCKS, 1, 1);
            if (state.getValue(FACING).getAxis().isHorizontal()) {
                results.get(0).setCount(1);
                world.setBlockAndUpdate(targetPos, BwtBlocks.companionSlabBlock.defaultBlockState());
            }
            else {
                world.destroyBlock(targetPos, false);
            }
        }
        else {
            world.destroyBlock(targetPos, false);
        }
        playBangSound(world, pos);

        if (targetState.hasProperty(BlockStateProperties.SLAB_TYPE) && targetState.getValue(BlockStateProperties.SLAB_TYPE).equals(SlabType.DOUBLE)) {
            results.forEach(stack -> stack.setCount(stack.getCount() * 2));
        }

        Containers.dropContents(world, targetPos, NonNullList.of(ItemStack.EMPTY, results.toArray(new ItemStack[0])));
    }

    public void breakSaw(Level world, BlockPos pos) {
        dropItemsOnBreak(world, pos);
        world.destroyBlock(pos, false);
        playBangSound(world, pos, 1);
    }

    public void dropItemsOnBreak(Level world, BlockPos pos) {
        Containers.dropContents(world, pos, NonNullList.of(
                ItemStack.EMPTY,
                new ItemStack(BwtItems.gearItem, 1),
                new ItemStack(Items.STICK, 2),
                new ItemStack(BwtItems.sawDustItem, 2),
                new ItemStack(Items.IRON_INGOT, 2),
                new ItemStack(BwtItems.strapItem, 2)
        ));
    }

    protected void scheduleUpdateIfRequired(Level world, BlockState state, BlockPos pos) {
        if (isMechPowered(state) != isReceivingMechPower(world, state, pos)) {
            world.scheduleTick(pos, this, powerChangeTickRate);
            return;
        }

        if (!isMechPowered(state)) {
            return;
        }

        // check if we have something to cut in front of us
        BlockPos targetPos = pos.relative(state.getValue(FACING));
        BlockState targetState = world.getBlockState(targetPos);
        if (!targetState.is(BlockTags.AIR)) {
            world.scheduleTick(pos, this, sawTimeBaseTickRate + world.random.nextInt(sawTimeTickRateVariance));
        }
    }

}