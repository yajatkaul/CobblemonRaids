package com.cobblemon.fabric.raid;

import com.cobblemon.common.raid.CobblemonRaidsClient;
import net.fabricmc.api.ClientModInitializer;

public class CobblemonRaidsFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CobblemonRaidsClient.register();
    }
}
