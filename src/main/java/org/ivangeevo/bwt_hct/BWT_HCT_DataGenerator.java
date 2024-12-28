package org.ivangeevo.bwt_hct;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import org.ivangeevo.bwt_hct.generation.*;

public class BWT_HCT_DataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(BWT_HCT_RecipeProvider::new);
        pack.addProvider(BWT_HCT_LootTableProvider::new);
        pack.addProvider(BWT_HCT_BlockTagProvider::new);
        pack.addProvider(BWT_HCT_ItemTagProvider::new);

    }

}
