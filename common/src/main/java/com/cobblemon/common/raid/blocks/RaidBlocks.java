package com.cobblemon.common.raid.blocks;

import com.cobblemon.common.raid.CobblemonRaids;
import com.cobblemon.common.raid.blocks.custom.blocks.RaidSpot;
import com.cobblemon.common.raid.creativeTab.CobblemonRaidsTab;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

public class RaidBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(CobblemonRaids.MOD_ID, Registries.BLOCK);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(CobblemonRaids.MOD_ID, Registries.ITEM);

    public static final RegistrySupplier<Block> RAID_SPOT = registerBlock("raid_spot",
            () -> new RaidSpot(BlockBehaviour.Properties.of()
                    .strength(1f)));

    public static RegistrySupplier<Block> registerBlock(String name, Supplier<Block> block) {
        RegistrySupplier<Block> blockSupplier = BLOCKS.register(name, block);
        ITEMS.register(name, () -> new BlockItem(blockSupplier.get(),
                new Item.Properties()
                .arch$tab(CobblemonRaidsTab.RAID_TAB)));
        return blockSupplier;
    }

    public static void register() {
        BLOCKS.register();
        ITEMS.register();
    }
}
