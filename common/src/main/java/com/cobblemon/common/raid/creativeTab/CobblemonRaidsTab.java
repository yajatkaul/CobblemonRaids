package com.cobblemon.common.raid.creativeTab;

import com.cobblemon.common.raid.CobblemonRaids;
import com.cobblemon.common.raid.blocks.RaidBlocks;
import com.cobblemon.common.raid.items.RaidItems;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

public class CobblemonRaidsTab {
    public static final CreativeModeTab RAID_ITEMS = PolymerItemGroupUtils.builder()
            .icon(RaidItems.RAID_BALL::getDefaultInstance)
            .title(Component.translatable("cobblemon_raids.creative_tab.name"))
            .displayItems((displayContext, entries) -> {
                // Key Items
                entries.accept(RaidItems.RAID_BALL);
                entries.accept(RaidItems.RAID_LOOT);
                entries.accept(RaidBlocks.RAID_SPOT);
            })
            .build();

    public static void register () {
        PolymerItemGroupUtils.registerPolymerItemGroup(
                ResourceLocation.fromNamespaceAndPath(CobblemonRaids.MOD_ID, "creative_tab") ,
                RAID_ITEMS
        );
    }
}
