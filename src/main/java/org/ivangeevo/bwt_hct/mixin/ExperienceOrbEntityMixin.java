package org.ivangeevo.bwt_hct.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.ivangeevo.bwt_hct.entities.interfaces.ExperienceOrbEntityAdded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceOrbEntity.class)
public abstract class ExperienceOrbEntityMixin extends Entity implements ExperienceOrbEntityAdded
{

    @Shadow private int amount;
    @Unique
    private static final TrackedData<Boolean> DRAGON = DataTracker.registerData(
            ExperienceOrbEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN
    );

    public ExperienceOrbEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void initDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(DRAGON, false);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        // Manually simulate the old bounding-box-based block collision
        Box box = this.getBoundingBox();
        BlockPos min = new BlockPos(
                MathHelper.floor(box.minX + 0.001),
                MathHelper.floor(box.minY - 0.01),
                MathHelper.floor(box.minZ + 0.001)
        );
        BlockPos max = new BlockPos(
                MathHelper.floor(box.maxX - 0.001),
                MathHelper.floor(box.maxY - 0.001),
                MathHelper.floor(box.maxZ - 0.001)
        );

        for (BlockPos pos : BlockPos.iterate(min, max)) {
            BlockState state = this.getWorld().getBlockState(pos);
            state.onEntityCollision(this.getWorld(), pos, this);
        }
    }

    // Disable any kind of regular updates for dragon orbs
    @Inject(method = "expensiveUpdate", at = @At("HEAD"), cancellable = true)
    private void onMerge(CallbackInfo ci) {
        if (isDragon()) {
            ci.cancel();
        }
    }

    // Change pickup behavior for dragon orbs
    @Inject(method = "onPlayerCollision", at = @At("HEAD"), cancellable = true)
    private void onPickup(PlayerEntity player, CallbackInfo ci) {
        if (this.isDragon()) {
            ci.cancel();
        }
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean("IsDragonOrb", this.dataTracker.get(DRAGON));
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readNbt(NbtCompound nbt, CallbackInfo ci) {
        this.dataTracker.set(DRAGON, nbt.getBoolean("IsDragonOrb"));
    }


    public void setDragon(boolean flag) { this.dataTracker.set(DRAGON, flag); }

    public boolean isDragon() { return this.dataTracker.get(DRAGON); }

    @Override
    public void setXPAmount(int value) {
        this.amount = value;
    }
}





