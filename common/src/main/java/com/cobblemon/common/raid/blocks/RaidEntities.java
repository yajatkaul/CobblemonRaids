package com.cobblemon.common.raid.blocks;

import com.cobblemon.common.raid.CobblemonRaids;
import com.cobblemon.common.raid.blocks.custom.blockEntities.RaidSpotEntity;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class RaidEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(CobblemonRaids.MOD_ID, Registries.BLOCK_ENTITY_TYPE);

    public static final RegistrySupplier<BlockEntityType<RaidSpotEntity>> RAID_SPOT_ENTITY =
            BLOCK_ENTITIES.register("raid_spot", () ->
                    BlockEntityType.Builder.of(RaidSpotEntity::new, RaidBlocks.RAID_SPOT.get()).build(null)
            );

    public static void register() {
        BLOCK_ENTITIES.register();
    }
}
