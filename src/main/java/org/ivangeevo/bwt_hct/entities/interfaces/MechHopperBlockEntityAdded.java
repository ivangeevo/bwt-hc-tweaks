package org.ivangeevo.bwt_hct.entities.interfaces;

import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface MechHopperBlockEntityAdded
{
    void attemptToEjectXPFromInv();
    boolean attemptToSwallowXPOrb(World world, BlockPos pos, ExperienceOrbEntity entityXPOrb);
}


