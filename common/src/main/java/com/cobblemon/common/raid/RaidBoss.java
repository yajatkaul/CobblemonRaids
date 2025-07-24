package com.cobblemon.common.raid;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.util.PlayerExtensionsKt;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;

import java.util.HashSet;
import java.util.Set;

public class RaidBoss {
    private final int maxHealth;
    private int currentHealth;
    private final int damagePerWin;
    private final int baseScale;
    private final Pokemon boss;
    private final ServerBossEvent bossEvent;
    private final Set<ServerPlayer> activePlayers = new HashSet<>();
    private boolean defeated = false;

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

    public RaidBoss(int maxHealth, int baseScale, Pokemon bossEntity, int damagePerWin){
        this.maxHealth = maxHealth;
        this.baseScale = baseScale;
        this.boss = bossEntity;
        this.currentHealth = maxHealth;
        this.damagePerWin = damagePerWin;

        this.bossEvent = new ServerBossEvent(
                Component.translatable("raid.phase.catch", battleDuration - phaseTime),
                BossEvent.BossBarColor.RED,
                BossEvent.BossBarOverlay.NOTCHED_12
        );

        this.boss.setScaleModifier(baseScale);
    }

    public void tick() {
        this.ticks++;
        if(this.ticks % 20 == 0) {
            if(ended) {
                return;
            }
            this.ticks = 0;
            this.phaseTime++;
            stateUpdate();
        }
    }

    public void endRaid() {
        RaidManager.endRaid(this.boss.getUuid());
        this.boss.getEntity().discard();
        for (ServerPlayer player: this.activePlayers) {
            removePlayer(player);
            PokemonBattle battle = Cobblemon.INSTANCE.getBattleRegistry().getBattleByParticipatingPlayerId(player.getUUID());
            if (battle != null){
                battle.end();
            }
        }
        this.ended = true;
    }

    private void stateUpdate() {
        if (this.currentPhase == Phase.BATTLE) {
            this.bossEvent.setName(Component.translatable("raid.phase.battle", battleDuration - phaseTime));
            if(phaseTime == battleDuration) {
                endRaid();
            }
        } else if (this.currentPhase == Phase.PREPARE){
            this.bossEvent.setName(Component.translatable("raid.phase.prepare", prepareDuration - phaseTime));
            this.bossEvent.setColor(BossEvent.BossBarColor.YELLOW);
            this.bossEvent.setProgress((float) prepareDuration - (float) phaseTime / prepareDuration);
            if(phaseTime == prepareDuration) {
                phaseTime = 0;
                this.currentPhase = Phase.CATCH;
            }
        } else if(this.currentPhase == Phase.CATCH){
            boss.getEntity().getEntityData().set(RaidManager.RAID_BOSS_PHASE, RaidManager.CATCH_PHASE);
            this.bossEvent.setName(Component.translatable("raid.phase.catch", catchDuration - phaseTime));
            this.bossEvent.setProgress((float) catchDuration - (float) phaseTime / catchDuration);
            this.bossEvent.setColor(BossEvent.BossBarColor.GREEN);
            if(phaseTime == catchDuration) {
                endRaid();
            }
        }
    }

    public void damagePerWin() {
        this.currentHealth -= this.damagePerWin;
        this.bossEvent.setProgress((float) this.currentHealth /this.maxHealth);
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

    public void removePlayer(ServerPlayer player){
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
