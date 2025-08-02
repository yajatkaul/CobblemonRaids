package com.cobblemon.common.raid.managers;

import com.cobblemon.common.raid.codecs.RaidDen;
import com.cobblemon.common.raid.datacomponents.RaidDataComponents;
import com.cobblemon.common.raid.items.RaidItems;
import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.properties.UncatchableProperty;
import kotlin.Unit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.List;
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
    private final RaidDen raidDen;
    private boolean started = false;
    private final ResourceKey<LootTable> winLootTable;
    private final ResourceKey<LootTable> defeatLootTable;
    private final int ballCount; //POKEBALL!!!!!

    public enum Phase {
        PRE_BATTLE,
        BATTLE,
        PREPARE,
        CATCH
    }

    public Phase currentPhase;
    private final long preBattleDuration;
    private final long battleDuration;
    private final long prepareDuration;
    private final long catchDuration;
    private long phaseTime = 0;
    private final int maxPlayers;
    private final int catchLevel;
    private final float orignalScale;

    private static class Connection {
        public final BlockPos connectionBlock;
        public final ServerLevel level;

        public Connection(BlockPos pos, ServerLevel level) {
            this.connectionBlock = pos;
            this.level = level;
        }
    }

    private final Connection connection;

    private boolean bossSpawned = false;

    private boolean ended = false;

    public RaidBoss(int maxHealth,
                    int baseScale,
                    Pokemon bossEntity,
                    int damagePerWin,
                    RaidDen raidDen,
                    int maxPlayers,
                    ServerLevel level,
                    BlockPos connectionBlock,
                    long preBattleDuration,
                    long battleDuration,
                    long prepareDuration,
                    long catchDuration,
                    String winLoot,
                    String defeatLoot,
                    int ballCount,
                    int catchLevel,
                    int bossLevel
    ) {
        this.maxHealth = maxHealth;
        this.baseScale = baseScale;
        this.boss = bossEntity;
        this.currentHealth = maxHealth;
        this.damagePerWin = damagePerWin;
        this.raidDen = raidDen;
        this.maxPlayers = maxPlayers;
        this.connection = new Connection(connectionBlock, level);
        this.preBattleDuration = preBattleDuration;
        this.battleDuration = battleDuration;
        this.prepareDuration = prepareDuration;
        this.catchDuration = catchDuration;
        this.winLootTable = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.tryParse(winLoot));
        this.defeatLootTable = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.tryParse(defeatLoot));
        this.ballCount = ballCount;
        this.catchLevel = catchLevel;

        this.bossEvent = new ServerBossEvent(
                Component.translatable("raid.phase.pre_battle", preBattleDuration - phaseTime),
                BossEvent.BossBarColor.PURPLE,
                BossEvent.BossBarOverlay.NOTCHED_12
        );

        this.orignalScale = this.boss.getScaleModifier();
        this.boss.setScaleModifier(baseScale);
        this.boss.setLevel(bossLevel);
        this.currentPhase = Phase.PRE_BATTLE;
        UncatchableProperty.INSTANCE.uncatchable().apply(this.boss);
        RaidManager.addRaid(this);
        DenManager.occupyDen(this.raidDen);
    }

    public float getOrignalScale() {
        return this.orignalScale;
    }

    public int getCatchLevel() {
        return this.catchLevel;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public void tick(MinecraftServer server) {
        if (server.getTickCount() % 20 == 0) {
            if (ended) {
                return;
            }
            this.phaseTime++;
            stateUpdate();
        }
    }

    public void endRaid() {
        RaidManager.endRaid(this.boss.getUuid());
        DenManager.freeDen(this.raidDen);
        for (ServerPlayer player : this.activePlayers) {
            PokemonBattle battle = Cobblemon.INSTANCE.getBattleRegistry().getBattleByParticipatingPlayerId(player.getUUID());
            if (battle != null) {
                battle.end();
            }

            removePlayer(player);

            ResourceKey<LootTable> lootPool = null;
            if(this.defeated && this.winLootTable != null){
                lootPool = this.winLootTable;
            }else if (!this.defeated && this.defeatLootTable != null){
                lootPool = this.defeatLootTable;
            }

            if(lootPool != null){
                List<ItemStack> itemStack = resolveItemsToEject((ServerLevel) player.level(), lootPool, player.getOnPos(), player);
                ItemStack stackToGive = new ItemStack(RaidItems.RAID_LOOT);
                stackToGive.set(RaidDataComponents.LOOT_COMPONENT.get(), itemStack);
                RaidUtils.giveItems(player, stackToGive);
            }
        }
        this.ended = true;
    }

    private void stateUpdate() {
        if (this.currentPhase == Phase.BATTLE) {
            this.bossEvent.setName(Component.translatable("raid.phase.battle", RaidUtils.formatTime((int) (battleDuration - phaseTime))));
            if (phaseTime == battleDuration) {
                endRaid();
            }
        } else if (this.currentPhase == Phase.PRE_BATTLE) {
            this.bossEvent.setName(Component.translatable("raid.phase.pre_battle", RaidUtils.formatTime((int) (preBattleDuration - phaseTime))));
            this.bossEvent.setProgress(((float) preBattleDuration - (float) phaseTime) / preBattleDuration);
            if (phaseTime == preBattleDuration) {
                phaseTime = 0;
                this.started = true;
                if (this.activePlayers.isEmpty()) {
                    endRaid();
                    this.connection.level.removeBlock(this.connection.connectionBlock, true);
                    return;
                }
                PokemonEntity pokemonEntity = this.getBoss().getEntity();
                if (pokemonEntity != null) {
                    pokemonEntity.getEntityData().set(RaidManager.RAID_BOSS_PHASE, RaidManager.BATTLE_PHASE);
                }
                this.bossEvent.setProgress((float) this.currentHealth / this.maxHealth);
                this.currentPhase = Phase.BATTLE;
                this.connection.level.removeBlock(this.connection.connectionBlock, true);
            }
        } else if (this.currentPhase == Phase.PREPARE) {
            this.bossEvent.setName(Component.translatable("raid.phase.prepare", RaidUtils.formatTime((int) (prepareDuration - phaseTime))));
            this.bossEvent.setProgress(((float) prepareDuration - phaseTime) / (float) prepareDuration);
            if (phaseTime == prepareDuration) {
                phaseTime = 0;
                UncatchableProperty.INSTANCE.catchable().apply(this.boss);
                PokemonEntity bossEntity = this.boss.getEntity();
                if (bossEntity != null) {
                    bossEntity.discard();
                }
                placePlayersCatchPhase();
                givePlayersBalls();
                this.currentPhase = Phase.CATCH;
                this.bossEvent.setColor(BossEvent.BossBarColor.GREEN);
            }
        } else if (this.currentPhase == Phase.CATCH) {
            this.bossEvent.setName(Component.translatable("raid.phase.catch", RaidUtils.formatTime((int) (catchDuration - phaseTime))));
            this.bossEvent.setProgress((float) catchDuration - phaseTime / (float) catchDuration);
            if (phaseTime == catchDuration) {
                endRaid();
            }
        }
    }

    public void placePlayer(ServerPlayer player) {
        Vec3 spawns = this.raidDen.denSpawn();
        player.teleportTo(player.getServer().getLevel(
                        this.raidDen.denLevel()),
                spawns.x,
                spawns.y,
                spawns.z,
                player.getYRot(),
                player.getXRot()
        );
    }

    public void returnPlayer(ServerPlayer player) {
        player.teleportTo(this.connection.level,
                this.connection.connectionBlock.getX(),
                this.connection.connectionBlock.getY() + 1,
                this.connection.connectionBlock.getZ(),
                player.getYRot(),
                player.getXRot()
        );
    }

    private void givePlayersBalls() {
        //POKEBALLS!!! ^^^^^^^^^
        ItemStack itemStack = new ItemStack(RaidItems.RAID_BALL, this.ballCount);
        RaidUtils.giveItems(this.activePlayers, itemStack);
    }


    private void takePlayerBalls(ServerPlayer player) {
        //POKEBALLS!!! ^^^^^^^^^
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(RaidItems.RAID_BALL.get())) {
                stack.shrink(stack.getCount());
            }
        }
    }

    public void placePlayersCatchPhase() {
        AtomicInteger index = new AtomicInteger(this.activePlayers.size() - 1);
        this.activePlayers.iterator().forEachRemaining((player -> {
            Pokemon cloneBoss = this.boss.clone(true, player.registryAccess());
            cloneBoss.sendOut((ServerLevel) player.level(), this.raidDen.catchSpawns().get(index.get()).bossSpawn(), null, (pokemonEntity -> {
                pokemonEntity.getEntityData().set(RaidManager.RAID_BOSS_PHASE, RaidManager.CATCH_PHASE);
                pokemonEntity.setNoAi(true);
                pokemonEntity.after(catchDuration, () -> {
                    pokemonEntity.discard();
                    return Unit.INSTANCE;
                });
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
            this.boss.getEntity().getEntityData().set(RaidManager.RAID_BOSS_PHASE, RaidManager.PREPARE_PHASE);
            this.currentPhase = Phase.PREPARE;
            this.bossEvent.setColor(BossEvent.BossBarColor.YELLOW);
            this.phaseTime = 0;
            this.defeated = true;
        }
    }

    public Pokemon getBoss() {
        return this.boss;
    }

    public RaidDen getRaidDen() {
        return this.raidDen;
    }

    public int getBaseScale() {
        return this.baseScale;
    }

    public void addPlayer(ServerPlayer player) {
        if (this.activePlayers.size() == this.maxPlayers || this.started) {
            return;
        }

        if (!bossSpawned) {
            RaidManager.spawnBoss((ServerLevel) player.level(), this);
            this.bossSpawned = true;
        }

        this.activePlayers.add(player);
        RaidManager.addPlayerToRaidMap(player, this);
        this.bossEvent.addPlayer(player);
        placePlayer(player);
    }

    public void removePlayer(ServerPlayer player) {
        this.activePlayers.remove(player);
        this.bossEvent.removePlayer(player);
        takePlayerBalls(player);
        RaidManager.removePlayerToRaidMap(player);
        returnPlayer(player);
    }

    public Set<ServerPlayer> getPlayers() {
        return this.activePlayers;
    }

    public boolean getDefeated() {
        return this.defeated;
    }

    private static List<ItemStack> resolveItemsToEject(ServerLevel level,
                                                       ResourceKey<LootTable> lootTableId,
                                                       BlockPos pos,
                                                       Player player) {
        LootTable lootTable = level.getServer().reloadableRegistries().getLootTable(lootTableId);
        LootParams lootParams = (new LootParams.Builder(level))
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withLuck(player.getLuck())
                .withParameter(LootContextParams.THIS_ENTITY, player)
                .create(LootContextParamSets.VAULT);

        return lootTable.getRandomItems(lootParams);
    }
}