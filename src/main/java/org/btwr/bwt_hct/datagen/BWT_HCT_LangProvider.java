package org.btwr.bwt_hct.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;
import org.btwr.bwt_hct.BWT_HCTMod;
import org.btwr.bwt_hct.blocks.ModBlocks;
import org.btwr.bwt_hct.items.ModItems;

import java.util.concurrent.CompletableFuture;

public class BWT_HCT_LangProvider extends FabricLanguageProvider {

    public BWT_HCT_LangProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder tb) {
        this.generateBlockTranslations(tb);
        this.generateItemTranslations(tb);
        this.generateConfigTranslations(tb);
        this.generateDeathMessages(tb);

        //this.addConfig("oldSchoolBuddyBlockNeighborUpdate", "Old School Buddy Block Neighbor Update", tb);
        //this.addConfig("blockDispenserRequiringStrongPower", "Block Dispenser Requires Strong Power", tb);
        //this.addConfig("sawBlockBreakSpeed", "Saw Block Break Speed", tb);
        //this.addConfigTooltip("oldSchoolBuddyBlockNeighborUpdate", "Reverts Buddy Block neighbor update behavior so that it works in the same way as it does in the original Better Than Wolves mod", tb);
        //this.addConfigTooltip("blockDispenserRequiringStrongPower", "Changes Block Dispensers to require strong redstone power in order to activate", tb);
        //this.addConfigTooltip("sawBlockBreakSpeed", "Set a custom sawing speed for the Saw Block (in ticks). Default 20. Vanilla - 15", tb);
    }

    private void generateBlockTranslations(TranslationBuilder tb) {
        tb.add(ModBlocks.modernMillStoneBlock, "Modern Millstone");
        tb.add(ModBlocks.dormantSoulForge, "Dormant Soul Forge");
    }

    private void generateItemTranslations(TranslationBuilder tb) {
        tb.add(ModItems.blastingOil, "Blasting Oil");
        tb.add(ModItems.fuse, "Fuse");
    }

    private void generateConfigTranslations(TranslationBuilder tb) {
        this.addConfigMenuDefaults(tb);
        this.addConfigMenuTitle("BWT: HC Tweaks Configuration Menu", tb);
        this.addConfigCategory("general", "General", tb);
    }

    private void generateDeathMessages(TranslationBuilder tb) {
        tb.add("death.attack.blasting_oil", "%1$s was obliterated by Blasting Oil");
        tb.add("death.attack.blasting_oil.player", "%1$s was obliterated by Blasting Oil whilst fighting %2$s");
    }

    private void addConfigMenuDefaults(TranslationBuilder tb) {
        this.addSimpleText("clientSettingsText", "Client Settings:", tb);
        this.addSimpleText("emptyClientConfigText", "§eNote:§r There are currently no client config settings.", tb);
        this.addSimpleText("serverSettingsText", "Server Settings:", tb);
        this.addSimpleText("serverSettingsNoAccessText", "§eNote:§r Server settings are not accessible in menus." +
                "\nThey can only be changed by editing the config file manually and require a world reload to take effect.", tb
        );
    }

    private void addItemTranslations(TranslationBuilder tb) {

    }

    private void addItemGroup(String entryPath, String translation, TranslationBuilder tb) {
        tb.add("itemgroup." + entryPath, translation);
    }

    private void addConfigMenuTitle(String translation, TranslationBuilder tb) {
        tb.add("title." + BWT_HCTMod.MOD_ID + ".config", translation);
    }

    private void addConfigCategory(String categoryPath, String translation, TranslationBuilder tb) {
        tb.add(configBasePath() + "category." + categoryPath, translation);
    }

    private void addSimpleText(String path, String translation, TranslationBuilder tb) {
        tb.add(configBasePath() + "text." + path, translation);
    }

    private void addConfig(String configPath, String translation, TranslationBuilder tb) {
        tb.add(configBasePath() + configPath, translation);
    }

    private void addConfigTooltip(String configPath, String translation, TranslationBuilder tb) {
        tb.add(configBasePath() + "tooltip." + configPath, translation);
    }

    private String configBasePath() {
        return "config." + BWT_HCTMod.MOD_ID + ".";
    }

}