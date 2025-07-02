package org.ivangeevo.bwt_hct.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import org.ivangeevo.bwt_hct.blocks.ModBlocks;

public class BWT_HCTModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        this.registerCutout(ModBlocks.modernMillStoneBlock);
        this.registerCutout(ModBlocks.dormantSoulForge);
        this.registerCutout(ModBlocks.arcaneVesselBlock);
    }

    private void registerCutout(Block block)  {
        BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getCutout());
    }
}
