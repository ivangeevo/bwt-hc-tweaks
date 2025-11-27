package org.ivangeevo.bwt_hct.config;

import com.google.common.reflect.Reflection;
import com.supermartijn642.configlib.api.ConfigBuilders;
import com.supermartijn642.configlib.api.IConfigBuilder;
import org.ivangeevo.bwt_hct.BWT_HCTMod;

import java.util.function.Supplier;

public class BWTHCTConfig {

    public static void register() {
        Reflection.initialize(Settings.class);
    }

    public static class Settings {
        public static final Supplier<Boolean> oldSchoolBuddyBlockNeighborUpdate;
        public static final Supplier<Boolean> blockDispenserRequiringStrongPower;
        public static final Supplier<Integer> sawBlockBreakSpeed;

        static {
            // construct a new config builder
            IConfigBuilder builder = ConfigBuilders.newTomlConfig(BWT_HCTMod.MOD_ID, BWT_HCTMod.MOD_ID + "_common", true);

            // Boolean checks
            oldSchoolBuddyBlockNeighborUpdate = builder
                    .comment("Reverts Buddy Block neighbor update behavior so that it works in the same way as it does in the original Better Than Wolves mod\"")
                    .define("hcPlayerMiningSpeed", true);
            blockDispenserRequiringStrongPower = builder
                    .comment("Changes Block Dispensers to require strong redstone power in order to activate")
                    .define("stratificationToughness", true);
            sawBlockBreakSpeed = builder
                    .comment("Set a custom sawing speed for the Saw Block (in ticks). Default 20. BWT default - 15")
                    .define("sawBlockBreakSpeed", 20, 2, 32767);

            // build the config
            builder.build();
        }
    }

}