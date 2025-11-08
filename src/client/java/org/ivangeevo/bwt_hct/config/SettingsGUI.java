package org.ivangeevo.bwt_hct.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.ivangeevo.bwt_hct.BWT_HCTMod;

public class SettingsGUI
{
    static BWT_HCTSettings settingsCommon = BWT_HCTMod.getInstance().settings;

    public static Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent).setTitle(Text.translatable("title.bwt_hct.config"));
        builder.setSavingRunnable(() -> { BWT_HCTMod.getInstance().saveSettings(); });

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("config.bwt_hct.category.general"));

        /** General Category **/
        general.addEntry(entryBuilder
                .startBooleanToggle(Text.translatable("config.bwt_hct.oldSchoolBuddyBlockNeighborUpdate"), settingsCommon.oldSchoolBuddyBlockNeighborUpdate)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> settingsCommon.oldSchoolBuddyBlockNeighborUpdate = newValue)
                .setTooltip(Text.translatable("config.bwt_hct.tooltip.oldSchoolBuddyBlockNeighborUpdate"))
                .build());

        general.addEntry(entryBuilder
                .startBooleanToggle(Text.translatable("config.bwt_hct.blockDispenserRequiringStrongPower"), settingsCommon.blockDispenserRequiringStrongPower)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> settingsCommon.blockDispenserRequiringStrongPower = newValue)
                .setTooltip(Text.translatable("config.bwt_hct.tooltip.blockDispenserRequiringStrongPower"))
                .build());

        return builder.build();
    }
}
