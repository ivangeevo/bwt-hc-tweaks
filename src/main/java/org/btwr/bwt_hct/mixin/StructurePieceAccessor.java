package org.btwr.bwt_hct.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.math.BlockBox;
import net.minecraft.world.StructureWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(StructurePiece.class)
public interface StructurePieceAccessor {
    @Invoker("addBlock")
    void invokeAddBlock(StructureWorldAccess world, BlockState state, int x, int y, int z, BlockBox chunkBox);
}