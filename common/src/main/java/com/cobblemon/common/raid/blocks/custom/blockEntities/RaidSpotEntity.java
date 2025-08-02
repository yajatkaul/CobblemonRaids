package com.cobblemon.common.raid.blocks.custom.blockEntities;

import com.cobblemon.common.raid.blocks.RaidEntities;
import com.cobblemon.common.raid.managers.RaidBoss;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class RaidSpotEntity extends BlockEntity {
    private RaidBoss raid = null;

    public RaidSpotEntity(BlockPos blockPos, BlockState blockState) {
        super(RaidEntities.RAID_SPOT_ENTITY, blockPos, blockState);
    }

    public void setRaid(RaidBoss raid) {
        this.raid = raid;
    }

    public RaidBoss getRaid() {
        return raid;
    }
}