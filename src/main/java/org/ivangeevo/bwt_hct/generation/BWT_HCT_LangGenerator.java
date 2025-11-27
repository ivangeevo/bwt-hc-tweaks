package org.ivangeevo.bwt_hct.generation;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;
import org.ivangeevo.bwt_hct.BWT_HCTMod;
import org.ivangeevo.bwt_hct.blocks.ModBlocks;

import java.util.concurrent.CompletableFuture;

public class BWT_HCT_LangGenerator extends FabricLanguageProvider {

    public BWT_HCT_LangGenerator(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder tb) {
        this.addBlockTranslations(tb);

        this.addConfigMenuTitle("BWT: HC Tweaks Configuration Menu", tb);
        this.addConfigCategory("general", "General", tb);

        this.addConfig("oldSchoolBuddyBlockNeighborUpdate", "Old School Buddy Block Neighbor Update", tb);
        this.addConfig("blockDispenserRequiringStrongPower", "Block Dispenser Requires Strong Power", tb);

        this.addConfigTooltip("oldSchoolBuddyBlockNeighborUpdate", "Reverts Buddy Block neighbor update behavior so that it works in the same way as it does in the original Better Than Wolves mod", tb);
        this.addConfigTooltip("blockDispenserRequiringStrongPower", "Changes Block Dispensers to require strong redstone power in order to activate", tb);
    }

    private void addBlockTranslations(TranslationBuilder tb) {
        tb.add(ModBlocks.modernMillStoneBlock, "Modern Millstone");
        tb.add(ModBlocks.dormantSoulForge, "Dormant Soul Forge");
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
        tb.add("config." + BWT_HCTMod.MOD_ID + ".category." + categoryPath, translation);
    }

    private void addConfig(String configPath, String translation, TranslationBuilder tb) {
        tb.add("config." + BWT_HCTMod.MOD_ID + "." + configPath, translation);
    }

    private void addConfigTooltip(String configPath, String translation, TranslationBuilder tb) {
        tb.add("config." + BWT_HCTMod.MOD_ID + ".tooltip." + configPath, translation);
    }

}