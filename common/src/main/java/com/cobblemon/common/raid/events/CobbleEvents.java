package com.cobblemon.common.raid.events;

import com.cobblemon.common.raid.items.RaidItems;
import com.cobblemon.common.raid.managers.RaidBoss;
import com.cobblemon.common.raid.managers.RaidManager;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.pokeball.ThrownPokeballHitEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonCapturedEvent;
import com.cobblemon.mod.common.item.PokeBallItem;
import com.cobblemon.mod.common.pokeball.PokeBall;
import com.cobblemon.mod.common.pokemon.Pokemon;
import kotlin.Unit;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class CobbleEvents {
    public static void register() {
        CobblemonEvents.THROWN_POKEBALL_HIT.subscribe(Priority.NORMAL, CobbleEvents::threwBall);
        CobblemonEvents.POKEMON_CAPTURED.subscribe(Priority.NORMAL, CobbleEvents::bossCaught);
    }

    private static Unit bossCaught(PokemonCapturedEvent event) {
        Pokemon pokemon = event.getPokemon();
        CompoundTag compoundTag = pokemon.getPersistentData();

        if (compoundTag.contains("is_raid_boss")) {
            compoundTag.remove("is_raid_boss");
            compoundTag.putBoolean("raid_catch", true);
        }

        ServerPlayer player = event.getPlayer();
        RaidBoss raidBoss = RaidManager.getRaidFromPlayer(player);

        pokemon.setLevel(raidBoss.getCatchLevel());
        pokemon.setScaleModifier(raidBoss.getOrignalScale());
        if (raidBoss != null) {
            raidBoss.returnPlayer(player);
        }

        return Unit.INSTANCE;
    }

    private static Unit threwBall(ThrownPokeballHitEvent event) {
        Pokemon pokemon = event.getPokemon().getPokemon();
        PokeBall pokeBallItem = event.getPokeBall().getPokeBall();
        PokeBall raidBall = ((PokeBallItem) RaidItems.RAID_BALL.get()).getPokeBall();

        if (pokemon.getPersistentData().contains("is_raid_boss")) {
            if (pokeBallItem != raidBall) {
                if (event.getPokeBall().getOwner() instanceof ServerPlayer player) {
                    player.sendSystemMessage(Component.translatable("raid.illegal_catch"));
                }
                event.cancel();
            }
        }

        return Unit.INSTANCE;
    }

}
