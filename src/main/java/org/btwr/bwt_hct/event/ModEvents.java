package org.btwr.bwt_hct.event;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.btwr.bwt_hct.event.events.PistonBreakEvents;
import org.btwr.bwt_hct.util.BlastingOilHelper;
import org.btwr.bwt_hct.util.ChoppingBlockHelper;
import org.btwr.bwt_hct.util.PistonBreakHempHelper;

public class ModEvents {

    public static void register() {
        registerBlastingOilEvents();

        PistonBreakEvents.register(PistonBreakHempHelper::tryBreakingHemp);

        ServerLivingEntityEvents.ALLOW_DEATH.register(ChoppingBlockHelper::tryDroppingSkull);
    }

    public static void registerBlastingOilEvents() {
        ServerLivingEntityEvents.AFTER_DAMAGE.register(BlastingOilHelper::tickBlastingOil);
        ServerTickEvents.END_SERVER_TICK.register(BlastingOilHelper::clearBlastingOilMap);
    }
}
