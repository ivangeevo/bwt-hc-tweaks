package org.ivangeevo.bwt_hct.mixin;

import com.bwt.blocks.PlanterBlock;
import com.bwt.blocks.SoilPlanterBlock;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoilPlanterBlock.class)
public abstract class SoilPlanterBlockMixin extends PlanterBlock {

    @Unique
    private static final IntProperty MOISTURE = Properties.MOISTURE;

    public SoilPlanterBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(Settings settings, CallbackInfo ci) {
        this.setDefaultState(this.stateManager.getDefaultState().with(MOISTURE, 0));
    }
}
