package com.cobblemon.common.raid.creativeTab;

import com.cobblemon.common.raid.CobblemonRaids;
import com.cobblemon.common.raid.blocks.RaidBlocks;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class CobblemonRaidsCreativeTab {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(CobblemonRaids.MOD_ID, Registries.CREATIVE_MODE_TAB);

    public static final RegistrySupplier<CreativeModeTab> RAID_TAB = TABS.register(
            "raids_tab",
            () -> CreativeTabRegistry.create(
                    Component.translatable("creativeTab.cobblemon_raids.raids_tab"),
                    () -> new ItemStack(RaidBlocks.RAID_SPOT.get().asItem())
            )
    );

    public static void register() {
        TABS.register();
    }
}
