package com.cobblemon.common.raid.events;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.pokeball.ThrownPokeballHitEvent;
import com.cobblemon.mod.common.pokemon.Pokemon;
import kotlin.Unit;

public class CobbleEvents {
    public static void register() {
        CobblemonEvents.THROWN_POKEBALL_HIT.subscribe(Priority.NORMAL, CobbleEvents::threwBall);
    }

    private static Unit threwBall(ThrownPokeballHitEvent event) {
        Pokemon pokemon = event.getPokemon().getPokemon();

        if (pokemon.getPersistentData().contains("is_raid_boss")) {
            pokemon.getPersistentData().remove("is_raid_boss");
        }

        return Unit.INSTANCE;
    }

}
