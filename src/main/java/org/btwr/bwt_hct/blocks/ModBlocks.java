package org.btwr.bwt_hct.blocks;

import com.bwt.blocks.BwtBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import org.btwr.bwt_hct.BWT_HCTMod;
import org.btwr.bwt_hct.blocks.blocks.ChoppingBlock;
import org.btwr.bwt_hct.blocks.blocks.DormantSoulForgeBlock;
import org.btwr.bwt_hct.blocks.blocks.ModernMillStoneBlock;

public class ModBlocks {

    public static final Block modernMillStoneBlock = registerBlock("modern_mill_stone",
            new ModernMillStoneBlock(AbstractBlock.Settings.copy(BwtBlocks.millStoneBlock))
    );

    public static final Block dormantSoulForge = registerBlock("dormant_soul_forge",
            new DormantSoulForgeBlock(AbstractBlock.Settings.copy(BwtBlocks.soulForgeBlock))
    );

    public static final Block choppingBlock = registerBlock("chopping_block",
            new ChoppingBlock(AbstractBlock.Settings.create()
                    .strength(2.0f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.STONE)
            )
    );

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(BWT_HCTMod.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, Identifier.of(BWT_HCTMod.MOD_ID, name),
                new BlockItem(block, new Item.Settings()));
    }

    public static void register() {
        BWT_HCTMod.LOGGER.debug("Registering ModBlocks for " + BWT_HCTMod.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> {
            //entries.add(modernMillStoneBlock.asItem());
            entries.addAfter(Blocks.BARREL, ModBlocks.choppingBlock.asItem());
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries ->
        {
            entries.add(dormantSoulForge.asItem());

        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(content -> {
            content.addAfter(Blocks.BARREL, ModBlocks.choppingBlock);
        });
    }

}