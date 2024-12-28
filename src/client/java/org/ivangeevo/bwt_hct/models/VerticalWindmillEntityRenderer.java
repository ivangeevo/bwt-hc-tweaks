package org.ivangeevo.bwt_hct.models;

import com.bwt.utils.Id;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import org.ivangeevo.bwt_hct.client.BWT_HCTModClient;
import org.ivangeevo.bwt_hct.entities.VerticalWindmillEntity;

public class VerticalWindmillEntityRenderer extends VerticalMechPowerSourceEntityRenderer<VerticalWindmillEntity> {
    public VerticalWindmillEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new VerticalWindmillEntityModel(context.getPart(BWT_HCTModClient.MODEL_VERTICAL_WINDMILL_LAYER));
    }

    @Override
    public Identifier getTexture(VerticalWindmillEntity entity) {
        return Id.of("textures/entity/vertical_windmill.png");
    }
}
