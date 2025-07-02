package org.ivangeevo.bwt_hct.mixin.client;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ExperienceOrbEntityRenderer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.util.math.MathHelper;
import org.ivangeevo.bwt_hct.entities.interfaces.ExperienceOrbEntityAdded;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceOrbEntityRenderer.class)
public abstract class ExperienceOrbEntityRendererMixin extends EntityRenderer<ExperienceOrbEntity>
{

    protected ExperienceOrbEntityRendererMixin(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Shadow @Final private static RenderLayer LAYER;

    @Inject(method = "render(Lnet/minecraft/entity/ExperienceOrbEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("HEAD"),
            cancellable = true)
    private void onRender1(ExperienceOrbEntity orb, float f, float g, MatrixStack matrices,
                          VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (((ExperienceOrbEntityAdded)(Object)orb).isDragon()) {
            ci.cancel(); // Prevent original render

            matrices.push();

            int orbSize = orb.getOrbSize();
            float u0 = (float)(orbSize % 4 * 16) / 64.0F;
            float u1 = (float)(orbSize % 4 * 16 + 16) / 64.0F;
            float v0 = (float)(orbSize / 4 * 16) / 64.0F;
            float v1 = (float)(orbSize / 4 * 16 + 16) / 64.0F;

            matrices.translate(0.0F, 0.1F, 0.0F);
            matrices.multiply(this.dispatcher.getRotation());
            matrices.scale(0.3F, 0.3F, 0.3F);

            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(LAYER);
            MatrixStack.Entry entry = matrices.peek();

            int red = 255, green = 0, blue = 0;

            vertex(vertexConsumer, entry, -0.5F, -0.25F, red, green, blue, u0, v1, light);
            vertex(vertexConsumer, entry,  0.5F, -0.25F, red, green, blue, u1, v1, light);
            vertex(vertexConsumer, entry,  0.5F,  0.75F, red, green, blue, u1, v0, light);
            vertex(vertexConsumer, entry, -0.5F,  0.75F, red, green, blue, u0, v0, light);

            matrices.pop();
        }
    }


    //@Inject(method = "render(Lnet/minecraft/entity/ExperienceOrbEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
           // at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderer;render(Lnet/minecraft/entity/Entity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V")
    //)
    private void onRender(ExperienceOrbEntity experienceOrbEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        matrixStack.push();

        int orbSize = experienceOrbEntity.getOrbSize();
        float u0 = (float)(orbSize % 4 * 16) / 64.0F;
        float u1 = (float)(orbSize % 4 * 16 + 16) / 64.0F;
        float v0 = (float)(orbSize / 4 * 16) / 64.0F;
        float v1 = (float)(orbSize / 4 * 16 + 16) / 64.0F;

        matrixStack.translate(0.0F, 0.1F, 0.0F);
        matrixStack.multiply(this.dispatcher.getRotation());
        matrixStack.scale(0.3F, 0.3F, 0.3F);

        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(LAYER);
        MatrixStack.Entry entry = matrixStack.peek();

        if (((ExperienceOrbEntityAdded)(Object)experienceOrbEntity).isDragon()) {
            // Red orb: no color animation
            int red = 255;
            int green = 0;
            int blue = 0;
            vertex(vertexConsumer, entry, -0.5F, -0.25F, red, green, blue, u0, v1, i);
            vertex(vertexConsumer, entry,  0.5F, -0.25F, red, green, blue, u1, v1, i);
            vertex(vertexConsumer, entry,  0.5F,  0.75F, red, green, blue, u1, v0, i);
            vertex(vertexConsumer, entry, -0.5F,  0.75F, red, green, blue, u0, v0, i);
        } else {
            // Normal XP orb logic
            float anim = ((float)experienceOrbEntity.age + g) / 2.0F;
            int red   = (int)((MathHelper.sin(anim) + 1.0F) * 0.5F * 255.0F);
            int green = 255;
            int blue  = (int)((MathHelper.sin(anim + 4.18879F) + 1.0F) * 0.1F * 255.0F);
            vertex(vertexConsumer, entry, -0.5F, -0.25F, red, green, blue, u0, v1, i);
            vertex(vertexConsumer, entry,  0.5F, -0.25F, red, green, blue, u1, v1, i);
            vertex(vertexConsumer, entry,  0.5F,  0.75F, red, green, blue, u1, v0, i);
            vertex(vertexConsumer, entry, -0.5F,  0.75F, red, green, blue, u0, v0, i);
        }

        matrixStack.pop();
    }

    @Unique
    private static void vertex(VertexConsumer vertexConsumer, MatrixStack.Entry matrix, float x, float y, int red,
                               int green, int blue, float u, float v, int light)
    {
        vertexConsumer.vertex(matrix, x, y, 0.0F)
                .color(red, green, blue, 128)
                .texture(u, v)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(light)
                .normal(matrix, 0.0F, 1.0F, 0.0F);
    }


}
