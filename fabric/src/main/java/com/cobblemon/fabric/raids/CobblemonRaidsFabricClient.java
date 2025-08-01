package com.cobblemon.fabric.raids;

import com.cobblemon.common.raid.CobblemonRaidsClient;
import com.cobblemon.common.raid.blocks.RaidEntityRenderers;
import net.fabricmc.api.ClientModInitializer;

public class CobblemonRaidsFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CobblemonRaidsClient.register();
    }
}
