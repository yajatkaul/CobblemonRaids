package com.cobblemon.common.raid.blocks.custom.blockEntities;

import com.cobblemon.common.raid.blocks.RaidEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class RaidSpotEntity extends BlockEntity {
    public RaidSpotEntity(BlockPos blockPos, BlockState blockState) {
        super(RaidEntities.RAID_SPOT_ENTITY.get(), blockPos, blockState);
    }
}
