package com.cobblemon.common.raid.blocks;

import com.cobblemon.common.raid.CobblemonRaids;
import com.cobblemon.common.raid.blocks.custom.blockEntities.RaidSpotEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class RaidEntities {
    public static final BlockEntityType<RaidSpotEntity> RAID_SPOT_ENTITY =
            Registry.register(
                    BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    ResourceLocation.fromNamespaceAndPath(CobblemonRaids.MOD_ID, "raid_spot"),
                    BlockEntityType.Builder.of(RaidSpotEntity::new, RaidBlocks.RAID_SPOT).build(null)
            );

    public static void register() {

    }
}
