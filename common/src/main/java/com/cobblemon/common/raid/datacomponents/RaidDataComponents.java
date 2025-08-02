package com.cobblemon.common.raid.datacomponents;

import com.cobblemon.common.raid.CobblemonRaids;
import com.mojang.serialization.Codec;
import eu.pb4.polymer.core.api.other.PolymerComponent;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class RaidDataComponents {

    public static final DataComponentType<List<ItemStack>> LOOT_COMPONENT = register("loot_component",
            builder -> builder
                    .persistent(Codec.list(ItemStack.CODEC)));

    private static <T> DataComponentType<T> register (String name, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        DataComponentType<T> component = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,
                ResourceLocation.fromNamespaceAndPath(CobblemonRaids.MOD_ID, name),
                builderOperator.apply(DataComponentType.builder()).build());
        PolymerComponent.registerDataComponent(component);
        return component;
    }

    public static void register() {

    }
}