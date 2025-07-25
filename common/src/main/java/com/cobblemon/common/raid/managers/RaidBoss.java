package com.cobblemon.common.raid.managers;

import com.cobblemon.common.raid.codecs.RaidDen;
import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.properties.UncatchableProperty;
import kotlin.Unit;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class RaidBoss {
    private final int maxHealth;
    private int currentHealth;
    private final int damagePerWin;
    private final int baseScale;
    private final Pokemon boss;
    private final ServerBossEvent bossEvent;
    private final Set<ServerPlayer> activePlayers = new HashSet<>();
    private boolean defeated = false;
    private RaidDen raidDen;

    public enum Phase {
        BATTLE,
        PREPARE,
        CATCH
    }

    public Phase currentPhase = Phase.BATTLE;
    private final long battleDuration = 40;
    private final long prepareDuration = 10;
    private final long catchDuration = 30;
    private long ticks = 0;
    private long phaseTime = 0;

    private boolean ended = false;

    public RaidBoss(int maxHealth, int baseScale, Pokemon bossEntity, int damagePerWin, RaidDen raidDen) {
        this.maxHealth = maxHealth;
        this.baseScale = baseScale;
        this.boss = bossEntity;
        this.currentHealth = maxHealth;
        this.damagePerWin = damagePerWin;
        this.raidDen = raidDen;

        this.bossEvent = new ServerBossEvent(
                Component.translatable("raid.phase.catch", battleDuration - phaseTime),
                BossEvent.BossBarColor.RED,
                BossEvent.BossBarOverlay.NOTCHED_12
        );

        this.boss.setScaleModifier(baseScale);
        UncatchableProperty.INSTANCE.uncatchable().apply(this.boss);
    }

    public void tick() {
        this.ticks++;
        if (this.ticks % 20 == 0) {
            if (ended) {
                return;
            }
            this.ticks = 0;
            this.phaseTime++;
            stateUpdate();
        }
    }

    public void endRaid() {
        RaidManager.endRaid(this.boss.getUuid());
        for (ServerPlayer player : this.activePlayers) {
            removePlayer(player);
            PokemonBattle battle = Cobblemon.INSTANCE.getBattleRegistry().getBattleByParticipatingPlayerId(player.getUUID());
            if (battle != null) {
                battle.end();
            }
        }
        this.ended = true;
    }

    private void stateUpdate() {
        if (this.currentPhase == Phase.BATTLE) {
            this.bossEvent.setName(Component.translatable("raid.phase.battle", battleDuration - phaseTime));
            if (phaseTime == battleDuration) {
                endRaid();
            }
        } else if (this.currentPhase == Phase.PREPARE) {
            this.bossEvent.setName(Component.translatable("raid.phase.prepare", prepareDuration - phaseTime));
            this.bossEvent.setColor(BossEvent.BossBarColor.YELLOW);
            this.bossEvent.setProgress(((float) prepareDuration - (float) phaseTime) / prepareDuration);
            if (phaseTime == prepareDuration) {
                phaseTime = 0;
                UncatchableProperty.INSTANCE.catchable().apply(this.boss);
                PokemonEntity bossEntity = this.boss.getEntity();
                if(bossEntity != null){
                    bossEntity.discard();
                }
                placePlayersCatchPhase();
                this.currentPhase = Phase.CATCH;
            }
        } else if (this.currentPhase == Phase.CATCH) {
            this.bossEvent.setName(Component.translatable("raid.phase.catch", catchDuration - phaseTime));
            this.bossEvent.setProgress((float) catchDuration - phaseTime / (float) catchDuration);
            this.bossEvent.setColor(BossEvent.BossBarColor.GREEN);
            if (phaseTime == catchDuration) {
                endRaid();
            }
        }
    }

    public void placePlayer(ServerPlayer player) {
        int index = this.activePlayers.size() - 1;
        Vec3 spawns = this.raidDen.denSpawns().get(index);

        player.teleportTo((ServerLevel) player.level(), spawns.x, spawns.y, spawns.z, player.getYRot(), player.getXRot());
    }

    public void placePlayersCatchPhase() {
        AtomicInteger index = new AtomicInteger(this.activePlayers.size() - 1);
        this.activePlayers.iterator().forEachRemaining((player -> {
            Pokemon cloneBoss = this.boss.clone(true, player.registryAccess());
            cloneBoss.sendOut((ServerLevel) player.level(), this.raidDen.catchSpawns().get(index.get()).bossSpawn(), null, (pokemonEntity -> {
                pokemonEntity.getEntityData().set(RaidManager.RAID_BOSS_PHASE, RaidManager.CATCH_PHASE);
                pokemonEntity.setNoAi(true);
                return Unit.INSTANCE;
            }));
            Vec3 spawns = this.raidDen.catchSpawns().get(index.get()).playerSpawn();
            player.teleportTo((ServerLevel) player.level(), spawns.x, spawns.y, spawns.z, player.getYRot(), player.getXRot());
            index.getAndDecrement();
        }));
    }

    public void damagePerWin() {
        this.currentHealth -= this.damagePerWin;
        this.bossEvent.setProgress((float) this.currentHealth / this.maxHealth);
        if (this.currentHealth <= 0) {
            boss.getEntity().getEntityData().set(RaidManager.RAID_BOSS_PHASE, RaidManager.PREPARE_PHASE);
            this.currentPhase = Phase.PREPARE;
            this.phaseTime = 0;
            this.defeated = true;
        }
    }

    public Pokemon getBoss() {
        return this.boss;
    }

    public int getBaseScale() {
        return this.baseScale;
    }

    public void addPlayer(ServerPlayer player) {
        this.activePlayers.add(player);
        this.bossEvent.addPlayer(player);
    }

    public void removePlayer(ServerPlayer player) {
        this.activePlayers.remove(player);
        this.bossEvent.removePlayer(player);
    }

    public Set<ServerPlayer> getPlayers() {
        return this.activePlayers;
    }

    public boolean getDefeated() {
        return this.defeated;
    }
}
