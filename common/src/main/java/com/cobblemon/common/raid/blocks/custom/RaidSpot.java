package com.cobblemon.common.raid.blocks.custom;

import com.cobblemon.common.raid.managers.RaidBoss;
import com.cobblemon.common.raid.managers.RaidManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class RaidSpot extends Block {
    private RaidBoss raid;
    public RaidSpot(Properties properties) {
        super(properties);
    }

    public void setRaid(RaidBoss boss) {
        this.raid = boss;
    }

    public RaidBoss getRaid() {
        return this.raid;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
        if(player.level().isClientSide){
            return InteractionResult.PASS;
        }

        RaidManager.joinRaid((ServerPlayer) player, raid);

        return InteractionResult.SUCCESS;
    }
}
