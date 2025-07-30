package com.cobblemon.common.raid.items.pokeballs;

import com.cobblemon.common.raid.CobblemonRaids;
import com.cobblemon.common.raid.items.RaidItems;
import com.cobblemon.mod.common.api.pokeball.catching.modifiers.MultiplierModifier;
import com.cobblemon.mod.common.item.PokeBallItem;
import com.cobblemon.mod.common.pokeball.PokeBall;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

import static com.cobblemon.mod.common.util.MiscUtilsKt.cobblemonResource;

public class RaidBalls {
    public static final PokeBall RAID_BALL = new PokeBall(ResourceLocation.fromNamespaceAndPath(CobblemonRaids.MOD_ID, "raid_ball"),
            new MultiplierModifier(1.0f, (thrower, pokemon) -> true),
            List.of(), // empty list
            0.8f,
            ResourceLocation.fromNamespaceAndPath(CobblemonRaids.MOD_ID,"raid_ball"),
            ResourceLocation.fromNamespaceAndPath(CobblemonRaids.MOD_ID,"item/raid_ball_model"),
            1.25f,
            false);
}