package com.cobblemon.common.raid;

import com.cobblemon.common.raid.blocks.RaidEntityRenderers;

public class CobblemonRaidsClient {
    public static void register() {
        RaidEntityRenderers.registerRenderers();
    }
}
