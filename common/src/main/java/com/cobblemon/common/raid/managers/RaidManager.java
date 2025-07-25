package com.cobblemon.common.raid.managers;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.api.drop.DropTable;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.battles.BattleBuilder;
import com.cobblemon.mod.common.battles.BattleFormat;
import com.cobblemon.mod.common.battles.actor.PokemonBattleActor;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.properties.UncatchableProperty;
import kotlin.Unit;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RaidManager {
    private static final Map<UUID, RaidBoss> raidMap = new HashMap<>();

    public static void joinRaid(ServerPlayer player, RaidBoss raid) {
        raid.addPlayer(player);
        raid.placePlayer(player);
    }

    public static void startRaid(ServerPlayer player, RaidBoss raid) {
        //TODO join shouldnt be from here
        joinRaid(player, raid);

        int partyIndex = canBattle(player);
        if (partyIndex == -1) {
            player.sendSystemMessage(Component.translatable("raid.alert.skill_issue")
                    .withStyle(ChatFormatting.RED), true);
            return;
        }

        Pokemon boss = raid.getBoss();

        PokemonEntity bossCloneEntity = createClone(boss, player);

        PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(player);
        BattleBuilder.INSTANCE.pve(player,
                bossCloneEntity,
                party.get(partyIndex).getUuid(),
                BattleFormat.Companion.getGEN_9_SINGLES(),
                false,
                false,
                Cobblemon.config.getDefaultFleeDistance(),
                party
        ).ifSuccessful((battle -> {
            battle.getOnEndHandlers().add((battleEnd) -> {
                for (BattleActor actor : battleEnd.getActors()) {
                    if (actor instanceof PokemonBattleActor pk) {
                        if (pk.getPokemon().getOriginalPokemon().getPersistentData().getBoolean("is_raid_boss") && pk.getPokemon().getOriginalPokemon().isFainted()) {
                            raid.damagePerWin();
                        } else if (pk.getPokemon().getOriginalPokemon().getPersistentData().getBoolean("is_raid_boss") && !pk.getPokemon().getOriginalPokemon().isFainted()) {
                            pk.getEntity().discard();
                        }
                    }
                }
                return Unit.INSTANCE;
            });
            return Unit.INSTANCE;
        }));
    }

    public static int canBattle(ServerPlayer player) {
        PlayerPartyStore playerPartyStore = Cobblemon.INSTANCE.getStorage().getParty(player);
        if (playerPartyStore.occupied() == 0) {
            return -1;
        }
        int index = 0;
        for (Pokemon pokemon : playerPartyStore) {
            if (!pokemon.isFainted()) {
                return index;
            }
            index++;
        }
        return -1;
    }

    public static final int NOT_A_BOSS = -1;
    public static final int BATTLE_PHASE = 0;
    public static final int PREPARE_PHASE = 1;
    public static final int CATCH_PHASE = 2;
    public static final EntityDataAccessor<Integer> RAID_BOSS_PHASE =
            SynchedEntityData.defineId(PokemonEntity.class, EntityDataSerializers.INT);

    public static void spawnBoss(ServerLevel level, RaidBoss raid, Vec3 pos) {
        raid.getBoss().getPersistentData().putBoolean("is_raid_boss", true);

        raid.getBoss().sendOut(level, pos, null, entity -> {
            entity.getEntityData().set(RAID_BOSS_PHASE, BATTLE_PHASE);
            entity.setNoAi(true);
            return Unit.INSTANCE;
        });

        raidMap.put(raid.getBoss().getUuid(), raid);
    }

    public static RaidBoss getRaid(UUID uuid) {
        return raidMap.get(uuid);
    }

    public static Collection<RaidBoss> getAllRaids() {
        return raidMap.values();
    }

    public static void endRaid(UUID uuid) {
        raidMap.remove(uuid);
    }

    //Helpers
    private static PokemonEntity createClone(Pokemon pokemon, ServerPlayer player) {
        Pokemon bossClone = pokemon.clone(true, player.getServer().registryAccess());
        bossClone.getCustomProperties().add(UncatchableProperty.INSTANCE.uncatchable());
        bossClone.setScaleModifier(0.1f);
        return bossClone.sendOut((ServerLevel) player.level(), pokemon.getEntity().position(), null, entity -> {
            entity.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, -1, 1, false, false));
            entity.setDrops(new DropTable());
            return Unit.INSTANCE;
        });
    }
}
