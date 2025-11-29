package org.btwr.bwt_hct;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import org.btwr.bwt_hct.datagen.*;

public class BWT_HCTDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(BWT_HCT_RecipeProvider::new);
        pack.addProvider(BWT_HCT_LootTableProvider::new);
        pack.addProvider(BWT_HCT_BlockTagProvider::new);
        pack.addProvider(BWT_HCT_ItemTagProvider::new);
        pack.addProvider(BWT_HCT_LangProvider::new);
    }

}