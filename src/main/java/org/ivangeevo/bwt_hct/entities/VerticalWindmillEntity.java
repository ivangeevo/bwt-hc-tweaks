package org.ivangeevo.bwt_hct.entities;

import com.bwt.blocks.AxleBlock;
import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.GearBoxBlock;
import com.bwt.items.BwtItems;
import com.bwt.utils.rectangular_entity.EntityRectDimensions;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.ivangeevo.bwt_hct.entities.ModEntities;
import org.ivangeevo.bwt_hct.entities.VerticalMechPowerSourceEntity;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class VerticalWindmillEntity extends VerticalMechPowerSourceEntity
{
    public static final int NUM_SAILS = 4;

    public static final float height = 6.8F;
    public static final float width = 8.8F;
    public static final float length = 0.8f;

    private static final float rotationPerTick = -0.12F;
    private static final float rotationPerTickInStorm = -2.0F;
    private static final float rotationPerTickInNether = -0.07F;
    private static final int secondsInStormBeforeOverpower = 30;

    protected static final TrackedData<Integer> dyeIndex = DataTracker.registerData(VerticalWindmillEntity.class, TrackedDataHandlerRegistry.INTEGER);
    protected static final List<TrackedData<Integer>> sailColors = IntStream.range(0, NUM_SAILS).mapToObj(i ->
            DataTracker.registerData(VerticalWindmillEntity.class, TrackedDataHandlerRegistry.INTEGER)).collect(Collectors.toList());
    protected int overpowerTimer = 0;

    public VerticalWindmillEntity(EntityType<? extends VerticalWindmillEntity> entityType, World world) {
        super(entityType, world);
    }

    public VerticalWindmillEntity(World world, Vec3d pos, Direction facing) {
        super(ModEntities.verticalWindmillEntity, world, pos, facing);
    }

    @Override
    public EntityRectDimensions getRectDimensions() {
        return EntityRectDimensions.fixed(VerticalWindmillEntity.width, VerticalWindmillEntity.height, VerticalWindmillEntity.length);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(dyeIndex, 0);
        sailColors.forEach(bladeColor -> builder.add(bladeColor, DyeColor.WHITE.getId()));
    }

    public DyeColor getSailColor(int index) {
        return DyeColor.byId(dataTracker.get(sailColors.get(index)));
    }

    public void setSailColor(int index, DyeColor dyeColor) {
        dataTracker.set(sailColors.get(index), dyeColor.getId());
    }

    @Override
    public boolean tryToSpawn(PlayerEntity player) {
        return super.tryToSpawn(
                player,
                Text.of("Not enough room to place Vertical Wind Mill (They are friggin HUGE!)"),
                Text.of("Vertical Wind Mill placement is obstructed by something, or by you")
        );
    }

    @Override
    public boolean shouldRender(double distance) {
        double d = 128.0 * getRenderDistanceMultiplier();
        return distance < d * d;
    }

    @Override
    public Predicate<BlockPos> getBlockInterferencePredicate() {
        return blockPos -> !getWorld().getBlockState(blockPos).isIn(BlockTags.AIR);
    }

    @Override
    float getSpeedToPowerThreshold() {
        return 0.01f;
    }

    @Override
    public float computeRotation() {
        World world = getWorld();
        // Nether
        if (world.getDimension().ultrawarm()) {
            return rotationPerTickInNether;
        }
        // End dimension or modded
        else if (!world.getDimension().natural()) {
            return 0.0f;
        }
        // Overworld, sky blocked
        else if (!world.isSkyVisible(getBlockPos())) {
            return 0.0f;
        }
        // Overworld, storming
        else if (world.isRaining() && world.isThundering()) {
            return rotationPerTickInStorm;
        }
        // Overworld, not raining
        return rotationPerTick;
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        ItemStack stackInHand = player.getStackInHand(hand);
        if (!(stackInHand.getItem() instanceof DyeItem dyeItem)) {
            return super.interact(player, hand);
        }

        DyeColor dyeColor = dyeItem.getColor();
        int dyeIdx = getDataTracker().get(dyeIndex);
        if (dyeColor.equals(getSailColor(dyeIdx))) {
            getDataTracker().set(dyeIndex, (dyeIdx + 1) % NUM_SAILS);
            return ActionResult.SUCCESS;
        }
        this.setSailColor(dyeIdx, dyeColor);
        getDataTracker().set(dyeIndex, (dyeIdx + 1) % NUM_SAILS);
        if (!player.getAbilities().creativeMode) {
            stackInHand.decrement(1);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    protected void fullUpdate() {
        super.fullUpdate();
        if (Math.abs(getRotationSpeed()) < Math.abs(rotationPerTickInStorm)) {
            overpowerTimer = 0;
            return;
        }
        overpowerTimer++;
        if (overpowerTimer >= secondsInStormBeforeOverpower) {
            breakConnectedGearBoxes();
        }
    }

    protected void breakConnectedGearBoxes() {
        BlockPos hostAxlePos = getBlockPos();
        BlockState hostAxleState = getWorld().getBlockState(hostAxlePos);

        // Bad block type
        if (!hostAxleState.isOf(BwtBlocks.axleBlock) && !hostAxleState.isOf(BwtBlocks.axlePowerSourceBlock)) {
            return;
        }
        Direction.Axis hostAxleAxis = hostAxleState.get(AxleBlock.AXIS);
        for (Direction.AxisDirection axisDirection : Direction.AxisDirection.values()) {
            for (int i = 1; i <= 4; i++) {
                Direction direction = Direction.from(hostAxleAxis, axisDirection);
                BlockPos connectedPos = hostAxlePos.offset(direction, i);
                BlockState connectedState = getWorld().getBlockState(connectedPos);
                if (connectedState.isOf(BwtBlocks.gearBoxBlock) && connectedState.get(GearBoxBlock.FACING).equals(direction.getOpposite())) {
                    GearBoxBlock.breakGearBox(getWorld(), connectedPos);
                    break;
                }
                if (!connectedState.isOf(BwtBlocks.axleBlock) && !connectedState.isOf(BwtBlocks.axlePowerSourceBlock)) {
                    break;
                }
                if (!connectedState.get(AxleBlock.AXIS).equals(hostAxleAxis)) {
                    break;
                }
            }
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("dyeIndex", dataTracker.get(dyeIndex));
        for (int i = 0; i < NUM_SAILS; i++) {
            nbt.putInt("sail" + i + "Color", dataTracker.get(sailColors.get(i)));
        }
        nbt.putInt("overpowerTimer", overpowerTimer);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        dataTracker.set(dyeIndex, nbt.getInt("dyeIndex"));
        for (int i = 0; i < NUM_SAILS; i++) {
            if (nbt.contains("sail" + i + "Color", NbtCompound.INT_TYPE)) {
                dataTracker.set(sailColors.get(i), nbt.getInt("sail" + i + "Color"));
            }
        }
        overpowerTimer = nbt.getInt("overpowerTimer");
    }

    @Override
    public ItemStack getPickBlockStack() {
        return new ItemStack(BwtItems.windmillItem);
    }
}
