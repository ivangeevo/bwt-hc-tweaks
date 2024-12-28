package org.ivangeevo.bwt_hct.client;

import com.bwt.utils.Id;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import org.ivangeevo.bwt_hct.blocks.ModBlocks;
import org.ivangeevo.bwt_hct.entities.ModEntities;
import org.ivangeevo.bwt_hct.models.VerticalWindmillEntityModel;
import org.ivangeevo.bwt_hct.models.VerticalWindmillEntityRenderer;

public class BWT_HCTModClient implements ClientModInitializer
{
    public static final EntityModelLayer MODEL_VERTICAL_WINDMILL_LAYER = new EntityModelLayer(Id.of("vertical_windmill"), "main");

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.modernMillStoneBlock, RenderLayer.getCutout());

        EntityRendererRegistry.register(ModEntities.verticalWindmillEntity, VerticalWindmillEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(MODEL_VERTICAL_WINDMILL_LAYER, VerticalWindmillEntityModel::getTexturedModelData);


    }
}
