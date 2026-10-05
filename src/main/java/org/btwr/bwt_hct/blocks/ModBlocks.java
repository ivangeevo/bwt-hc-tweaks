package org.btwr.bwt_hct.blocks;

import com.bwt.blocks.BwtBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.btwr.bwt_hct.BWT_HCTMod;
import org.btwr.bwt_hct.blocks.blocks.ChoppingBlock;
import org.btwr.bwt_hct.blocks.blocks.DormantSoulForgeBlock;
import org.btwr.bwt_hct.blocks.blocks.ModernMillStoneBlock;

public class ModBlocks {

    public static final Block modernMillStoneBlock = registerBlock("modern_mill_stone",
            new ModernMillStoneBlock(BlockBehaviour.Properties.ofFullCopy(BwtBlocks.millStoneBlock))
    );

    public static final Block dormantSoulForge = registerBlock("dormant_soul_forge",
            new DormantSoulForgeBlock(BlockBehaviour.Properties.ofFullCopy(BwtBlocks.soulForgeBlock))
    );

    public static final Block choppingBlock = registerBlock("chopping_block",
            new ChoppingBlock(BlockBehaviour.Properties.of()
                    .strength(2.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)
            )
    );

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(BWT_HCTMod.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(BWT_HCTMod.MOD_ID, name),
                new BlockItem(block, new Item.Properties()));
    }

    public static void register() {
        BWT_HCTMod.LOGGER.debug("Registering ModBlocks for " + BWT_HCTMod.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(entries -> {
            //entries.add(modernMillStoneBlock.asItem());
            entries.addAfter(Blocks.BARREL, ModBlocks.choppingBlock.asItem());
        });

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register(entries ->
        {
            entries.accept(dormantSoulForge.asItem());

        });

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(content -> {
            content.addAfter(Blocks.BARREL, ModBlocks.choppingBlock);
        });
    }

}