package org.ivangeevo.bwt_hct.models;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import org.ivangeevo.bwt_hct.entities.VerticalMechPowerSourceEntity;

public abstract class VerticalMechPowerSourceEntityModel<T extends VerticalMechPowerSourceEntity> extends EntityModel<T> {
    public VerticalMechPowerSourceEntityModel() {}

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {}

    public abstract void render(T entity, MatrixStack matrixStack, VertexConsumer vertexConsumer, int light, int uv, int color);

    @Override
    public final void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {}
}
