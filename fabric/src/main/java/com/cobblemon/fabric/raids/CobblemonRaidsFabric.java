package com.cobblemon.fabric.raids;

import com.cobblemon.common.raid.CobblemonRaids;
import com.cobblemon.mod.common.platform.events.ServerTickEvent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class CobblemonRaidsFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(CobblemonRaids::registerCommands);
        CobblemonRaids.register();

        ServerTickEvents.END_SERVER_TICK.register(CobblemonRaids::onServerTickEnd);
    }

}
