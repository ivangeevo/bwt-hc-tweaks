package org.ivangeevo.bwt_hct;

import com.bwt.utils.FireData;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.CampfireBlock;
import org.ivangeevo.bwt_hct.block.ModBlocks;
import org.ivangeevo.bwt_hct.entity.ModBlockEntities;
import org.ivangeevo.bwt_hct.recipes.ModRecipes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BWT_HCTMod implements ModInitializer
{
    public static final String MOD_ID = "bwt_hct";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


    @Override
    public void onInitialize() {
        ModBlocks.registerModBlocks();
        ModBlockEntities.registerBlockEntities();
        ModRecipes.registerRecipes();

        FireData.FIRE_AMOUNT_FUNCTIONS.put(CampfireBlock.class,
                ((world, blockPos, blockState) -> new FireData(CampfireBlock.isLitCampfire(blockState) ? 1 : 0)));


    }
}
