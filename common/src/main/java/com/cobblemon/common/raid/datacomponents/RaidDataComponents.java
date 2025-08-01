package com.cobblemon.common.raid.datacomponents;

import com.cobblemon.common.raid.CobblemonRaids;
import com.mojang.serialization.Codec;
import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Supplier;

public class RaidDataComponents {
    private static final DeferredRegister<DataComponentType<?>> REGISTRAR = DeferredRegister.create(CobblemonRaids.MOD_ID, Registries.DATA_COMPONENT_TYPE);

    public static final Supplier<DataComponentType<List<ItemStack>>> LOOT_COMPONENT = REGISTRAR.register(
            "loot_component",
            () -> DataComponentType.<List<ItemStack>>builder()
                    .persistent(Codec.list(ItemStack.CODEC))
                    .build()
    );

    public static void register() {
        REGISTRAR.register();
    }
}