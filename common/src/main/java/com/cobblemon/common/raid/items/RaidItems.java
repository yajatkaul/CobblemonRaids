package com.cobblemon.common.raid.items;

import com.cobblemon.common.raid.CobblemonRaids;
import com.cobblemon.common.raid.items.custom.items.RaidLoot;
import com.cobblemon.common.raid.items.pokeballs.RaidBalls;
import com.cobblemon.mod.common.item.PokeBallItem;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class RaidItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(CobblemonRaids.MOD_ID, Registries.ITEM);

    public static final RegistrySupplier<Item> RAID_LOOT = registerItem("raid_loot",
            () -> new RaidLoot(new Item.Properties().stacksTo(1)));

    public static final RegistrySupplier<Item> RAID_BALL = registerPokeball("raid_ball",
            () -> new PokeBallItem(RaidBalls.RAID_BALL));

    public static RegistrySupplier<Item> registerItem(String name, Supplier<Item> item) {
        return ITEMS.register(name, item);
    }

    public static RegistrySupplier<Item> registerPokeball(String name, Supplier<Item> item) {
        return ITEMS.register(name, item);
    }

    public static void registerBalls() {
        RaidBalls.RAID_BALL.setItem$common((PokeBallItem) RaidItems.RAID_BALL.get());
    }

    public static void register() {
        ITEMS.register();
    }
}
