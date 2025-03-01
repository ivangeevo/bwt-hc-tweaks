package org.ivangeevo.bwt_hct.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import org.ivangeevo.bwt_hct.blocks.ModBlocks;

public class BWT_HCTModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.modernMillStoneBlock, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.dormantSoulForge, RenderLayer.getCutout());

    }
}
