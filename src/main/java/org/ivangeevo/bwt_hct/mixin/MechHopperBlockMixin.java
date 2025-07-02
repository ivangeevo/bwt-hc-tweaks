package org.ivangeevo.bwt_hct.mixin;

import com.bwt.blocks.mech_hopper.MechHopperBlock;
import com.bwt.blocks.mech_hopper.MechHopperBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.ivangeevo.bwt_hct.entities.interfaces.ExperienceOrbEntityAdded;
import org.ivangeevo.bwt_hct.entities.interfaces.MechHopperBlockEntityAdded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MechHopperBlock.class)
public abstract class MechHopperBlockMixin extends BlockWithEntity
{

    protected MechHopperBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "onSteppedOn", at = @At("HEAD"))
    private void onDragonOrbStepOn(World world, BlockPos pos, BlockState state, Entity entity, CallbackInfo ci) {
        if (world.isClient) {
            return;
        }

        if (entity instanceof ExperienceOrbEntity xpOrbEntity) {
            onEntityXPOrbCollidedWithBlock(world, pos, xpOrbEntity);
        }
    }

    @Unique
    private void onEntityXPOrbCollidedWithBlock(World world, BlockPos pos, ExperienceOrbEntity entityXPOrb) {
        boolean isDragonOrb = ((ExperienceOrbEntityAdded) entityXPOrb).isDragon();
        if (!isDragonOrb) {
            return;
        }

        // check if item is within the collection zone

        final float fHopperHeight = 1F;

        Box collectionZone = new Box(pos.getX(), pos.getY() + fHopperHeight, pos.getZ(),
                pos.getX() + 1, pos.getY() + fHopperHeight + 0.05F, pos.getZ() + 1
        );

        if (entityXPOrb.getBoundingBox().intersects(collectionZone)) {

            MechHopperBlockEntity hopperBlockEntity = (MechHopperBlockEntity) world.getBlockEntity(pos);
            MechHopperBlockEntityAdded added = (MechHopperBlockEntityAdded) hopperBlockEntity;

            if (hopperBlockEntity != null && hopperBlockEntity.getFilterItem().equals(Items.SOUL_SAND)) {
                if (added.attemptToSwallowXPOrb(world, pos, entityXPOrb)) {
                    world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS);
                }
            }
        }
    }

}
