package com.cobblemon.common.raid.items;

import com.cobblemon.common.raid.CobblemonRaids;
import com.cobblemon.common.raid.items.custom.items.RaidLoot;
import com.cobblemon.common.raid.items.pokeballs.RaidBalls;
import com.cobblemon.mod.common.item.PokeBallItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class RaidItems {

    public static final Item RAID_LOOT = registerItem("raid_loot",
            new RaidLoot(new Item.Properties().stacksTo(1), Items.IRON_INGOT));

    public static final PokeBallItem RAID_BALL = registerPokeball("raid_ball",
            new PokeBallItem(RaidBalls.RAID_BALL));

    private static Item registerItem(String name, Item item) {
        return Registry.register(BuiltInRegistries.ITEM,
                ResourceLocation.fromNamespaceAndPath(CobblemonRaids.MOD_ID, name), item);
    }

    private static PokeBallItem registerPokeball(String name, PokeBallItem item) {
        return Registry.register(BuiltInRegistries.ITEM,
                ResourceLocation.fromNamespaceAndPath(CobblemonRaids.MOD_ID, name), item);
    }

    public static void registerBalls() {
        RaidBalls.RAID_BALL.setItem$common(RAID_BALL);
    }

    public static void register() {

    }
}