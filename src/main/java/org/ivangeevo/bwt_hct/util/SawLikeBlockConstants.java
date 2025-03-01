package org.ivangeevo.bwt_hct.util;

import com.bwt.utils.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

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

    Box UPWARD_BASE_BOX = new Box(0f, 0f, 0f, 16f, baseHeight, 16F);
    Box UPWARD_BLADE_BOX = new Box(8f - bladeHalfLength, baseHeight, 8f - bladeHalfWidth, 8f + bladeHalfLength, baseHeight + bladeHeight, 8f + bladeHalfWidth);
    Box DOWNWARD_BLADE_BOX = new Box(8f - bladeHalfLength, 0, 8f - bladeHalfWidth, 8f + bladeHalfLength, bladeHeight, 8f + bladeHalfWidth);
    Box NORTH_BLADE_BOX = new Box(
            8f - bladeHalfLength, 8f - bladeHalfWidth, 16f - baseHeight,
            8f + bladeHalfLength, 8f + bladeHalfWidth, 16f - baseHeight - bladeHeight
    );
    Box SOUTH_BLADE_BOX = new Box(
            8f - bladeHalfLength, 8f - bladeHalfWidth, baseHeight,
            8f + bladeHalfLength, 8f + bladeHalfWidth, baseHeight + bladeHeight
    );
    Box EAST_BLADE_BOX = new Box(
            16f - baseHeight, 8f - bladeHalfWidth, 8f - bladeHalfLength,
            16f - baseHeight - bladeHeight, 8f + bladeHalfWidth, 8f + bladeHalfLength
    );
    Box WEST_BLADE_BOX = new Box(
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
    ).map(box -> Block.createCuboidShape(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ)).toList();

    List<VoxelShape> OUTLINE_SHAPES = Arrays.stream(Direction.values())
            .map(direction -> BlockUtils.rotateCuboidFromUp(direction, UPWARD_BASE_BOX)).toList();

}
