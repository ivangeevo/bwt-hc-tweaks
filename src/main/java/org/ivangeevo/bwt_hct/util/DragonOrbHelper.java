package org.ivangeevo.bwt_hct.util;

import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.ivangeevo.bwt_hct.entities.interfaces.ExperienceOrbEntityAdded;

public class DragonOrbHelper {

    public static void spawn(ServerWorld world, Vec3d pos, int amount) {
        for (int i = 0; i < amount; i++) {
            ExperienceOrbEntity orb = new ExperienceOrbEntity(world, pos.x, pos.y, pos.z, 1);
            ((ExperienceOrbEntityAdded) orb).setDragon(true);
            world.spawnEntity(orb);
        }
    }

}
