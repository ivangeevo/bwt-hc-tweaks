package org.btwr.bwt_hct.config;

import org.btwr.shared_library.api.config.ConfigBuilder;
import org.btwr.shared_library.api.config.ConfigGroup;
import org.btwr.shared_library.api.config.ConfigSetting;
import org.btwr.shared_library.api.config.TomlConfigManager;
import org.btwr.bwt_hct.BWT_HCTMod;

public class BWT_HCTConfig {

    /** Replace with your MOD_ID for easy adaptation **/
    private static final String MOD_ID = BWT_HCTMod.MOD_ID;

    public static final ConfigGroup CONFIG;

    /** Call this method in your mod initializer so the class can initialize **/
    public static void register() {}

    public static final ConfigSetting<Boolean> oldSchoolBuddyBlockNeighborUpdate =
            ConfigBuilder.booleanSetting("oldSchoolBuddyBlockNeighborUpdate")
                    .defaultValue(true)
                    .comment("Reverts Buddy Block neighbor update behavior so that it works\n in the same way as it does in the original Better Than Wolves mod")
                    .build();

    public static final ConfigSetting<Boolean> blockDispenserRequiringStrongPower =
            ConfigBuilder.booleanSetting("blockDispenserRequiringStrongPower")
                    .defaultValue(true)
                    .comment("Changes Block Dispensers to require strong redstone power to activate")
                    .build();

    public static final ConfigSetting<Integer> sawBlockBreakSpeed =
            ConfigBuilder.intSetting("sawBlockBreakSpeed")
                    .defaultValue(20)
                    .comment("Set a custom sawing speed for the Saw Block (in ticks). Default 20. BWT default - 15")
                    .build();

    static {
        CONFIG = new ConfigGroup(String.format("%s/%s_common.toml", MOD_ID, MOD_ID));
        CONFIG.add(oldSchoolBuddyBlockNeighborUpdate);
        CONFIG.add(blockDispenserRequiringStrongPower);
        CONFIG.add(sawBlockBreakSpeed);
        TomlConfigManager.registerGroup(CONFIG); // auto init/load/save
    }

}