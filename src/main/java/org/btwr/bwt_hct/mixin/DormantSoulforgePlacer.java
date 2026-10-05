package org.btwr.bwt_hct.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.structures.NetherFortressPieces;
import org.btwr.bwt_hct.blocks.ModBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetherFortressPieces.CastleEntrance.class)
public abstract class DormantSoulforgePlacer extends StructurePiece {

    protected DormantSoulforgePlacer(StructurePieceType type, int length, BoundingBox boundingBox) {
        super(type, length, boundingBox);
    }

    @Inject(method = "postProcess", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/levelgen/structure/structures/NetherFortressPieces$CastleEntrance;placeBlock(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/block/state/BlockState;IIILnet/minecraft/world/level/levelgen/structure/BoundingBox;)V"))
    private void placeDormantSoulforge(WorldGenLevel world, StructureManager structureAccessor, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox chunkBox, ChunkPos chunkPos, BlockPos pivot, CallbackInfo ci) {
        this.placeBlock(world, ModBlocks.dormantSoulForge.defaultBlockState(), 7, 6, 6, chunkBox);
    }

}