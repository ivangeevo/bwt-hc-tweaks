package org.btwr.bwt_hct;

import com.bwt.utils.FireData;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.CampfireBlock;
import org.btwr.bwt_hct.blocks.ModBlocks;
import org.btwr.bwt_hct.config.BWT_HCTConfig;
import org.btwr.bwt_hct.entity.ModBlockEntities;
import org.btwr.bwt_hct.event.PistonBreakEventsHandler;
import org.btwr.bwt_hct.items.ModItems;
import org.btwr.bwt_hct.recipes.ModRecipes;
import org.btwr.bwt_hct.util.BlastingOilHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BWT_HCTMod implements ModInitializer {

    public static final String MOD_ID = "bwt_hct";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Better With Time: HC Tweaks");

        BWT_HCTConfig.register();
        ModBlocks.registerModBlocks();
        ModItems.registerModItems();
        ModBlockEntities.registerBlockEntities();
        ModRecipes.registerRecipes();
        PistonBreakEventsHandler.init();

        BlastingOilHelper.registerTickEvents();

        // Make campfire a valid fuel for BWT FireData
        FireData.FIRE_AMOUNT_FUNCTIONS.put(
                CampfireBlock.class,
                ((world, blockPos, blockState) -> new FireData(CampfireBlock.isLitCampfire(blockState) ? 1 : 0))
        );
    }

}