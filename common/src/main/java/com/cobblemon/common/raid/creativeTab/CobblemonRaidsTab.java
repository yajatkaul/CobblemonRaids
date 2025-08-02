package com.cobblemon.common.raid.creativeTab;

import com.cobblemon.common.raid.CobblemonRaids;
import com.cobblemon.common.raid.items.RaidItems;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class CobblemonRaidsTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(CobblemonRaids.MOD_ID, Registries.CREATIVE_MODE_TAB);

    public static final RegistrySupplier<CreativeModeTab> RAID_TAB = CREATIVE_TABS.register(
            "raid_tab",
            () -> CreativeTabRegistry.create(
                    Component.translatable("category.raid_tab"),
                    () -> new ItemStack(RaidItems.RAID_BALL.get()) // Icon
            )
    );

    public static void register() {
        CREATIVE_TABS.register();
    }
}
