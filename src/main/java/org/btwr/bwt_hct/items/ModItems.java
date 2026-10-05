package org.btwr.bwt_hct.items;

import com.bwt.items.BwtItems;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import org.btwr.bwt_hct.BWT_HCTMod;
import org.btwr.bwt_hct.blocks.ModBlocks;

public class ModItems {

    public static final Item blastingOil = register("blasting_oil", new Item(new Item.Properties()));
    public static final Item fuse = register("fuse", new Item(new Item.Properties()));

    private static Item register(String name, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(BWT_HCTMod.MOD_ID, name), item);
    }

    public static void register() {
        BWT_HCTMod.LOGGER.info("Registering Mod Items for " + BWT_HCTMod.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.NATURAL_BLOCKS).register(content -> {
            //content.add(ModBlocks.modernMillStoneBlock);
            content.accept(ModBlocks.dormantSoulForge);
        });

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register(content -> {
            content.addAfter(BwtItems.concentratedHellfireItem, blastingOil);
            content.addBefore(blastingOil, fuse);
        });
    }

}