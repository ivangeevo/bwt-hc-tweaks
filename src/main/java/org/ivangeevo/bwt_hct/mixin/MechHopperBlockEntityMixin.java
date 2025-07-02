package org.ivangeevo.bwt_hct.mixin;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.mech_hopper.MechHopperBlock;
import com.bwt.blocks.mech_hopper.MechHopperBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.ivangeevo.bwt_hct.blocks.ModBlocks;
import org.ivangeevo.bwt_hct.blocks.blocks.ArcaneVesselBlock;
import org.ivangeevo.bwt_hct.entities.block.ArcaneVesselBE;
import org.ivangeevo.bwt_hct.entities.interfaces.ExperienceOrbEntityAdded;
import org.ivangeevo.bwt_hct.entities.interfaces.MechHopperBlockEntityAdded;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.bwt.blocks.MechPowerBlockBase.MECH_POWERED;

@Pseudo
@Mixin(MechHopperBlockEntity.class)
public abstract class MechHopperBlockEntityMixin extends BlockEntity implements MechHopperBlockEntityAdded
{
    @Shadow protected int xpCount;

    @Shadow protected int xpDropCooldown;
    @Unique private static final int XP_INVENTORY_SPACE = 100;
    @Unique private static final int XP_EJECT_UNIT_SIZE = 20;
    @Unique private static final int XP_DELAY_BETWEEN_DROPS = 10;

    public MechHopperBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private static void attemptEjectXPIntoArcaneVessel(World world, BlockPos pos, BlockState state, MechHopperBlockEntity blockEntity, CallbackInfo ci)
    {
        boolean bHopperOn = state.getBlock() instanceof MechHopperBlock && state.get(MECH_POWERED);
        if (bHopperOn) {
            ((MechHopperBlockEntityAdded)blockEntity).attemptToEjectXPFromInv();
        }
    }

    @Override
    public void attemptToEjectXPFromInv() {
        boolean bShouldResetEjectCount = true;

        BlockPos pos = this.getPos();
        assert world != null;

        if (xpCount >= XP_EJECT_UNIT_SIZE) {
            int targetPosX = pos.getX();
            int targetPosY = pos.getY() - 1;
            int targetPosZ = pos.getZ();

            boolean bCanEjectIntoWorld = false;

            BlockPos ejectPos = new BlockPos(targetPosX, targetPosY, targetPosZ);
            BlockState ejectPosState = world.getBlockState(ejectPos);

            if (ejectPosState.isAir()) {
                bCanEjectIntoWorld = true;
            }
            else {
                Block targetBlock = ejectPosState.getBlock();

                if (targetBlock == BwtBlocks.hopperBlock) {
                    bShouldResetEjectCount = attemptToEjectXPIntoHopper(ejectPos);
                }
                else if (targetBlock == ModBlocks.arcaneVesselBlock) {
                    bShouldResetEjectCount = attemptToEjectXPIntoArcaneVessel(ejectPos);
                }
                else if (world.getBlockState(ejectPos).isReplaceable()) {
                    bCanEjectIntoWorld = true;
                }
                else {

                    if (!ejectPosState.isSolidBlock(world, ejectPos)) {
                        bCanEjectIntoWorld = true;
                    }
                }
            }

            if (bCanEjectIntoWorld) {
                if (xpDropCooldown <= 0) {
                    ejectXPOrb(XP_EJECT_UNIT_SIZE);

                    xpCount -= XP_EJECT_UNIT_SIZE;
                }
                else {
                    bShouldResetEjectCount = false;
                }
            }
        }

        if (bShouldResetEjectCount) {
            resetXPEjectCount();
        }
        else {
            xpDropCooldown--;
        }

    }

    /*
     * returns true if the *entire* XP orb is swallowed, false otherwise
     */
    @Override
    public boolean attemptToSwallowXPOrb(World world, BlockPos pos, ExperienceOrbEntity entityXPOrb) {
        int iRemainingSpace = XP_INVENTORY_SPACE - xpCount;

        if (iRemainingSpace > 0) {
            if (entityXPOrb.getExperienceAmount() <= iRemainingSpace) {
                xpCount += entityXPOrb.getExperienceAmount();

                entityXPOrb.discard();

                return true;
            } else {
                int newXP = entityXPOrb.getExperienceAmount() - iRemainingSpace;
                ((ExperienceOrbEntityAdded)entityXPOrb).setXPAmount(newXP);

                xpCount = XP_INVENTORY_SPACE;
            }
        }

        return false;
    }



    @Unique
    private boolean attemptToEjectXPIntoHopper(BlockPos pos) {
        // returns whether the hopper eject counter should be reset
        assert world != null;
        MechHopperBlockEntity targetTileEntity = (MechHopperBlockEntity) world.getBlockEntity(pos);

        if (targetTileEntity != null) {
            if (targetTileEntity.getFilterItem().equals(Items.SOUL_SAND)) // soul sand filter required
            {
                int iTargetSpaceRemaining = XP_INVENTORY_SPACE - xpCount;

                if (iTargetSpaceRemaining > 0) {
                    if (xpDropCooldown <= 0) {
                        int iXPEjected = XP_EJECT_UNIT_SIZE;

                        if (iTargetSpaceRemaining < iXPEjected) {
                            iXPEjected = iTargetSpaceRemaining;
                        }

                        xpCount += iXPEjected;

                        xpCount -= iXPEjected;

                        world.playSound(null, pos, SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS,
                                0.5F + world.getRandom().nextFloat() * 0.25F,
                                0.5F + world.getRandom().nextFloat() * 0.25F
                        );
                    }
                    else {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private boolean attemptToEjectXPIntoArcaneVessel(BlockPos pos) {
        // returns whether the hopper eject counter should be reset

        assert world != null;
        ArcaneVesselBlock vesselBlock = (ArcaneVesselBlock) ModBlocks.arcaneVesselBlock;
        BlockState state = world.getBlockState(pos);
        ArcaneVesselBE vesselBE = (ArcaneVesselBE) world.getBlockEntity(pos);

        if (vesselBE != null) {
            if (!state.get(MECH_POWERED)) {
                int iTargetSpaceRemaining = vesselBE.MAX_CONTAINED_EXPERIENCE - vesselBE.getContainedTotalExperience();

                if (iTargetSpaceRemaining > 0) {
                    if (xpDropCooldown <= 0) {
                        int iXPEjected = XP_EJECT_UNIT_SIZE;

                        if (iTargetSpaceRemaining < iXPEjected) {
                            iXPEjected = iTargetSpaceRemaining;
                        }

                        vesselBE.setContainedRegularExperience(vesselBE.getContainedRegularExperience() + iXPEjected);

                        xpCount -= iXPEjected;

                        world.playSound(null, pos, SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS,
                                0.5F + world.getRandom().nextFloat() * 0.25F,
                                0.5F + world.getRandom().nextFloat() * 0.25F
                        );
                    }
                    else {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    @Unique
    private void ejectXPOrb(int iXPValue) {
        assert world != null;
        double xOffset = world.getRandom().nextDouble() * 0.1D + 0.45D;
        double yOffset = -0.20D;
        double zOffset = world.getRandom().nextDouble() * 0.1D + 0.45D;

        ExperienceOrbEntity xpOrb = new ExperienceOrbEntity(world,
                this.getPos().getX() + xOffset,
                this.getPos().getY() + yOffset,
                this.getPos().getZ() + zOffset,
                iXPValue
        );

        xpOrb.prevX = 0D;
        xpOrb.prevY = 0D;
        xpOrb.prevZ = 0D;

        world.spawnEntity(xpOrb);

        world.playSound(null, pos, SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS,
                0.5F + world.getRandom().nextFloat() * 0.25F,
                0.5F + world.getRandom().nextFloat() * 0.25F
        );
    }

    @Unique
    private void resetXPEjectCount() {
        assert world != null;
        xpDropCooldown = XP_DELAY_BETWEEN_DROPS + world.getRandom().nextInt(3);
    }

}

