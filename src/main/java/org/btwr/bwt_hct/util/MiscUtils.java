package org.btwr.bwt_hct.util;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class MiscUtils {

    /**
     * Returns a normalized vector in the direction of the block facing.
     */
    public static Vec3d convertBlockFacingToVector(Direction direction) {
        return new Vec3d(direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ()).normalize();
    }

}