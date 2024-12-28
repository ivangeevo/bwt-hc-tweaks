package org.ivangeevo.bwt_hct.entities;

import com.bwt.blocks.AxleBlock;
import com.bwt.blocks.BwtBlocks;
import com.bwt.utils.rectangular_entity.RectangularEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.ArrayList;
import java.util.function.Predicate;

public abstract class VerticalMechPowerSourceEntity extends RectangularEntity {
    protected float rotation = 0;
    protected float prevRotation = 0;

    protected int ticksBeforeNextFullUpdate = 20;

    protected static final TrackedData<Float> rotationSpeed = DataTracker.registerData(VerticalMechPowerSourceEntity.class, TrackedDataHandlerRegistry.FLOAT);
    protected static final TrackedData<Integer> DAMAGE_WOBBLE_TICKS = DataTracker.registerData(VerticalMechPowerSourceEntity.class, TrackedDataHandlerRegistry.INTEGER);
    protected static final TrackedData<Integer> DAMAGE_WOBBLE_SIDE = DataTracker.registerData(VerticalMechPowerSourceEntity.class, TrackedDataHandlerRegistry.INTEGER);
    protected static final TrackedData<Float> DAMAGE_WOBBLE_STRENGTH = DataTracker.registerData(VerticalMechPowerSourceEntity.class, TrackedDataHandlerRegistry.FLOAT);

    public VerticalMechPowerSourceEntity(EntityType<? extends VerticalMechPowerSourceEntity> type, World world) {
        super(type, world);
        this.intersectionChecked = true;
    }

    public VerticalMechPowerSourceEntity(EntityType<? extends VerticalMechPowerSourceEntity> type, World world, Vec3d pos, Direction facing) {
        this(type, world);
        setPosition(pos);
        setYaw(facing.asRotation());
    }

    public interface Factory {
        VerticalMechPowerSourceEntity create(World world, Vec3d pos, Direction facing);
    }


    abstract public boolean tryToSpawn(PlayerEntity player);
    abstract public Predicate<BlockPos> getBlockInterferencePredicate();
    abstract float computeRotation();
    abstract float getSpeedToPowerThreshold();

    @Override
    public double getEyeY() {
        return this.getHeight() / 2;
    }

    @Override
    protected MoveEffect getMoveEffect() {
        return MoveEffect.NONE;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(rotationSpeed, 0f);
        builder.add(DAMAGE_WOBBLE_TICKS, 0);
        builder.add(DAMAGE_WOBBLE_SIDE, 1);
        builder.add(DAMAGE_WOBBLE_STRENGTH, 0.0f);
    }

    public float getRotation() {
        return rotation;
    }

    protected void setRotation(float rotation) {
        rotation = (rotation + 360f) % 360f;
        this.prevRotation = this.rotation;
        this.rotation = rotation;
    }

    public float getPrevRotation() {
        return prevRotation;
    }

    public float getRotationSpeed() {
        return getDataTracker().get(rotationSpeed);
    }

    public void setRotationSpeed(float speed) {
        getDataTracker().set(rotationSpeed, speed);
    }

    public void setDamageWobbleTicks(int damageWobbleTicks) {
        this.dataTracker.set(DAMAGE_WOBBLE_TICKS, damageWobbleTicks);
    }

    public void setDamageWobbleSide(int damageWobbleSide) {
        this.dataTracker.set(DAMAGE_WOBBLE_SIDE, damageWobbleSide);
    }

    public void setDamageWobbleStrength(float damageWobbleStrength) {
        this.dataTracker.set(DAMAGE_WOBBLE_STRENGTH, damageWobbleStrength);
    }

    public float getDamageWobbleStrength() {
        return this.dataTracker.get(DAMAGE_WOBBLE_STRENGTH);
    }

    public int getDamageWobbleTicks() {
        return this.dataTracker.get(DAMAGE_WOBBLE_TICKS);
    }

    public int getDamageWobbleSide() {
        return this.dataTracker.get(DAMAGE_WOBBLE_SIDE);
    }

    @Override
    public boolean canHit() {
        return true;
    }

    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    public boolean isPushedByFluids() {
        return false;
    }

    @Override
    public PistonBehavior getPistonBehavior() {
        return PistonBehavior.DESTROY;
    }

    public boolean tryToSpawn(PlayerEntity player, Text blockBlockedErrorMessage, Text entityBlockedErrorMessage) {
        if (player instanceof ServerPlayerEntity) {
            player = null;
        }

        if (placementBlockedByBlock()) {
            if(player != null) {
                player.sendMessage(blockBlockedErrorMessage);
            }
            return false;
        }
        if (placementBlockedByEntity()) {
            if(player != null) {
                player.sendMessage(entityBlockedErrorMessage);
            }
            return false;
        }

        if (placementHasBadAxleState()) {
            return false;
        }

        setRotationSpeed(computeRotation());
        World world = getWorld();
        world.spawnEntity(this);
        return true;
    }

    public boolean placementBlockedByBlock() {
        Predicate<BlockPos> blockInterferencePredicate = getBlockInterferencePredicate();
        return BlockPos.stream(getBoundingBox())
                // Ignore the axle we're on
                .filter(blockPos -> !blockPos.equals(this.getBlockPos()))
                .anyMatch(blockInterferencePredicate);
    }

    @Override
    protected void onBlockCollision(BlockState state) {
        destroyWithDrop();
    }

    public boolean placementBlockedByEntity() {
        ArrayList<Entity> anyEntities = new ArrayList<>();
        getWorld().collectEntitiesByType(
                TypeFilter.instanceOf(Entity.class),
                getBoundingBox(),
                entity -> entity != this && EntityPredicates.EXCEPT_SPECTATOR.test(entity) && !(entity instanceof ItemEntity),
                anyEntities, 1);
        return !anyEntities.isEmpty();
    }

    public boolean placementHasBadAxleState() {
        World world = getWorld();
        BlockPos pos = getBlockPos();

        // Check the block at this position is an axle block
        BlockState centerBlock = world.getBlockState(pos);
        if (!centerBlock.isOf(BwtBlocks.axleBlock) && !centerBlock.isOf(BwtBlocks.axlePowerSourceBlock)) {
            return true;
        }

        // Check that there are exactly 3 axles above and 3 axles below this block
        for (int offset = 1; offset <= 3; offset++) {
            BlockState aboveBlock = world.getBlockState(pos.up(offset));
            BlockState belowBlock = world.getBlockState(pos.down(offset));
            if (!aboveBlock.isOf(BwtBlocks.axleBlock) || !belowBlock.isOf(BwtBlocks.axleBlock)) {
                return true;
            }
        }

        return world.getBlockState(pos.up(4)).isOf(BwtBlocks.axleBlock)
                || world.getBlockState(pos.down(4)).isOf(BwtBlocks.axleBlock);

    }


    @Override
    public void tick() {
        super.tick();

        if (isRemoved()) {
            return;
        }
        if (this.getDamageWobbleTicks() > 0) {
            this.setDamageWobbleTicks(this.getDamageWobbleTicks() - 1);
        }
        if (this.getDamageWobbleStrength() > 0.0f) {
            this.setDamageWobbleStrength(this.getDamageWobbleStrength() - 1.0f);
        }

        if (getWorld().isClient) {
            updateRotation();
        }
        else {
            ticksBeforeNextFullUpdate--;
            if (ticksBeforeNextFullUpdate <= 0) {
                ticksBeforeNextFullUpdate = 20;
                fullUpdate();
            }
        }
        getWorld()
                .getOtherEntities(this, this.getBoundingBox().expand(0.1f, 0.01f, 0.1f), EntityPredicates.canBePushedBy(this))
                .forEach(this::pushAwayFrom);
    }

    protected void updateRotation() {
        setRotation(rotation + this.getDataTracker().get(rotationSpeed));
    }

    protected void fullUpdate() {
        if (placementBlockedByBlock() || placementHasBadAxleState()) {
            destroyWithDrop();
            return;
        }

        setRotationSpeed(computeRotation());

        setHostAxlePower(Math.abs(getRotationSpeed()) > getSpeedToPowerThreshold());
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.getWorld().isClient || this.isRemoved()) {
            return true;
        }
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        this.setDamageWobbleSide(-this.getDamageWobbleSide());
        this.setDamageWobbleTicks(10);
        this.scheduleVelocityUpdate();
        this.setDamageWobbleStrength(this.getDamageWobbleStrength() + amount * 10.0f);
        this.emitGameEvent(GameEvent.ENTITY_DAMAGE, source.getAttacker());
        boolean instantKill = source.getAttacker() instanceof PlayerEntity && ((PlayerEntity)source.getAttacker()).getAbilities().creativeMode;
        if (instantKill) {
            discard();
            return true;
        }
        if (this.getDamageWobbleStrength() > 40.0f) {
            destroyWithDrop();
        }
        return true;
    }

    public void destroyWithDrop() {
        if (isRemoved()) return;
        dropStack(getPickBlockStack(), 0.5f);
        kill();
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        setHostAxlePower(false);
    }

    protected void setHostAxlePower(boolean powered) {
        World world = getWorld();
        BlockPos pos = getBlockPos();
        BlockState hostBlockState = world.getBlockState(pos);
        if (!powered && hostBlockState.isOf(BwtBlocks.axlePowerSourceBlock)) {
            world.removeBlock(pos, false);
            world.setBlockState(pos, BwtBlocks.axleBlock.getDefaultState()
                    .with(AxleBlock.AXIS, hostBlockState.get(AxleBlock.AXIS)));
        }
        if (powered && hostBlockState.isOf(BwtBlocks.axleBlock)) {
            world.setBlockState(pos, BwtBlocks.axlePowerSourceBlock.getDefaultState()
                    .with(AxleBlock.AXIS, hostBlockState.get(AxleBlock.AXIS)));
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putFloat("rotationSpeed", dataTracker.get(rotationSpeed));
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        this.dataTracker.set(rotationSpeed, nbt.getFloat("rotationSpeed"));
    }
}
