package org.btwr.bwt_hct.mixin;

import com.bwt.blocks.BwtBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.structures.JungleTemplePiece;
import org.btwr.bwt_hct.blocks.ModBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.btwr.bwt_hct.blocks.blocks.ChoppingBlock.DIRTY;

@Mixin(JungleTemplePiece.class)
public abstract class JungleTempleGeneratorMixin  {

    @Inject(method = "postProcess", at = @At("TAIL"))
    private void addBlocks(WorldGenLevel world, StructureManager structureAccessor, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox chunkBox, ChunkPos chunkPos, BlockPos pivot, CallbackInfo ci) {
        JungleTemplePiece self = (JungleTemplePiece)(Object)this;
        StructurePieceAccessor access = (StructurePieceAccessor)self;

        BlockState dirtyChoppingBlock = ModBlocks.choppingBlock.defaultBlockState().setValue(DIRTY, true);

        // Add chopping block
        access.invokeAddBlock(world, dirtyChoppingBlock, 5, 4, 11, chunkBox);
        access.invokeAddBlock(world, dirtyChoppingBlock, 6, 4, 11, chunkBox);

        // Add hand crank block
        access.invokeAddBlock(world, BwtBlocks.handCrankBlock.defaultBlockState(), 5, 3, 10, chunkBox);

        // Add dragon vessel block
        //access.invokeAddBlock(world, Blocks.REDSTONE_BLOCK.getDefaultState(), 6, 3, 10, chunkBox);
    }
}
