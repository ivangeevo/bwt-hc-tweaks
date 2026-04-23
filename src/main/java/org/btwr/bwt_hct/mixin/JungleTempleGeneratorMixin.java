package org.btwr.bwt_hct.mixin;

import com.bwt.blocks.BwtBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.JungleTempleGenerator;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.btwr.bwt_hct.blocks.ModBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.btwr.bwt_hct.blocks.blocks.ChoppingBlock.DIRTY;

@Mixin(JungleTempleGenerator.class)
public abstract class JungleTempleGeneratorMixin  {

    @Inject(method = "generate", at = @At("TAIL"))
    private void addBlocks(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot, CallbackInfo ci) {
        JungleTempleGenerator self = (JungleTempleGenerator)(Object)this;
        StructurePieceAccessor access = (StructurePieceAccessor)self;

        BlockState dirtyChoppingBlock = ModBlocks.choppingBlock.getDefaultState().with(DIRTY, true);

        // Add chopping block
        access.invokeAddBlock(world, dirtyChoppingBlock, 5, 4, 11, chunkBox);
        access.invokeAddBlock(world, dirtyChoppingBlock, 6, 4, 11, chunkBox);

        // Add hand crank block
        access.invokeAddBlock(world, BwtBlocks.handCrankBlock.getDefaultState(), 5, 3, 10, chunkBox);

        // Add dragon vessel block
        //access.invokeAddBlock(world, Blocks.REDSTONE_BLOCK.getDefaultState(), 6, 3, 10, chunkBox);
    }
}
