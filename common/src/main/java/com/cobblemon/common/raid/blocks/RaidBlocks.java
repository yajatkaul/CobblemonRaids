package com.cobblemon.common.raid.blocks;

import com.cobblemon.common.raid.CobblemonRaids;
import com.cobblemon.common.raid.blocks.custom.blocks.RaidSpot;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

public class RaidBlocks {
    public static final Block RAID_SPOT = registerBlock("raid_spot",
            new RaidSpot(BlockBehaviour.Properties.of()
                    .strength(1f)),
            new Item.Properties());

    private static Block registerBlock(String name, Block block, Item.Properties itemProps) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(CobblemonRaids.MOD_ID, name);
        Registry.register(BuiltInRegistries.BLOCK, id, block);

        // Register block item with optional Polymer support
        BlockItem blockItem = new BlockItem(block, itemProps);
        Registry.register(BuiltInRegistries.ITEM, id, blockItem);

        return block;
    }

    public static void register() {

    }
}
