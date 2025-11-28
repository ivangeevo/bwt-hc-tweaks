package org.btwr.bwt_hct.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class SettingsGUI {

    public static Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent).setTitle(Text.translatable("title.bwt_hct.config"));
        //builder.setSavingRunnable(() -> { BWT_HCTMod.getInstance().saveSettings(); });

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("config.bwt_hct.category.general"));

        // Client Settings
        general.addEntry(entryBuilder
                .startTextDescription(Text.translatable("config.bwt_hct.text.clientSettingsText"))
                .build()
        );
        general.addEntry(entryBuilder
                .startTextDescription(Text.translatable("config.bwt_hct.text.emptyClientConfigText"))
                .build()
        );

        // Server Settings
        general.addEntry(entryBuilder
                .startTextDescription(Text.translatable("config.bwt_hct.text.serverSettingsNoAccessText"))
                //.setDisplayRequirement(displayWhenRemoteOrLAN())
                .build()
        );

        return builder.build();
    }

}