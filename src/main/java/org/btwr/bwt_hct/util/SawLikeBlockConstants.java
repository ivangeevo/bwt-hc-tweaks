package org.btwr.bwt_hct.util;

import com.bwt.utils.BlockUtils;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface SawLikeBlockConstants {

    int powerChangeTickRate = 10;

    int sawTimeBaseTickRate = 15;
    int sawTimeTickRateVariance = 4;

    // This base height prevents chickens slipping through grinders, while allowing items to pass

    float baseHeight = 16f - 4f;

    float bladeLength = 10f;
    float bladeHalfLength = bladeLength * 0.5F;

    float bladeWidth = 0.25f;
    float bladeHalfWidth = bladeWidth * 0.5F;
    float bladeHeight = 16F - baseHeight;

    AABB UPWARD_BASE_BOX = new AABB(0f, 0f, 0f, 16f, baseHeight, 16F);
    AABB UPWARD_BLADE_BOX = new AABB(8f - bladeHalfLength, baseHeight, 8f - bladeHalfWidth, 8f + bladeHalfLength, baseHeight + bladeHeight, 8f + bladeHalfWidth);
    AABB DOWNWARD_BLADE_BOX = new AABB(8f - bladeHalfLength, 0, 8f - bladeHalfWidth, 8f + bladeHalfLength, bladeHeight, 8f + bladeHalfWidth);
    AABB NORTH_BLADE_BOX = new AABB(
            8f - bladeHalfLength, 8f - bladeHalfWidth, 16f - baseHeight,
            8f + bladeHalfLength, 8f + bladeHalfWidth, 16f - baseHeight - bladeHeight
    );
    AABB SOUTH_BLADE_BOX = new AABB(
            8f - bladeHalfLength, 8f - bladeHalfWidth, baseHeight,
            8f + bladeHalfLength, 8f + bladeHalfWidth, baseHeight + bladeHeight
    );
    AABB EAST_BLADE_BOX = new AABB(
            16f - baseHeight, 8f - bladeHalfWidth, 8f - bladeHalfLength,
            16f - baseHeight - bladeHeight, 8f + bladeHalfWidth, 8f + bladeHalfLength
    );
    AABB WEST_BLADE_BOX = new AABB(
            (baseHeight), 8f - bladeHalfWidth, 8f - bladeHalfLength,
            baseHeight + bladeHeight, 8f + bladeHalfWidth, 8f + bladeHalfLength
    );

    List<VoxelShape> COLLISION_SHAPES = Arrays.stream(Direction.values())
            .map(direction -> BlockUtils.rotateCuboidFromUp(direction, UPWARD_BASE_BOX))
            .toList();
     
    List<VoxelShape> BLADE_SHAPES = Stream.of(
            DOWNWARD_BLADE_BOX,
            UPWARD_BLADE_BOX,
            NORTH_BLADE_BOX,
            SOUTH_BLADE_BOX,
            EAST_BLADE_BOX,
            WEST_BLADE_BOX
    ).map(box -> Block.box(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ)).toList();

    List<VoxelShape> OUTLINE_SHAPES = Arrays.stream(Direction.values())
            .map(direction -> BlockUtils.rotateCuboidFromUp(direction, UPWARD_BASE_BOX)).toList();

}