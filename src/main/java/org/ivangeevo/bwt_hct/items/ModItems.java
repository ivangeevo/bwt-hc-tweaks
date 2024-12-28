package org.ivangeevo.bwt_hct.items;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.ivangeevo.bwt_hct.BWT_HCTMod;
import org.ivangeevo.bwt_hct.entities.VerticalWindmillEntity;

public class ModItems
{
    public static final Item verticalWindmillItem = register(
            "vertical_windmill",
            new VerticalMechPowerSourceItem(
                    VerticalWindmillEntity::new,
                    new Item.Settings().maxCount(1)
            )
    );


    private static Item register(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(BWT_HCTMod.MOD_ID, name), item);
    }

    public static void registerModItems() {
        BWT_HCTMod.LOGGER.info("Registering Mod Items for " + BWT_HCTMod.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(content -> {
            content.add(verticalWindmillItem);
        });
    }
}
