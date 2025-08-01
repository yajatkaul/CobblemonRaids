package com.cobblemon.common.raid.items;

import com.cobblemon.common.raid.CobblemonRaids;
import com.cobblemon.common.raid.items.custom.items.RaidLoot;
import com.cobblemon.common.raid.items.pokeballs.RaidBalls;
import com.cobblemon.mod.common.item.PokeBallItem;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;

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

    private static <T extends SimplePolymerItem> T register (String name, ItemConstructor<T> itemConstructor) {
        return register(name, Items.IRON_INGOT, new Item.Settings().maxCount(64).rarity(Rarity.RARE), itemConstructor);
    }

    private static <T extends SimplePolymerItem> T register (String name, Item baseItem, Item.Settings settings, ItemConstructor<T> itemConstructor) {
        ResourceLocation itemId = new ResourceLocation.fromNamespaceAndPath(CobblemonRaids.MOD_ID, name);
        PolymerModelData model = PolymerResourcePackUtils.requestModel(baseItem, itemId.withPrefixedPath("item/"));
        return Registry.register(Registries.ITEM, itemId, itemConstructor.get(settings, baseItem, model));
    }

    public static void registerBalls() {
        RaidBalls.RAID_BALL.setItem$common((PokeBallItem) RaidItems.RAID_BALL.get());
    }
}
