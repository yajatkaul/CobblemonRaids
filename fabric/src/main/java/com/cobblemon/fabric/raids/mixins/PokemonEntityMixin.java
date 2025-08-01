package com.cobblemon.fabric.raids.mixins;

import com.cobblemon.common.raid.managers.RaidBoss;
import com.cobblemon.common.raid.managers.RaidManager;
import com.cobblemon.mod.common.entity.PoseType;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PokemonEntity.class)
public class PokemonEntityMixin {
    @Inject(method = "isInvulnerableTo", at = @At("HEAD"), cancellable = true)
    private void makeBossUnKillable(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        PokemonEntity pokemon = (PokemonEntity) (Object) this;

        int raidBossPhase = pokemon.getEntityData().get(RaidManager.RAID_BOSS_PHASE);
        if (raidBossPhase == RaidManager.BATTLE_PHASE || raidBossPhase == RaidManager.PREPARE_PHASE || raidBossPhase == RaidManager.CATCH_PHASE) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void injectCustomAttachment(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(RaidManager.RAID_BOSS_PHASE, RaidManager.NOT_A_BOSS);
    }

    @Inject(method = "isBattling", at = @At("HEAD"), cancellable = true, remap = false)
    public void isBattling(CallbackInfoReturnable<Boolean> cir) {
        PokemonEntity pokemon = (PokemonEntity) (Object) this;

        int raidBossPhase = pokemon.getEntityData().get(RaidManager.RAID_BOSS_PHASE);
        if (raidBossPhase == RaidManager.BATTLE_PHASE) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getCurrentPoseType", at = @At("HEAD"), cancellable = true, remap = false)
    private void forceBattlePoseType(CallbackInfoReturnable<PoseType> cir) {
        PokemonEntity pokemon = (PokemonEntity) (Object) this;

        int raidBossPhase = pokemon.getEntityData().get(RaidManager.RAID_BOSS_PHASE);
        if (raidBossPhase == RaidManager.BATTLE_PHASE) {
            cir.setReturnValue(PoseType.STAND);
        } else if (raidBossPhase == RaidManager.PREPARE_PHASE) {
            cir.setReturnValue(PoseType.SLEEP);
        } else if (raidBossPhase == RaidManager.CATCH_PHASE) {
            cir.setReturnValue(PoseType.STAND);
        }
    }

    @Inject(method = "canBattle", at = @At("HEAD"), cancellable = true, remap = false)
    private void canBattle(Player player, CallbackInfoReturnable<Boolean> cir) {
        PokemonEntity pokemonEntity = (PokemonEntity) (Object) this;

        int raidBossPhase = pokemonEntity.getEntityData().get(RaidManager.RAID_BOSS_PHASE);
        if ((raidBossPhase == RaidManager.BATTLE_PHASE || raidBossPhase == RaidManager.PREPARE_PHASE) && pokemonEntity.getPokemon().getPersistentData().getBoolean("is_raid_boss")) {
            if (raidBossPhase == RaidManager.BATTLE_PHASE) {
                RaidBoss raid = RaidManager.getRaid(pokemonEntity.getPokemon().getUuid());
                if (raid != null) {
                    RaidManager.startRaid((ServerPlayer) player, raid);
                }
            }
            cir.setReturnValue(false);
        }
    }
}