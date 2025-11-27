package org.ivangeevo.bwt_hct;

import com.bwt.utils.FireData;
import com.google.gson.Gson;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.CampfireBlock;
import org.ivangeevo.bwt_hct.blocks.ModBlocks;
import org.ivangeevo.bwt_hct.config.BWT_HCTSettings;
import org.ivangeevo.bwt_hct.entity.ModBlockEntities;
import org.ivangeevo.bwt_hct.event.PistonBreakEventsHandler;
import org.ivangeevo.bwt_hct.items.ModItems;
import org.ivangeevo.bwt_hct.recipes.ModRecipes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class BWT_HCTMod implements ModInitializer {

    public static final String MOD_ID = "bwt_hct";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public BWT_HCTSettings settings;
    private static BWT_HCTMod instance;
    public static BWT_HCTMod getInstance() {
        return instance;
    }

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Better With Time: HC Tweaks.");
        instance = this;

        ModBlocks.registerModBlocks();
        ModItems.registerModItems();
        ModBlockEntities.registerBlockEntities();
        ModRecipes.registerRecipes();
        PistonBreakEventsHandler.init();

        // Make campfire a valid fuel for BWT FireData
        FireData.FIRE_AMOUNT_FUNCTIONS.put(
                CampfireBlock.class,
                ((world, blockPos, blockState) -> new FireData(CampfireBlock.isLitCampfire(blockState) ? 1 : 0))
        );

    }

}