package org.btwr.bwt_hct.util;

import com.bwt.items.BwtItems;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;

public final class PaddlePoseHelper {
    private PaddlePoseHelper() {}

    // For boats
    public static void stowInside(ModelPart left, ModelPart right) {
        left.setPos(0.0F, -5.0F, 9.0F);
        left.xRot = -2.5132F;
        left.yRot = 0.9164F;
        left.zRot = -2.0029F;

        right.setPos(0.0F, -5.0F, -9.0F);
        right.xRot = 0.6725F;
        right.yRot = 0.89F;
        right.zRot = 1.1948F;
    }

    // For rafts
    public static void restOnSide(ModelPart left, ModelPart right) {
        left.setPos(3.0F, -3.0F, 7.0F);
        left.xRot = 1.5708F;
        left.yRot = 0.0F;
        left.zRot = -1.5708F;

        right.setPos(3.0F, -3.0F, -7.0F);
        right.xRot = -1.5708F;
        right.yRot = 0.0F;
        right.zRot = 1.5708F;
    }

    /** Tuck the paddles into the boat if there is no controlling passenger or if they are holding a sail **/
    public static boolean shouldTuckPaddles(Boat boat) {
        LivingEntity controller = boat.getControllingPassenger();
        if (controller == null) return true;

        return controller instanceof Player player && player.isHolding(BwtItems.sailItem);
    }
}