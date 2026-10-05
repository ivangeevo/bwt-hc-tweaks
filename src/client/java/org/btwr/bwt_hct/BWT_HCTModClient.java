package org.btwr.bwt_hct;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import org.btwr.bwt_hct.blocks.ModBlocks;

public class BWT_HCTModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        this.registerCutout(ModBlocks.modernMillStoneBlock);
        this.registerCutout(ModBlocks.dormantSoulForge);
    }

    private void registerCutout(Block block)  {
        BlockRenderLayerMap.INSTANCE.putBlock(block, RenderType.cutout());
    }

}