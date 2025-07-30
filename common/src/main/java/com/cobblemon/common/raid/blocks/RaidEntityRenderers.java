package com.cobblemon.common.raid.blocks;

import com.cobblemon.common.raid.blocks.custom.renderer.RaidSpotEntityRenderer;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;

public class RaidEntityRenderers {
    public static void registerRenderers() {
        BlockEntityRendererRegistry.register(RaidEntities.RAID_SPOT_ENTITY.get(), RaidSpotEntityRenderer::new);
    }
}
