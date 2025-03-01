package org.ivangeevo.bwt_hct.mixin;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.MechPowerBlockBase;
import com.bwt.items.BwtItems;
import com.bwt.recipes.BlockIngredient;
import com.bwt.recipes.BwtRecipes;
import com.bwt.recipes.saw.SawRecipe;
import com.bwt.recipes.saw.SawRecipeInput;
import com.bwt.sounds.BwtSoundEvents;
import com.bwt.tags.BwtBlockTags;
import com.bwt.utils.CustomItemScatterer;
import net.minecraft.block.*;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.ivangeevo.bwt_hct.util.SawLikeBlockConstants;
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
public abstract class StonecutterBlockMixin extends Block implements MechPowerBlockBase, SawLikeBlockConstants
{
    @Unique
    private static DirectionProperty FACING = Properties.FACING;

    public StonecutterBlockMixin(Settings settings) {
        super(settings);
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;with(Lnet/minecraft/state/property/Property;Ljava/lang/Comparable;)Ljava/lang/Object;"), index = 0)
    private Property<Direction> injected(Property<Direction> par1) {
        return FACING;
    }

    @Inject(method = "appendProperties", at = @At("HEAD"), cancellable = true)
    private void onAppendProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        ci.cancel();
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        MechPowerBlockBase.super.appendProperties(builder);
        builder.add(FACING);
    }



    @Inject(method = "getPlacementState", at = @At("HEAD"), cancellable = true)
    private void onGetPlacementState(ItemPlacementContext ctx, CallbackInfoReturnable<BlockState> cir) {
        cir.setReturnValue(getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite()).with(MECH_POWERED, false));
    }

    @Inject(method = "rotate", at = @At("HEAD"), cancellable = true)
    private void onRotate(BlockState state, BlockRotation rotation, CallbackInfoReturnable<BlockState> cir) {
        cir.setReturnValue(state.with(FACING, rotation.rotate(state.get(FACING))));
    }

    @Inject(method = "mirror", at = @At("HEAD"), cancellable = true)
    private void onMirror(BlockState state, BlockMirror mirror, CallbackInfoReturnable<BlockState> cir) {
        cir.setReturnValue(state.rotate(mirror.getRotation(state.get(FACING))));
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

        // note that we can't validate if the update is required here as the block will have
        // its facing set after being added
        world.scheduleBlockTick(pos, this, powerChangeTickRate);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return COLLISION_SHAPES.get(state.get(FACING).getId());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return OUTLINE_SHAPES.get(state.get(FACING).getId());
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
        scheduleUpdateIfRequired(world, state, pos);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, net.minecraft.util.math.random.Random random) {
        super.scheduledTick(state, world, pos, random);

        boolean bReceivingPower = isReceivingMechPower(world, state, pos);
        boolean bOn = isMechPowered(state);

        if (bOn != bReceivingPower) {
            emitSawParticles(world, state, pos);

            world.setBlockState(pos, state.with(MECH_POWERED, bReceivingPower));

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

    void emitSawParticles(World world, BlockState state, BlockPos pos) {
        // compute position of saw blade
        Direction facing = state.get(FACING);
        VoxelShape bladeFace = BLADE_SHAPES.get(facing.getId()).asCuboid().offset(pos.getX(), pos.getY(), pos.getZ());
        double bladeMaxX = bladeFace.getMax(Direction.Axis.X);
        double bladeMaxY = bladeFace.getMax(Direction.Axis.Y);
        double bladeMaxZ = bladeFace.getMax(Direction.Axis.Z);
        double bladeMinX = bladeFace.getMin(Direction.Axis.X);
        double bladeMinY = bladeFace.getMin(Direction.Axis.Y);
        double bladeMinZ = bladeFace.getMin(Direction.Axis.Z);
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

    protected void sawBlockToFront(World world, BlockState state, BlockPos pos) {
        BlockPos targetPos = pos.offset(state.get(FACING));
        BlockState targetState = world.getBlockState(targetPos);

        if (targetState.isIn(BlockTags.AIR)) {
            return;
        }

        SawRecipeInput recipeInput = new SawRecipeInput(targetState.getBlock());
        Optional<SawRecipe> recipe = world.getRecipeManager().getFirstMatch(
                BwtRecipes.SAW_RECIPE_TYPE,
                recipeInput,
                world
        ).map(RecipeEntry::value);
        // Cutting
        if (recipe.isEmpty()) {
            if (targetState.isIn(BwtBlockTags.SAW_BREAKS_NO_DROPS)) {
                world.breakBlock(targetPos, false);
                playBangSound(world, pos);
                return;
            }
            if (targetState.isIn(BwtBlockTags.SAW_BREAKS_DROPS_LOOT)) {
                world.breakBlock(targetPos, true);
                playBangSound(world, pos);
                return;
            }
            if (!targetState.isIn(BwtBlockTags.SURVIVES_SAW_BLOCK)) {
                breakSaw(world, pos);
            }
            return;
        }

        List<ItemStack> results = recipe.get().getResults();
        if (targetState.getBlock() instanceof SlabBlock && targetState.get(SlabBlock.TYPE) == SlabType.DOUBLE) {
            results.forEach(result -> result.setCount(result.getCount() * 2));
        }
        BlockIngredient blockIngredient = recipe.get().getIngredient();

        // The companion slab is the only partial block that doesn't just get cut regardless of collision
        if (blockIngredient.test(BwtBlocks.companionSlabBlock) && state.get(FACING).getAxis().isHorizontal()) {
            return;
        }

        if (blockIngredient.test(BwtBlocks.companionCubeBlock)) {
            world.playSound(null, pos, BwtSoundEvents.COMPANION_CUBE_DEATH, SoundCategory.BLOCKS, 1, 1);
            if (state.get(FACING).getAxis().isHorizontal()) {
                results.get(0).setCount(1);
                world.setBlockState(targetPos, BwtBlocks.companionSlabBlock.getDefaultState());
            }
            else {
                world.breakBlock(targetPos, false);
            }
        }
        else {
            world.breakBlock(targetPos, false);
        }
        playBangSound(world, pos);

        if (targetState.contains(Properties.SLAB_TYPE) && targetState.get(Properties.SLAB_TYPE).equals(SlabType.DOUBLE)) {
            results.forEach(stack -> stack.setCount(stack.getCount() * 2));
        }

        CustomItemScatterer.spawn(world, targetPos, DefaultedList.copyOf(ItemStack.EMPTY, results.toArray(new ItemStack[0])));
    }

    public void breakSaw(World world, BlockPos pos) {
        dropItemsOnBreak(world, pos);
        world.breakBlock(pos, false);
        playBangSound(world, pos, 1);
    }

    public void dropItemsOnBreak(World world, BlockPos pos) {
        ItemScatterer.spawn(world, pos, DefaultedList.copyOf(
                ItemStack.EMPTY,
                new ItemStack(BwtItems.gearItem, 1),
                new ItemStack(Items.STICK, 2),
                new ItemStack(BwtItems.sawDustItem, 2),
                new ItemStack(Items.IRON_INGOT, 2),
                new ItemStack(BwtItems.strapItem, 2)
        ));
    }

    protected void scheduleUpdateIfRequired(World world, BlockState state, BlockPos pos) {
        if (isMechPowered(state) != isReceivingMechPower(world, state, pos)) {
            world.scheduleBlockTick(pos, this, powerChangeTickRate);
            return;
        }
        if (!isMechPowered(state)) {
            return;
        }

        // check if we have something to cut in front of us
        BlockPos targetPos = pos.offset(state.get(FACING));
        BlockState targetState = world.getBlockState(targetPos);
        if (!targetState.isIn(BlockTags.AIR)) {
            world.scheduleBlockTick(pos, this, sawTimeBaseTickRate + world.random.nextInt(sawTimeTickRateVariance));
        }
    }


}
