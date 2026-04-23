package org.btwr.bwt_hct.items;

import com.bwt.items.BwtItems;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.btwr.bwt_hct.BWT_HCTMod;
import org.btwr.bwt_hct.blocks.ModBlocks;

public class ModItems {

    public static final Item blastingOil = register("blasting_oil", new Item(new Item.Settings()));
    public static final Item fuse = register("fuse", new Item(new Item.Settings()));

    private static Item register(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(BWT_HCTMod.MOD_ID, name), item);
    }

    public static void register() {
        BWT_HCTMod.LOGGER.info("Registering Mod Items for " + BWT_HCTMod.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register(content -> {
            //content.add(ModBlocks.modernMillStoneBlock);
            content.add(ModBlocks.dormantSoulForge);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(content -> {
            content.addAfter(BwtItems.concentratedHellfireItem, blastingOil);
            content.addBefore(blastingOil, fuse);
        });
    }

}