package com.cobblemon.common.raid;

import com.cobblemon.common.raid.blocks.RaidBlocks;
import com.cobblemon.common.raid.blocks.RaidEntities;
import com.cobblemon.common.raid.config.CobblemonRaidDimConfig;
import com.cobblemon.common.raid.config.CobblemonRaidsConfig;
import com.cobblemon.common.raid.datacomponents.RaidDataComponents;
import com.cobblemon.common.raid.datapack.DatapackRegister;
import com.cobblemon.common.raid.events.CobbleEvents;
import com.cobblemon.common.raid.items.RaidItems;
import com.cobblemon.common.raid.managers.DenManager;
import com.cobblemon.common.raid.managers.RaidBoss;
import com.cobblemon.common.raid.managers.RaidManager;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CobblemonRaids {
    public static final Logger LOGGER = LoggerFactory.getLogger("CobblemonRaids");
    public static final String MOD_ID = "cobblemon_raids";

    public static void register() {
        CobbleEvents.register();
        RaidBlocks.register();

        RaidItems.register();

        RaidEntities.register();
        CobblemonRaidsConfig.register();
        CobblemonRaidDimConfig.register();
        RaidDataComponents.register();
    }

    public static void registerCommon() {
        RaidItems.registerBalls();
    }

    public static void onServerTickEnd(MinecraftServer server) {
        RaidManager.tick(server);
        for (RaidBoss raid : RaidManager.getAllRaids()) {
            raid.tick(server);
        }
    }

    public static void onServerStarted(MinecraftServer server) {
        DenManager.load(server);
        DatapackRegister.register(server.registryAccess());
    }

    public static void onServerClosed(MinecraftServer server) {
        DenManager.unload();
    }
}
