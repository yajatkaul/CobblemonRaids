package com.cobblemon.common.raid.managers;

import com.cobblemon.common.raid.blocks.RaidBlocks;
import com.cobblemon.common.raid.blocks.custom.blockEntities.RaidSpotEntity;
import com.cobblemon.common.raid.blocks.custom.blocks.RaidSpot;
import com.cobblemon.common.raid.codecs.RaidData;
import com.cobblemon.common.raid.codecs.RaidDen;
import com.cobblemon.common.raid.codecs.Webhook;
import com.cobblemon.common.raid.codecs.pokemon.RaidMon;
import com.cobblemon.common.raid.config.CobblemonRaidsConfig;
import com.cobblemon.common.raid.datapack.DatapackRegister;
import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.api.drop.DropTable;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.battles.BattleBuilder;
import com.cobblemon.mod.common.battles.BattleFormat;
import com.cobblemon.mod.common.battles.actor.PokemonBattleActor;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.properties.UncatchableProperty;
import kotlin.Unit;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.*;

public class RaidManager {
    private static final Map<UUID, RaidBoss> raidMap = new HashMap<>();
    private static final Map<ServerPlayer, RaidBoss> serverRaidMap = new HashMap<>();

    public static void addPlayerToRaidMap(ServerPlayer player, RaidBoss raid) {
        serverRaidMap.put(player, raid);
    }

    public static void removePlayerToRaidMap(ServerPlayer player) {
        serverRaidMap.remove(player);
    }

    public static RaidBoss getRaidFromPlayer(ServerPlayer player) {
        return serverRaidMap.get(player);
    }

    public static void startRaid(ServerPlayer player, RaidBoss raid) {
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
    public static final int PRE_BATTLE_PHASE = 1;
    public static final int BATTLE_PHASE = 0;
    public static final int PREPARE_PHASE = 1;
    public static final int CATCH_PHASE = 2;
    public static final EntityDataAccessor<Integer> RAID_BOSS_PHASE =
            SynchedEntityData.defineId(PokemonEntity.class, EntityDataSerializers.INT);

    public static int raidCoolDown = CobblemonRaidsConfig.raidCoolDown * 20;

    public static void tick(MinecraftServer server) {
        if (server.getTickCount() % raidCoolDown == 0) { // every raidCoolDown seconds
            ServerLevel level = server.getLevel(DimensionUtils.rollDimension());
            if (level != null) {
                float roll = new Random().nextFloat() * 100;
                if (roll >= CobblemonRaidsConfig.raidOccurrencePercentage) {
                    return;
                }

                BlockPos pos = SpawnUtils.getRaidSpawnPos(level);

                if (pos == null) return;

                RaidData raidData = SpawnUtils.spawnRaid(level, pos);

                if (raidData == null) return;

                RaidDen den = DenManager.getRandomDen(raidData.raidType());

                if (den == null || DatapackRegister.raidsRegistry.size() == 0) return;

                Pokemon pokemon = PokemonProperties.Companion.parse(raidData.raidMon().pokemon()).create();

                server.sendSystemMessage(Component.literal(pos.toString()));
                server.sendSystemMessage(Component.literal(level.getLevel().dimension().toString()));
                level.setBlock(pos, RaidBlocks.RAID_SPOT.defaultBlockState(), Block.UPDATE_ALL);

                RaidBoss raid = new RaidBoss(raidData.raidMon().maxHealth(),
                        raidData.raidMon().baseScale(),
                        pokemon,
                        raidData.raidMon().damagePerWin(),
                        den,
                        raidData.raidMon().maxPlayers(),
                        level,
                        pos,
                        raidData.preBattleDuration(),
                        raidData.battleDuration(),
                        raidData.prepareDuration(),
                        raidData.catchDuration(),
                        raidData.lootTables().winLoot().orElse(null),
                        raidData.lootTables().defeatLoot().orElse(null),
                        raidData.totalBalls(),
                        raidData.raidMon().catchLevel(),
                        raidData.raidMon().bossLevel()
                );

                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof RaidSpotEntity raidSpotEntity) {
                    raidSpotEntity.setRaid(raid);
                }

                Webhook webhook = Webhook.loadFromJson(server);
                if (webhook != null) {
                    RaidMon raidMon = raidData.raidMon();
                    webhook.sendWebhook(raidMon.pokemonImage().orElse(null),
                            raidMon.name(),
                            raidData.battleDuration(),
                            pos.toString(),
                            level.dimension().location().toString(),
                            pokemon,
                            String.valueOf(raidData.raidMon().catchLevel())
                    );
                }
            }
        }
    }

    public static void spawnBoss(ServerLevel level, RaidBoss raid) {
        raid.getBoss().getPersistentData().putBoolean("is_raid_boss", true);

        raid.getBoss().sendOut(level, raid.getRaidDen().bossSpawn(), null, entity -> {
            entity.getEntityData().set(RAID_BOSS_PHASE, PRE_BATTLE_PHASE);
            entity.setNoAi(true);
            entity.setDrops(new DropTable());
            return Unit.INSTANCE;
        });
    }

    public static RaidBoss getRaid(UUID uuid) {
        return raidMap.get(uuid);
    }

    public static void addRaid(RaidBoss raid) {
        raidMap.put(raid.getBoss().getUuid(), raid);
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
